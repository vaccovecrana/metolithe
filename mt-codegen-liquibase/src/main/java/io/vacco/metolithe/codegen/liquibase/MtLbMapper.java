package io.vacco.metolithe.codegen.liquibase;

import io.vacco.metolithe.annotations.*;
import io.vacco.metolithe.core.*;
import io.vacco.oriax.alg.OxKos;
import io.vacco.oriax.core.*;
import org.joox.Match;
import org.xml.sax.SAXException;

import java.io.IOException;
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
        .attr("name", d.getFieldName())
        .attr("type", d.getFormat().of(sqlTypeOf(d)));
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
        format("%s_%s_idx", d.getName(), fm.getFieldName()),
        d.getName()
    );
    idx.append(column(fm.getFieldName()));
    return idx;
  }

  private Match mapCompIndex(MtDescriptor<?> d, List<MtFieldDescriptor> components) {
    Object[] fields = components.stream()
        .sorted(comparingInt(fd -> fd.get(MtCompIndex.class).get().idx()))
        .map(MtFieldDescriptor::getFieldName).toArray();
    String indexId = Integer.toHexString(hash32(toStringConcat(fields).get(), DEFAULT_SEED));
    Match idx = createIndex(indexId, d.getName());
    for (MtFieldDescriptor fd : components) {
      idx.append(column(fd.getFieldName()));
    }
    return idx;
  }

  private Match mapUniqueConstraints(MtDescriptor<?> d) {
    return $("addUniqueConstraint")
        .attr("tableName", d.getName())
        .attr("constraintName", d.getFormat().of(format("%s_unq", d.getName())))
        .attr("columnNames", d.get(MtUnique.class)
            .sorted(comparingInt(fd -> fd.get(MtUnique.class).get().idx()))
            .map(MtFieldDescriptor::getFieldName).collect(joining(",")));
  }

  private Match changeSet(String id) {
    return $("changeSet").attr("author", "generated").attr("id", id);
  }

  public List<Match> mapForeignKeys(List<OxVtx<MtDescriptor<?>>> classGroup) {
    return classGroup.stream().map(v -> v.data).flatMap(d -> d.get(MtFk.class)
        .map(fd -> {
          MtFk fk = fd.get(MtFk.class).get();
          MtDescriptor<?> fkTarget = new MtDescriptor<>(fk.value(), fd.getFormat());
          MtFieldDescriptor targetPk = fkTarget.get(MtPk.class).findFirst().get();
          if (!fd.getFieldType().equals(targetPk.getFieldType())) {
            throw new MtException.MtForeignKeyMismatchException(
                d.getName(), fd.getFieldName(), fd.getFieldType().getTypeName(),
                fkTarget.getName(), targetPk.getFieldName(), targetPk.getFieldType().getTypeName()
            );
          }
          String from = d.getName();
          String fromField = fd.getFieldName();
          String to = fkTarget.getName();
          String toField = targetPk.getFieldName();
          String fkId = Integer.toHexString(hash32(toStringConcat(from, fromField, to, toField).get(), DEFAULT_SEED));
          Match cs = changeSet(fkId);
          cs.append(
              $("addForeignKeyConstraint")
                  .attr("baseColumnNames", fromField)
                  .attr("baseTableName", from)
                  .attr("constraintName", d.getFormat().of(fkId))
                  .attr("referencedColumnNames", toField)
                  .attr("referencedTableName", to)
          );
          return cs;
        })).collect(toList());
  }

  public Match mapClass(MtDescriptor<?> d) {
    Match cs = changeSet(d.getName());
    Match ct = $("createTable").attr("tableName", d.getName());
    asList(MtPk.class, MtFk.class, MtField.class, MtVarchar.class)
        .forEach(cl -> d.get(cl).map(this::mapAttribute).forEach(ct::append));
    cs.append(ct);
    d.get(MtUnique.class).findFirst().ifPresent(fd -> cs.append(mapUniqueConstraints(d)));
    d.get(MtIndex.class).map(fd -> mapIndex(d, fd)).forEach(cs::append);
    d.getCompositeIndexes().forEach((k, v) -> cs.append(mapCompIndex(d, v)));
    return cs;
  }

  public Match mapSchema(MtCaseFormat fmt, Class<?> ... schemaClasses) throws IOException, SAXException {
    List<OxVtx<MtDescriptor<?>>> descriptors = Arrays.stream(schemaClasses)
        .map(clazz -> new MtDescriptor<>(clazz, fmt))
        .map(fd -> new OxVtx<MtDescriptor<?>>(fd.getName(), fd))
        .collect(Collectors.toList());

    OxGrph<MtDescriptor<?>> schema = new OxGrph<>();
    for (OxVtx<MtDescriptor<?>> vd : descriptors) {
      vd.data.getFields(true).stream()
          .map(fd -> fd.get(MtFk.class))
          .filter(Optional::isPresent).map(Optional::get)
          .forEach(fk -> descriptors.stream()
              .filter(v -> v.data.matches(fk.value()))
              .findFirst().ifPresent(v0 -> schema.addEdge(vd, v0))
          );
    }

    URL xmlTemplate = MtLbMapper.class.getClassLoader().getResource("io/vacco/metolithe/codegen/liquibase/changelog-template.xml");
    Match lb = $(xmlTemplate);
    OxKos.apply(schema).forEach((k, v) -> {
      v.stream().map(v0 -> mapClass(v0.data)).forEach(lb::append);
      mapForeignKeys(v).forEach(lb::append);
    });
    return lb;
  }
}
