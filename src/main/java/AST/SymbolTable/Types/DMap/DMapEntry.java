package AST.SymbolTable.Types.DMap;

import AST.Expressions.Expression;

public class DMapEntry {

  private final Expression key;
  private final Expression value;

  public DMapEntry(Expression key, Expression value) {
    this.key = key;
    this.value = value;
  }

  public Expression getKey() {
    return key;
  }

  public Expression getValue() {
    return value;
  }
}
