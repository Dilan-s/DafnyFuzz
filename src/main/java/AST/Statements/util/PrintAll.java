package AST.Statements.util;

import AST.Statements.BaseStatement;
import AST.Statements.PrintStatement;
import AST.Statements.Statement;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PrintAll extends BaseStatement {

  private final PrintStatement printStat;
  private final SymbolTable symbolTable;

  public PrintAll(SymbolTable symbolTable) {
    this.symbolTable = symbolTable;
    this.printStat = new PrintStatement(symbolTable);
  }

  @Override
  public String toString() {
    return "";
  }

  @Override
  public List<String> toOutput() {
    return Collections.emptyList();
  }

  @Override
  public ReturnStatus execute(Map<Variable, Variable> paramMap, StringBuilder s, boolean unused) {
    return ReturnStatus.UNKNOWN;
  }

  @Override
  public List<Statement> expand() {
    return printStat.expand();
  }

  @Override
  public String minimizedTestCase() {
    return null;
  }
}
