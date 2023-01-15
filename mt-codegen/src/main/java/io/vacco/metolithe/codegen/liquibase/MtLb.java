package io.vacco.metolithe.codegen.liquibase;

import io.vacco.metolithe.annotations.*;
import io.vacco.metolithe.codegen.liquibase.type.*;
import io.vacco.metolithe.core.*;
import io.vacco.oriax.alg.OxKos;
import io.vacco.oriax.core.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

import static io.vacco.metolithe.core.MtTypeMapper.sqlTypeOf;
import static io.vacco.metolithe.core.MtUtil.toStringConcat;
import static io.vacco.metolithe.hashing.MtMurmur3.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;

public class MtLb {

  public interface Consumer3<V0, V1, V2> {
    void accept(V0 v0, V1 v, V2 v2);
  }

  public interface Consumer4<V0, V1, V2, V3> {
    void accept(V0 v0, V1 v, V2 v2, V3 v3);
  }

  private Optional<Constraints> mapConstraints(MtFieldDescriptor d) {
    var nn = d.get(MtNotNull.class);
    var pk = d.get(MtPk.class);
    if (nn.isPresent() || pk.isPresent()) {
      var cn = new Constraints();
      cn.nullable = false;
      if (pk.isPresent()) {
        cn.primaryKey = true;
      }
      return Optional.of(cn);
    }
    return Optional.empty();
  }

  private Column mapAttribute(MtFieldDescriptor d) {
    var c = new Column();
    c.name = d.getFieldName();
    c.type = d.getFormat().of(sqlTypeOf(d));
    mapConstraints(d).ifPresent(cn -> c.constraints = cn);
    return c;
  }

  private CreateIndex mapIndex(MtDescriptor<?> d, MtFieldDescriptor fm) {
    var idx = new CreateIndex();
    idx.indexName = d.getFormat().of(format("idx_%s_%s", d.getName(), fm.getFieldName()));
    idx.tableName = d.getName();
    idx.columns.add(new Column().withName(fm.getFieldName()));
    return idx;
  }

  private CreateIndex mapCompositeIndex(String indexName, MtDescriptor<?> d, List<MtFieldDescriptor> components) {
    var fields = components.stream()
      .sorted(comparingInt(fd -> fd.get(MtCompIndex.class).get().idx()))
      .map(MtFieldDescriptor::getFieldName).toArray();
    var indexId = format("idx_%s_%s", indexName, Integer.toHexString(hash32(toStringConcat(fields).get(), DEFAULT_SEED)));
    var idx = new CreateIndex()
      .withIndexName(d.getFormat().of(indexId))
      .withTableName(d.getName());
    for (var fd : components) {
      idx.columns.add(new Column().withName(fd.getFieldName()));
    }
    return idx;
  }

  private AddUniqueConstraint mapUniqueConstraints(MtDescriptor<?> d) {
    var uc = new AddUniqueConstraint();
    uc.tableName = d.getName();
    uc.constraintName = d.getFormat().of(format("unq_%s", d.getName()));
    uc.columnNames = d.get(MtUnique.class)
      .sorted(comparingInt(fd -> fd.get(MtUnique.class).get().idx()))
      .map(MtFieldDescriptor::getFieldName).collect(joining(","));
    return uc;
  }

  private AddForeignKeyConstraint mapForeignKey(MtDescriptor<?> d, MtFieldDescriptor fd) {
    var fk = fd.get(MtFk.class).get();
    var fkTarget = new MtDescriptor<>(fk.value(), fd.getFormat());
    var targetPk = fkTarget.get(MtPk.class).findFirst().get();

    if (!fd.getType().equals(targetPk.getType())) {
      throw new MtException.MtForeignKeyMismatchException(
        d.getName(), fd.getFieldName(), fd.getType().getTypeName(),
        fkTarget.getName(), targetPk.getFieldName(), targetPk.getType().getTypeName()
      );
    }

    var from = d.getName();
    var fromField = fd.getFieldName();
    var to = fkTarget.getName();
    var toField = targetPk.getFieldName();
    var fkId = format("fk_%s", Integer.toHexString(hash32(toStringConcat(from, fromField, to, toField).get(), DEFAULT_SEED)));

    var fkc = new AddForeignKeyConstraint();
    fkc.baseColumnNames = fromField;
    fkc.baseTableName = from;
    fkc.constraintName = d.getFormat().of(fkId);
    fkc.referencedColumnNames = toField;
    fkc.referencedTableName = to;

    return fkc;
  }

  private List<ChangeSet> mapForeignKeys(List<OxVtx<String, MtDescriptor<?>>> classGroup) {
    return classGroup.stream()
      .map(v -> v.data)
      .flatMap(d -> d.get(MtFk.class).map(fd -> mapForeignKey(d, fd)))
      .map(fkc -> new ChangeSet().withId(fkc.constraintName).add(fkc))
      .collect(toList());
  }

  private ChangeSet mapTable(MtDescriptor<?> d) {
    var cs = new ChangeSet().withId(d.getName());
    var ct = new CreateTable().withTableName(d.getName());
    cs.changes.add(ct);
    asList(MtPk.class, MtFk.class, MtField.class, MtVarchar.class)
      .forEach(cl -> d.get(cl).map(this::mapAttribute).forEach(col -> ct.columns.add(col)));
    d.get(MtUnique.class).findFirst().ifPresent(fd -> cs.changes.add(mapUniqueConstraints(d)));
    d.get(MtIndex.class).map(fd -> mapIndex(d, fd)).forEach(idx -> cs.changes.add(idx));
    d.getCompositeIndexes().forEach((k, v) -> cs.changes.add(mapCompositeIndex(k, d, v)));
    return cs;
  }

  public Root build(MtCaseFormat fmt, Class<?> ... schemaClasses) {
    var descriptors = Arrays.stream(schemaClasses)
      .map(clazz -> new MtDescriptor<>(clazz, fmt))
      .map(fd -> new OxVtx<String, MtDescriptor<?>>(fd.getName(), fd))
      .collect(Collectors.toList());
    var schema = new OxGrph<String, MtDescriptor<?>>();

    for (var vd : descriptors) {
      vd.data.getFields(true).stream()
        .map(fd -> fd.get(MtFk.class))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(fk -> descriptors.stream()
          .filter(v -> v.data.matches(fk.value()))
          .findFirst().ifPresent(v0 -> schema.addEdge(vd, v0))
        );
    }

    var root = new Root();
    OxKos.apply(schema).forEach((k, v) -> {
      v.stream().map(v0 -> mapTable(v0.data)).forEach(root::append);
      mapForeignKeys(v).forEach(root::append);
    });

    return root;
  }

  public static <T> T map(MtLbType t,
                          Function<MtLbType, T> rootFn,
                          Consumer3<MtLbType, T, T> onObj,
                          Consumer3<T, String, String> onAttr,
                          Consumer4<MtLbType, Field, T, T> onListItem) {
    try {
      var m0 = rootFn.apply(t);
      for (var f : t.getClass().getFields()) {
        var v = f.get(t);
        if (v != null) {
          if (!(v instanceof Collection)) {
            if (v instanceof Constraints) {
              var t0 = (MtLbType) v;
              T m1 = map(t0, rootFn, onObj, onAttr, onListItem);
              onObj.accept(t0, m0, m1);
            } else {
              onAttr.accept(m0, f.getName(), v.toString());
            }
          } else {
            for (var o : (Collection<?>) v) {
              var t0 = (MtLbType) o;
              var m1 = map(t0, rootFn, onObj, onAttr, onListItem);
              onListItem.accept(t0, f, m0, m1);
            }
          }
        }
      }
      return m0;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

}
