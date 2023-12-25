package AST.Statements;

import AST.Statements.util.PrintAll;
import AST.Statements.util.ReturnStatus;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BreakStatement extends BaseStatement {

  private final Statement statement;
  private final int breakDepth;
  private final SymbolTable symbolTable;

  public BreakStatement(SymbolTable symbolTable, int breakDepth) {
    this.symbolTable = symbolTable;
    this.statement = new PrintAll(symbolTable);
    this.breakDepth = breakDepth;
  }

  @Override
  protected ReturnStatus execute(Map<Variable, Variable> paramMap, StringBuilder s,
    boolean unused) {
    return ReturnStatus.breakWithDepth(breakDepth);
  }

  @Override
  public List<Statement> expand() {
    List<Statement> r = new ArrayList<>();
    r.addAll(statement.expand());
    r.add(this);
    return r;
  }

  @Override
  public boolean isReturn() {
    return true;
  }

  @Override
  public String toString() {
    String curr = IntStream.range(0, breakDepth + 1).mapToObj(x -> "break")
      .collect(Collectors.joining(" "));
    return curr + ";";
  }

  @Override
  public String minimizedTestCase() {
    return toString();
  }

  @Override
  public boolean validForFunctionBody() {
    return false;
  }
}
