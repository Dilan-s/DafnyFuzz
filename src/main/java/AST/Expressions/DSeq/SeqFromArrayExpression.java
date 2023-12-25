package AST.Expressions.DSeq;

import AST.Expressions.Array.ArrayValue;
import AST.Expressions.BaseExpression;
import AST.Statements.Statement;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SeqFromArrayExpression extends BaseExpression {

  private final SymbolTable symbolTable;
  private final Type type;
  private final Variable arrayVariable;
  private final Statement arrayAssign;

  private final List<List<Statement>> expanded;

  public SeqFromArrayExpression(SymbolTable symbolTable, Type type, Variable arrayVariable,
    Statement arrayAssign) {
    super();
    this.symbolTable = symbolTable;
    this.type = type;
    this.arrayVariable = arrayVariable;
    this.arrayAssign = arrayAssign;

    this.expanded = new ArrayList<>();
    expanded.add(arrayAssign.expand());
  }

  @Override
  public List<Type> getTypes() {
    return List.of(type);
  }

  @Override
  protected List<Object> getValue(Map<Variable, Variable> paramMap, StringBuilder s,
    boolean unused) {
    List<Object> arrValues = arrayVariable.getValue(paramMap);

    ArrayValue arrayValue = (ArrayValue) arrValues.get(0);

    List<Object> r = new ArrayList<>();
    r.add(arrayValue.getContents());
    return r;
  }

  @Override
  public List<Statement> expand() {
    expanded.set(0, arrayAssign.expand());
    return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return String.format("%s[..]", arrayVariable.getName());
  }

  @Override
  public boolean validForFunctionBody() {
    return false;
  }
}
