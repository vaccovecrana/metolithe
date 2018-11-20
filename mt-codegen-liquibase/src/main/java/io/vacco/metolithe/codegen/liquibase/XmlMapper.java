package io.vacco.metolithe.codegen.liquibase;

import io.vacco.metolithe.annotations.*;
import io.vacco.metolithe.extraction.*;
import org.joox.Match;
import org.slf4j.*;
import java.net.URL;
import java.util.*;

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
      String entityName = toSnakeCase(em.getTarget().getSimpleName());
      Match lb = $(xmlTemplate);
      Match cs = $("changeSet").attr("author", "generated").attr("id", entityName);
      Match ct = $("createTable").attr("tableName", entityName);
      em.rawFields().map(fld0 -> XmlMapper.mapAttribute(em.getTarget(), fld0, tm)).forEach(ct::append);
      cs.append(ct);
      em.rawFields().map(fld0 -> XmlMapper.mapIndex(em.getTarget(), fld0)).filter(Objects::nonNull).forEach(cs::append);
      lb.append(cs);
      return lb;
    } catch (Exception e) {
      log.error("Unable to map entity [{}]", em.getTarget());
      log.error(e.getMessage(), e);
      throw new IllegalStateException(e);
    }
  }

  private static Match mapAttribute(Class<?> root, FieldMetadata fm, TypeMapper tm) {
    requireNonNull(fm);
    requireNonNull(tm);
    Match columnXml = $("column")
        .attr("name", fm.field.getName().toLowerCase())
        .attr("type", tm.resolveSqlType(fm));
    Optional<MtId> oid = fm.hasPrimaryKeyOf(root);
    Optional<MtAttribute> nn = fm.hasNotNull();
    boolean isTargetPk = oid.isPresent();
    Match cn = (isTargetPk || nn.isPresent()) ? $("constraints") : null;
    if (cn != null) {
      if (isTargetPk) { cn.attr("primaryKey", "true"); }
      if (nn.isPresent()) { cn.attr("nullable", "false"); }
      columnXml.append(cn);
    }
    return columnXml;
  }

  private static Match mapIndex(Class<?> root, FieldMetadata fm) {
    requireNonNull(fm);
    if (fm.hasIndexOf(root).isPresent()) {
      String entityName = toSnakeCase(root.getSimpleName());
      Match idx = $("createIndex")
          .attr("indexName", format("%s_%s_idx", entityName, fm.field.getName().toLowerCase()))
          .attr("tableName", entityName);
      idx.append($("column").attr("name", fm.field.getName().toLowerCase()));
      return idx;
    }
    return null;
  }

  private static String toSnakeCase(String in) {
    return requireNonNull(in).replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
  }
}
