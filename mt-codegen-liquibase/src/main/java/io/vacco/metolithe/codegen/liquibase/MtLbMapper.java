package io.vacco.metolithe.codegen.liquibase;

import io.vacco.metolithe.annotations.*;
import io.vacco.metolithe.core.*;
import io.vacco.oriax.alg.OxKos;
import io.vacco.oriax.core.*;
import org.joox.Match;
import org.slf4j.*;

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

  private static final Logger log = LoggerFactory.getLogger(MtLbMapper.class);

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
        .attr("columnNames", d.get(MtUnique.class).stream()
            .sorted(comparingInt(fd -> fd.get(MtUnique.class).get().idx()))
            .map(fd -> fd.getField().getName()).collect(joining(",")));
  }

  private Match changeSet(String id) {
    return $("changeSet").attr("author", "generated").attr("id", id);
  }

  public List<Match> mapForeignKeys(List<OxVtx<MtDescriptor<?>>> classGroup) {
    return classGroup.stream().map(v -> v.data).flatMap(d -> d.get(MtFk.class).stream()
        .map(fd -> {
          String field = fd.getField().getName();
          String from = d.getTarget().getSimpleName();
          String to = fd.get(MtFk.class).get().value().getSimpleName();
          Match cs = changeSet(Integer.toHexString(hash32(toStringConcat(from, to, field).get(), DEFAULT_SEED)));
          cs.append(
              $("addForeignKeyConstraint")
                  .attr("baseColumnNames", field)
                  .attr("baseTableName", from)
                  .attr("constraintName", format("fk_%s_%s_%s", from, to, field))
                  .attr("referencedColumnNames", field)
                  .attr("referencedTableName", to)
          );
          return cs;
        })).collect(toList());
  }

  public Match mapClass(MtDescriptor<?> d) {
    Match cs = changeSet(d.getTarget().getSimpleName());
    Match ct = $("createTable").attr("tableName", d.getTarget().getSimpleName());
    asList(MtPk.class, MtFk.class, MtField.class, MtVarchar.class)
        .forEach(cl -> d.get(cl).stream().map(this::mapAttribute).forEach(ct::append));
    cs.append(ct);
    if (!d.get(MtUnique.class).isEmpty()) {
      cs.append(mapUniqueConstraints(d));
    }
    d.get(MtIndex.class).stream().map(fd -> mapIndex(d, fd)).forEach(cs::append);
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
      log.error("Unable to map entity classes [{}]", Arrays.toString(schemaClasses));
      log.error(e.getMessage(), e);
      throw new IllegalStateException(e);
    }
  }
}
