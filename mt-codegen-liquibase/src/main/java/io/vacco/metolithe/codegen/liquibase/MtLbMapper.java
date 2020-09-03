package io.vacco.metolithe.codegen.liquibase;

import io.vacco.metolithe.annotations.*;
import io.vacco.metolithe.core.*;
import io.vacco.oriax.alg.OxKos;
import io.vacco.oriax.core.*;
import org.joox.Match;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.*;
import static org.joox.JOOX.*;
import static io.vacco.metolithe.core.MtTypeMapper.*;
import static io.vacco.metolithe.hashing.MtMurmur3.*;
import static io.vacco.metolithe.hashing.MtArrays.*;
import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;
import static java.util.Arrays.*;

public class MtLbMapper {

  private Match mapAttribute(MtFieldDescriptor d) {
    Match columnXml = $("column")
        .attr("name", d.getField().getName())
        .attr("type", sqlTypeOf(d));
    Optional<MtNotNull> nn = d.get(MtNotNull.class);
    Optional<MtPk> pk = d.get(MtPk.class);
    Match cn = (nn.isPresent() || pk.isPresent()) ? $("constraints") : null;
    if (cn != null) {
      cn.attr("nullable", "false");
      if (pk.isPresent()) {
        cn.attr("primaryKey", "true");
      }
      columnXml.append(cn);
    }
    return columnXml;
  }

  private Match createIndex(String indexName, String tableName) {
    return $("createIndex").attr("indexName", indexName).attr("tableName", tableName);
  }

  private Match column(String name) {
    return $("column").attr("name", name);
  }

  private Match mapIndex(MtDescriptor<?> d, MtFieldDescriptor fm) {
    Match idx = createIndex(
        format("%s_%s_idx", d.getTarget().getSimpleName(), fm.getField().getName()),
        d.getTarget().getSimpleName()
    );
    idx.append(column(fm.getField().getName()));
    return idx;
  }

  private Match mapCompIndex(MtDescriptor<?> d, List<MtFieldDescriptor> components) {
    Object[] fields = components.stream()
        .sorted(comparingInt(fd -> fd.get(MtCompIndex.class).get().idx()))
        .map(fd -> fd.getField().getName()).toArray();
    String indexId = Integer.toHexString(hash32(toStringConcat(fields).get(), DEFAULT_SEED));
    Match idx = createIndex(indexId, d.getTarget().getSimpleName());
    for (MtFieldDescriptor fd : components) {
      idx.append(column(fd.getField().getName())); // TODO case format?
    }
    return idx;
  }

  private Match mapUniqueConstraints(MtDescriptor<?> d) {
    return $("addUniqueConstraint")
        .attr("tableName", d.getTarget().getSimpleName())
        .attr("constraintName", format("%s_unq", d.getTarget().getSimpleName()))
        .attr("columnNames", d.get(MtUnique.class)
            .sorted(comparingInt(fd -> fd.get(MtUnique.class).get().idx()))
            .map(fd -> fd.getField().getName()).collect(joining(",")));
  }

  private Match changeSet(String id) {
    return $("changeSet").attr("author", "generated").attr("id", id);
  }

  public List<Match> mapForeignKeys(List<OxVtx<MtDescriptor<?>>> classGroup) {
    return classGroup.stream().map(v -> v.data).flatMap(d -> d.get(MtFk.class)
        .map(fd -> {
          MtFk fk = fd.get(MtFk.class).get();
          MtDescriptor<?> fkTarget = new MtDescriptor<>(fk.value());
          MtFieldDescriptor targetPk = fkTarget.get(MtPk.class).findFirst().get();
          if (!fd.getField().getType().equals(targetPk.getField().getType())) {
            throw new IllegalArgumentException(format(
                "Foreign key type mismatch: [%s:%s:%s] -> [%s:%s:%s]",
                d.getTarget().getSimpleName(), fd.getField().getName(), fd.getField().getType(),
                fkTarget.getTarget().getSimpleName(), targetPk.getField().getName(), targetPk.getField().getType()
            ));
          }
          String from = d.getTarget().getSimpleName();
          String fromField = fd.getField().getName();
          String to = fkTarget.getTarget().getSimpleName();
          String toField = targetPk.getField().getName();
          String fkId = Integer.toHexString(hash32(toStringConcat(from, fromField, to, toField).get(), DEFAULT_SEED));
          Match cs = changeSet(fkId);
          cs.append(
              $("addForeignKeyConstraint")
                  .attr("baseColumnNames", fromField)
                  .attr("baseTableName", from)
                  .attr("constraintName", fkId)
                  .attr("referencedColumnNames", toField)
                  .attr("referencedTableName", to)
          );
          return cs;
        })).collect(toList());
  }

  public Match mapClass(MtDescriptor<?> d) {
    Match cs = changeSet(d.getTarget().getSimpleName());
    Match ct = $("createTable").attr("tableName", d.getTarget().getSimpleName());
    asList(MtPk.class, MtFk.class, MtField.class, MtVarchar.class)
        .forEach(cl -> d.get(cl).map(this::mapAttribute).forEach(ct::append));
    cs.append(ct);
    d.get(MtUnique.class).findFirst().ifPresent(fd -> cs.append(mapUniqueConstraints(d)));
    d.get(MtIndex.class).map(fd -> mapIndex(d, fd)).forEach(cs::append);
    d.getCompositeIndexes().forEach((k, v) -> cs.append(mapCompIndex(d, v)));
    return cs;
  }

  public Match mapSchema(Class<?> ... schemaClasses) {
    List<OxVtx<MtDescriptor<?>>> descriptors = Arrays.stream(schemaClasses)
        .map(MtDescriptor::new)
        .map(fd -> new OxVtx<MtDescriptor<?>>(fd.getTarget().getSimpleName(), fd))
        .collect(Collectors.toList());

    OxGrph<MtDescriptor<?>> schema = new OxGrph<>();
    for (OxVtx<MtDescriptor<?>> vd : descriptors) {
      vd.data.getFields(true).stream()
          .map(fd -> fd.get(MtFk.class))
          .filter(Optional::isPresent).map(Optional::get)
          .forEach(fk -> descriptors.stream()
              .filter(d -> d.data.getTarget() == fk.value())
              .findFirst().ifPresent(v0 -> schema.addEdge(vd, v0))
          );
    }

    try {
      URL xmlTemplate = MtLbMapper.class.getClassLoader().getResource("io/vacco/metolithe/codegen/liquibase/changelog-template.xml");
      Match lb = $(xmlTemplate);
      OxKos.apply(schema).forEach((k, v) -> {
        v.stream().map(v0 -> mapClass(v0.data)).forEach(lb::append);
        mapForeignKeys(v).forEach(lb::append);
      });
      return lb;
    } catch (Exception e) {
      String msg = format("Unable to map entity classes [%s]", Arrays.toString(schemaClasses));
      throw new IllegalStateException(msg, e);
    }
  }
}
