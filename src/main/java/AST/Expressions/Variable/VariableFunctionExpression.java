package AST.Expressions.Variable;

import AST.Expressions.BaseExpression;
import AST.Expressions.DClass.DClassValue;
import AST.Expressions.Expression;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.Statement;
import AST.SymbolTable.Function.Function;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.DClass;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VariableFunctionExpression extends BaseExpression {

  private Function function;
  private Type type;
  private SymbolTable symbolTable;

  private Variable functionVariable;
  private AssignmentStatement functionAssign;

  private List<List<Statement>> expanded;

  public VariableFunctionExpression(SymbolTable symbolTable, Function function, Type type) {
    super();
    this.symbolTable = symbolTable;
    this.function = function;
    this.type = type;

    this.functionVariable = new Variable(VariableNameGenerator.generateVariableValueName(type, symbolTable), type);
    this.functionAssign = new AssignmentStatement(symbolTable, List.of(functionVariable), new FunctionExpression(function));

    this.expanded = new ArrayList<>();
    expanded.add(functionAssign.expand());
  }

  @Override
  public List<Type> getTypes() {
    return List.of(type);
  }

  @Override
  public String toString() {
    return functionVariable.getName();
  }

  @Override
  protected List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s, boolean unused) {
    if (paramsMap.containsKey(functionVariable)) {
      return paramsMap.get(functionVariable).getValue(paramsMap);
    }
    return functionVariable.getValue(paramsMap);
  }

  public Variable getVariable() {
    return functionVariable;
  }

  @Override
  public List<Statement> expand() {
    return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
  }

  private class FunctionExpression extends BaseExpression {

    private final Function function;

    public FunctionExpression(Function function) {
      this.function = function;
    }

    @Override
    public List<Type> getTypes() {
      return List.of(type);
    }

    @Override
    public List<Statement> expand() {
      return new ArrayList<>();
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s, boolean unused) {
      List<Object> r = new ArrayList<>();
      r.add(new FunctionVariableValue(function));
      return r;
    }

    @Override
    public String toString() {
      return function.getName();
    }

    @Override
    public boolean requireUpdate() {
      return false;
    }
  }
}
