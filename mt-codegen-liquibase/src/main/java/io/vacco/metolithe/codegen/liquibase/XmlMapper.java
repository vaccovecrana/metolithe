package io.vacco.metolithe.codegen.liquibase;

import io.vacco.metolithe.annotations.*;
import io.vacco.metolithe.extraction.*;
import org.joox.Match;
import org.slf4j.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.*;
import static java.util.Objects.*;
import static org.joox.JOOX.$;

public class XmlMapper {

  private static final Logger log = LoggerFactory.getLogger(XmlMapper.class);

  public static Match mapEntity(EntityMetadata em, TypeMapper tm) {
    try {
      requireNonNull(em);
      requireNonNull(tm);
      URL xmlTemplate = requireNonNull(XmlMapper.class.getClassLoader().getResource("io/vacco/metolithe/codegen/liquibase/changelog-template.xml"));
      Match lb = $(xmlTemplate);
      Match cs = $("changeSet").attr("author", "generated").attr("id", em.getName());
      Match ct = $("createTable").attr("tableName", em.getName());
      Match uq = mapUniqueConstraints(em);
      em.rawFields().map(fld0 -> XmlMapper.mapAttribute(em, fld0, tm)).forEach(ct::append);
      cs.append(ct);
      em.rawFields().map(fld0 -> XmlMapper.mapIndex(em, fld0)).filter(Objects::nonNull).forEach(cs::append);
      if (uq != null) { cs.append(uq); }
      lb.append(cs);
      return lb;
    } catch (Exception e) {
      log.error("Unable to map entity [{}]", em.getTarget());
      log.error(e.getMessage(), e);
      throw new IllegalStateException(e);
    }
  }

  private static Match mapAttribute(EntityMetadata em, FieldMetadata fm, TypeMapper tm) {
    requireNonNull(fm);
    requireNonNull(tm);
    Match columnXml = $("column")
        .attr("name", fm.field.getName().toLowerCase())
        .attr("type", tm.resolveSqlType(fm));
    Optional<MtAttribute> nn = fm.hasNotNull();
    Optional<MtId> pk = fm.hasPrimaryKeyOf(em.getTarget());
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

  private static Match mapIndex(EntityMetadata em, FieldMetadata fm) {
    requireNonNull(fm);
    if (fm.hasIndexOf(em.getTarget()).isPresent()) {
      Match idx = $("createIndex")
          .attr("indexName", format("%s_%s_idx", em.getName(), fm.field.getName().toLowerCase()))
          .attr("tableName", em.getName());
      idx.append($("column").attr("name", fm.field.getName().toLowerCase()));
      return idx;
    }
    return null;
  }

  private static Match mapUniqueConstraints(EntityMetadata em) {
    requireNonNull(em);
    List<FieldMetadata> idGroupFields = em.rawFields()
        .filter(fm -> fm.hasIdGroup().isPresent())
        .collect(Collectors.toList());
    if (!idGroupFields.isEmpty()) {
      return $("addUniqueConstraint")
          .attr("tableName", em.getName())
          .attr("constraintName", format("%s_unique", em.getName()))
          .attr("columnNames", idGroupFields.stream()
              .map(fm -> fm.field.getName().toLowerCase())
              .collect(Collectors.joining(",")));
    }
    return null;
  }
}
