package io.vacco.metolithe.codegen.liquibase;

import io.vacco.metolithe.annotations.*;
import org.joox.Match;
import org.slf4j.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

import static java.lang.String.*;
import static java.util.Objects.*;
import static org.joox.JOOX.$;

public class XmlMapper {

  private static final Logger log = LoggerFactory.getLogger(XmlMapper.class);

  public static Match mapEntity(Class<?> entity, Set<Field> attributes) {
    try {
      requireNonNull(entity);
      requireNonNull(attributes);
      URL xmlTemplate = requireNonNull(XmlMapper.class.getClassLoader().getResource("io/vacco/metolithe/codegen/liquibase/changelog-template.xml"));
      String entityName = toSnakeCase(entity.getSimpleName());
      Match lb = $(xmlTemplate);
      Match cs = $("changeSet").attr("author", "generated").attr("id", entityName);
      Match ct = $("createTable").attr("tableName", entityName);
      attributes.stream().map(XmlMapper::mapAttribute).forEach(ct::append);
      cs.append(ct);
      attributes.stream().map(XmlMapper::mapIndex).filter(Objects::nonNull).forEach(cs::append);
      lb.append(cs);
      return lb;
    } catch (Exception e) {
      log.error("Unable to map entity [{}]", entity);
      log.error(e.getMessage(), e);
      throw new IllegalStateException(e);
    }
  }

  private static Match mapAttribute(Field target) {
    requireNonNull(target);
    Match columnXml = $("column")
        .attr("name", target.getName().toLowerCase())
        .attr("type", TypeMapper.resolveSqlType(target.getType(), target.getDeclaredAnnotations()));
    Optional<MtId> pk = hasPrimaryKey(target.getDeclaredAnnotations());
    Optional<MtAttribute> nn = isNotNull(target.getDeclaredAnnotations());
    Match cn = (pk.isPresent() || nn.isPresent()) ? $("constraints") : null;
    if (pk.isPresent()) { cn.attr("primaryKey", "true"); }
    if (nn.isPresent()) { cn.attr("nullable", "false"); }
    if (cn != null) { columnXml.append(cn); }
    return columnXml;
  }

  private static Match mapIndex(Field target) {
    requireNonNull(target);
    Optional<MtIndex> isIndex = isIndex(target.getDeclaredAnnotations());
    String entityName = toSnakeCase(target.getDeclaringClass().getSimpleName());
    if (isIndex.isPresent()) {
      Match idx = $("createIndex")
          .attr("indexName", format("%s_%s_idx", entityName, target.getName().toLowerCase()))
          .attr("tableName", entityName);
      idx.append($("column").attr("name", target.getName().toLowerCase()));
      return idx;
    }
    return null;
  }

  private static Optional<MtId> hasPrimaryKey(Annotation ... annotations) {
    return Arrays.stream(annotations)
        .filter(an0 -> an0.annotationType() == MtId.class)
        .map(an0 -> (MtId) an0).findFirst();
  }

  private static Optional<MtAttribute> isNotNull(Annotation ... annotations) {
    return Arrays.stream(annotations)
        .filter(an0 -> an0.annotationType() == MtAttribute.class)
        .map(an0 -> (MtAttribute) an0)
        .filter(nn0 -> !nn0.nil())
        .findFirst();
  }

  private static Optional<MtIndex> isIndex(Annotation ... annotations) {
    return Arrays.stream(annotations)
        .filter(an0 -> an0.annotationType() == MtIndex.class)
        .map(an0 -> (MtIndex) an0).findFirst();
  }

  private static String toSnakeCase(String in) {
    return requireNonNull(in).replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
  }
}
