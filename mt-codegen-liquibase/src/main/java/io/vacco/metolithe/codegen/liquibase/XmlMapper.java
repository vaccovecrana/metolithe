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
      Class<?> root = em.getTarget();
      Match lb = $(xmlTemplate);
      Match cs = $("changeSet").attr("author", "generated").attr("id", em.getName());
      Match ct = $("createTable").attr("tableName", em.getName());
      em.rawFields().map(fld0 -> XmlMapper.mapAttribute(fld0, tm)).forEach(ct::append);
      cs.append(ct);
      Optional<Match> pka = mapPrimaryKeyAttributes(root, em);
      pka.ifPresent(cs::append);
      em.rawFields().map(fld0 -> XmlMapper.mapIndex(em, fld0)).filter(Objects::nonNull).forEach(cs::append);
      lb.append(cs);
      return lb;
    } catch (Exception e) {
      log.error("Unable to map entity [{}]", em.getTarget());
      log.error(e.getMessage(), e);
      throw new IllegalStateException(e);
    }
  }

  private static Optional<Match> mapPrimaryKeyAttributes(Class<?> root, EntityMetadata em) {
    Collection<FieldMetadata> pkFields = em.rawFields().filter(fm -> fm.hasPrimaryKeyOf(root).isPresent())
        .collect(Collectors.toList());
    Collection<FieldMetadata> pkPartFields = em.rawFields().filter(fm -> fm.hasPrimaryKeyPart().isPresent())
        .collect(Collectors.toList());
    Collection<FieldMetadata> pkAll = new ArrayList<>(pkFields);
    pkAll.addAll(pkPartFields);
    if (pkFields.size() > 1) {
      String err = format("Class [%s] defines more than one target primary key field: %s", root, pkFields);
      throw new IllegalStateException(err);
    }
    if (!pkAll.isEmpty()) {
      Match apk = $("addPrimaryKey")
          .attr("tableName", em.getName())
          .attr("columnNames", pkFields.stream()
          .map(pk -> pk.field.getName()).collect(Collectors.joining(", ")));
      return Optional.of(apk);
    }
    return Optional.empty();
  }

  private static Match mapAttribute(FieldMetadata fm, TypeMapper tm) {
    requireNonNull(fm);
    requireNonNull(tm);
    Match columnXml = $("column")
        .attr("name", fm.field.getName().toLowerCase())
        .attr("type", tm.resolveSqlType(fm));
    Optional<MtAttribute> nn = fm.hasNotNull();
    Optional<MtCollection> oc = fm.hasCollection();
    Optional<MtId> pk = fm.hasPrimaryKey();
    Optional<MtIdPart> pkp = fm.hasPrimaryKeyPart();
    Match cn = (nn.isPresent() || oc.isPresent() || pk.isPresent() || pkp.isPresent()) ? $("constraints") : null;
    if (cn != null) {
      cn.attr("nullable", "false");
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
}
