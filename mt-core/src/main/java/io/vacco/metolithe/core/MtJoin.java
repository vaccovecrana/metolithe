package io.vacco.metolithe.core;

import java.util.Objects;

import static java.lang.String.format;

public class MtJoin {

  public enum JoinType { INNER, LEFT }

  public final JoinType type;
  public final MtDescriptor<?> table;
  public final MtPredicate joinCondition;
  public final String schema;

  private MtJoin(String schema, JoinType type, MtDescriptor<?> table, MtPredicate joinOn) {
    this.type = Objects.requireNonNull(type);
    this.table = Objects.requireNonNull(table);
    this.joinCondition = Objects.requireNonNull(joinOn);
    this.schema = Objects.requireNonNull(schema);
  }

  public static MtJoin inner(String schema, MtDescriptor<?> sourceTable, MtDescriptor<?> targetTable) {
    var pk = targetTable.getPkField().orElseThrow();
    var fk = sourceTable.getForeignKeyTo(targetTable.getType()).orElseThrow(
      () -> new IllegalArgumentException(format(
        "Table [%s] has no foreign key to [%s]", sourceTable, targetTable
      ))
    );
    var join = MtPredicate.join(fk, pk);
    return new MtJoin(schema, JoinType.INNER, sourceTable, join);
  }

  public static MtJoin left(String schema, MtDescriptor<?> sourceTable, MtDescriptor<?> targetTable) {
    var pk = targetTable.getPkField().orElseThrow();
    var fk = sourceTable.getForeignKeyTo(targetTable.getType()).orElseThrow(
      () -> new IllegalArgumentException(format(
        "Table [%s] has no foreign key to [%s]", sourceTable, targetTable
      ))
    );
    var join = MtPredicate.join(fk, pk);
    return new MtJoin(schema, JoinType.LEFT, sourceTable, join);
  }

  public String render() {
    return String.format("%s join %s.%s %s on %s",
      type == JoinType.INNER ? "inner" : "left",
      schema, table.getName(), table.getAlias(),
      joinCondition.render()
    );
  }

}