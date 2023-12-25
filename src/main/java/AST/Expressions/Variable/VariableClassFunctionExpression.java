package AST.Expressions.Variable;

import AST.Expressions.BaseExpression;
import AST.Expressions.Expression;
import AST.Expressions.Variable.Function.FunctionClassVariableValue;
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

public class VariableClassFunctionExpression extends BaseExpression {

  private final Function function;
  private final Type type;
  private final SymbolTable symbolTable;

  private final Variable classExpVariable;
  private final AssignmentStatement classExpAssign;
  private final Variable functionVariable;
  private final AssignmentStatement functionAssign;

  private final List<List<Statement>> expanded;

  public VariableClassFunctionExpression(SymbolTable symbolTable, Function function, Type type,
    DClass dClass, Expression classExp) {
    super();
    this.symbolTable = symbolTable;
    this.function = function;
    this.type = type;

    this.classExpVariable = new Variable(
      VariableNameGenerator.generateVariableValueName(dClass, symbolTable), dClass);
    this.classExpAssign = new AssignmentStatement(symbolTable, List.of(classExpVariable), classExp);

    this.functionVariable = new Variable(
      VariableNameGenerator.generateVariableValueName(type, symbolTable), type);
    this.functionAssign = new AssignmentStatement(symbolTable, List.of(functionVariable),
      new FunctionExpression(classExpVariable, function));

    this.expanded = new ArrayList<>();
    expanded.add(classExpAssign.expand());
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
  protected List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s,
    boolean unused) {
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
    expanded.set(0, classExpAssign.expand());
    expanded.set(1, functionAssign.expand());
    return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
  }

  private class FunctionExpression extends BaseExpression {

    private final Function function;
    private final Variable classExpVariable;

    public FunctionExpression(Variable classExpVariable,
      Function function) {
      this.classExpVariable = classExpVariable;
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
    public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s,
      boolean unused) {
      List<Object> r = new ArrayList<>();
      r.add(new FunctionClassVariableValue(classExpVariable, function));
      return r;
    }

    @Override
    public String toString() {
      return String.format("%s.%s", classExpVariable.getName(), function.getName());
    }
  }
}
