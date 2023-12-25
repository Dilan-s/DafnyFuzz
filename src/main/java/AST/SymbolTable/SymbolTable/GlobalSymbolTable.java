package AST.SymbolTable.SymbolTable;

public class GlobalSymbolTable {

  private static final SymbolTable instance = new SymbolTable(true);

  public static SymbolTable getGlobalSymbolTable() {
    return instance;
  }
}
