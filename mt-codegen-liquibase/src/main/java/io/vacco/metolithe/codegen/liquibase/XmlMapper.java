package io.vacco.metolithe.codegen.liquibase;

import io.vacco.metolithe.annotations.*;
import io.vacco.metolithe.extraction.AnnotationExtractor;
import io.vacco.metolithe.extraction.TypeMapper;
import org.joox.Match;
import org.slf4j.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

import static java.lang.String.*;
import static java.util.Objects.*;
import static org.joox.JOOX.$;
import static io.vacco.metolithe.extraction.FieldFilter.*;

public class XmlMapper {

  private static final Logger log = LoggerFactory.getLogger(XmlMapper.class);

  public static Match mapEntity(Class<?> entity, Collection<Field> attributes) {
    try {
      requireNonNull(entity);
      requireNonNull(attributes);
      URL xmlTemplate = requireNonNull(XmlMapper.class.getClassLoader().getResource("io/vacco/metolithe/codegen/liquibase/changelog-template.xml"));
      String entityName = toSnakeCase(entity.getSimpleName());
      Match lb = $(xmlTemplate);
      Match cs = $("changeSet").attr("author", "generated").attr("id", entityName);
      Match ct = $("createTable").attr("tableName", entityName);
      attributes.stream().map(fld0 -> XmlMapper.mapAttribute(entity, fld0)).forEach(ct::append);
      cs.append(ct);
      attributes.stream().map(fld0 -> XmlMapper.mapIndex(entity, fld0)).filter(Objects::nonNull).forEach(cs::append);
      lb.append(cs);
      return lb;
    } catch (Exception e) {
      log.error("Unable to map entity [{}]", entity);
      log.error(e.getMessage(), e);
      throw new IllegalStateException(e);
    }
  }

  private static Match mapAttribute(Class<?> root, Field target) {
    requireNonNull(target);
    Match columnXml = $("column")
        .attr("name", target.getName().toLowerCase())
        .attr("type", TypeMapper.resolveSqlType(target.getType(), AnnotationExtractor.asArray(target)));
    boolean pk = isOwnPrimaryKey(root, target);
    Optional<MtAttribute> nn = hasNotNull(target);
    Match cn = (pk || nn.isPresent()) ? $("constraints") : null;
    if (pk) { cn.attr("primaryKey", "true"); }
    if (nn.isPresent()) { cn.attr("nullable", "false"); }
    if (cn != null) { columnXml.append(cn); }
    return columnXml;
  }

  private static Match mapIndex(Class<?> root, Field target) {
    requireNonNull(target);
    if (isOwnIndex(root, target)) {
      String entityName = toSnakeCase(root.getSimpleName());
      Match idx = $("createIndex")
          .attr("indexName", format("%s_%s_idx", entityName, target.getName().toLowerCase()))
          .attr("tableName", entityName);
      idx.append($("column").attr("name", target.getName().toLowerCase()));
      return idx;
    }
    return null;
  }

  private static String toSnakeCase(String in) {
    return requireNonNull(in).replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
  }
}
