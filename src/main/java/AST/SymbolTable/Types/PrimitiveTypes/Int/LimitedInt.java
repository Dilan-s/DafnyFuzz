package AST.SymbolTable.Types.PrimitiveTypes.Int;

import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;

public class LimitedInt extends Int {

  public LimitedInt(int max) {
    super(max);
  }

  public LimitedInt() {
    super();
  }

  @Override
  public Type concrete(SymbolTable symbolTable) {
    return this;
  }
}
