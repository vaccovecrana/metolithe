package io.vacco.metolithe.changeset;

import io.marioslab.basis.template.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static io.vacco.metolithe.changeset.MtChange.change;
import static io.vacco.metolithe.id.MtMurmur3.DEFAULT_SEED;
import static io.vacco.metolithe.id.MtMurmur3.hash32;
import static java.lang.String.format;

public class MtLogMapper {

  private final TemplateLoader loader = new TemplateLoader.ClasspathTemplateLoader();
  private final BiFunction<String, List<String>, String> join = String::join;
  private final String schema;

  public MtLogMapper(String schema) {
    this.schema = schema;
  }

  private String removeBlankLines(String v) {
    return Arrays.stream(v.split("\n"))
      .filter(line -> !line.trim().isEmpty())
      .collect(Collectors.joining("\n"));
  }

  private String schemaPrefix() {
    return schema == null ? "" : format("%s.", schema);
  }

  private MtChange generateFullTable(MtTable table) {
    var template = loader.load("/io/vacco/metolithe/codegen/MtFullTable.bt");
    var ctx = new TemplateContext();
    ctx.set("schemaPrefix", schemaPrefix());
    ctx.set("table", table);
    ctx.set("join", join);
    return change(
      format("tbl_%s", table.name),
      removeBlankLines(template.render(ctx))
    );
  }

  private MtChange generateEmptyTable(MtTable table) {
    var template = loader.load("/io/vacco/metolithe/codegen/MtEmptyTable.bt");
    var ctx = new TemplateContext();
    ctx.set("schemaPrefix", schemaPrefix());
    ctx.set("table", table);
    return change(
      format("tbl_%s_init", table.name),
      removeBlankLines(template.render(ctx))
    );
  }

  private MtChange generateTableWithColumnsAndFks(MtTable table) {
    var template = loader.load("/io/vacco/metolithe/codegen/MtTableWithColumnsAndFks.bt");
    var ctx = new TemplateContext();
    ctx.set("schemaPrefix", schemaPrefix());
    ctx.set("table", table);
    ctx.set("join", join);
    return change(
      format("tbl_%s_cols_fks", table.name),
      removeBlankLines(template.render(ctx))
    );
  }

  private MtChange generateColumnChange(MtTable table, MtCol column) {
    var template = loader.load("/io/vacco/metolithe/codegen/MtColumnChange.bt");
    var ctx = new TemplateContext();
    ctx.set("schemaPrefix", schemaPrefix());
    ctx.set("table", table);
    ctx.set("column", column);
    return change(
      format("tbl_%s_col_%s", table.name, column.name),
      removeBlankLines(template.render(ctx))
    );
  }

  private MtChange generateForeignKeyChange(MtTable table, MtFkey fk) {
    var template = loader.load("/io/vacco/metolithe/codegen/MtForeignKeyChange.bt");
    var ctx = new TemplateContext();
    ctx.set("schemaPrefix", schemaPrefix());
    ctx.set("table", table);
    ctx.set("fk", fk);
    return change(
      format("tbl_%s_%s", table.name, fk.name),
      removeBlankLines(template.render(ctx))
    );
  }

  private MtChange generateUniqueConstraintChange(MtTable table, MtUnq unq) {
    var template = loader.load("/io/vacco/metolithe/codegen/MtUniqueConstraintChange.bt");
    var ctx = new TemplateContext();
    ctx.set("schemaPrefix", schemaPrefix());
    ctx.set("table", table);
    ctx.set("unq", unq);
    ctx.set("join", join);
    return change(
      format("tbl_%s_%s", table.name, unq.name),
      removeBlankLines(template.render(ctx))
    );
  }

  private MtChange generateIndexChange(MtTable table, MtIdx idx) {
    var template = loader.load("/io/vacco/metolithe/codegen/MtIndexChange.bt");
    var ctx = new TemplateContext();
    ctx.set("schemaPrefix", schemaPrefix());
    ctx.set("table", table);
    ctx.set("idx", idx);
    ctx.set("join", join);
    return change(
      format("tbl_%s_%s", table.name, idx.name),
      removeBlankLines(template.render(ctx))
    );
  }

  private void validateTable(MtTable table) {
    if (table.columns == null) {
      throw new IllegalArgumentException("Table " + table.name + " has null columns");
    }
    for (MtCol col : table.columns) {
      if (col == null || col.name == null || col.type == null) {
        throw new IllegalArgumentException("Invalid column in table " + table.name + ": " + col);
      }
    }
    for (MtFkey fk : table.fKeys) {
      if (fk == null || fk.name == null || fk.fromCol == null || fk.to == null || fk.toCol == null) {
        throw new IllegalArgumentException("Invalid foreign key in table " + table.name + ": " + fk);
      }
    }
    for (MtUnq unq : table.unique) {
      if (unq == null || unq.name == null || unq.columns.isEmpty()) {
        throw new IllegalArgumentException("Invalid unique constraint in table " + table.name + ": " + unq);
      }
    }
    for (MtIdx idx : table.indices) {
      if (idx == null || idx.name == null || idx.columns.isEmpty()) {
        throw new IllegalArgumentException("Invalid index in table " + table.name + ": " + idx);
      }
    }
  }

  public List<MtChange> generateSql(List<MtTable> tables, MtLevel level) {
    if (tables == null || tables.isEmpty()) {
      return Collections.emptyList();
    }
    if (level == null) {
      throw new IllegalArgumentException("Granularity cannot be null");
    }
    var changeSets = new ArrayList<MtChange>();
    for (var table : tables) {
      if (table == null || table.name == null) {
        throw new IllegalArgumentException("Invalid table: " + table);
      }
      validateTable(table);
      switch (level) {
        case TABLE_COMPACT:
          changeSets.add(generateFullTable(table));
          break;
        case TABLE_AND_INDICES:
          changeSets.add(generateTableWithColumnsAndFks(table));
          for (var unq : table.unique)  { changeSets.add(generateUniqueConstraintChange(table, unq)); }
          for (var idx : table.indices) { changeSets.add(generateIndexChange(table, idx)); }
          break;
        case TABLE_MAX:
          changeSets.add(generateEmptyTable(table));
          for (var col : table.columns) { changeSets.add(generateColumnChange(table, col)); }
          for (var fk : table.fKeys)    { changeSets.add(generateForeignKeyChange(table, fk)); }
          for (var unq : table.unique)  { changeSets.add(generateUniqueConstraintChange(table, unq)); }
          for (var idx : table.indices) { changeSets.add(generateIndexChange(table, idx)); }
          break;
      }
    }
    return changeSets;
  }

  public List<MtChange> process(List<MtTable> tables, MtLevel level) {
    var chs = generateSql(tables, level);
    for (var chg : chs) {
      chg.hash = Integer.toHexString(hash32(chg.sql.getBytes(StandardCharsets.UTF_8), DEFAULT_SEED));
    }
    return chs;
  }

}