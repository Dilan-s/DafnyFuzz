package AST.SymbolTable.Method;

import AST.Expressions.Expression;
import AST.Expressions.Method.CallClassMethodExpression;
import AST.Expressions.Method.CallMethodExpression;
import AST.Generator.RandomExpressionGenerator;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.DClass;
import AST.SymbolTable.Types.Variables.Variable;
import AST.SymbolTable.Types.Variables.VariableThis;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassMethod extends Method {

  private final DClass dClass;
  private VariableThis thisVariable;

  public ClassMethod(List<Type> returnTypes, String name, SymbolTable symbolTable,
    List<Variable> args, DClass dClass) {
    super(returnTypes, name, symbolTable, args);
    this.dClass = dClass;
    addThisToMethod();
  }

  public ClassMethod(List<Type> returnTypes, String name, SymbolTable symbolTable,
    DClass dClass) {
    this(returnTypes, name, symbolTable, new ArrayList<>(), dClass);
  }

  public ClassMethod(Type returnTypes, String name, SymbolTable symbolTable, DClass dClass) {
    this(List.of(returnTypes), name, symbolTable, dClass);
  }

  public ClassMethod(Type returnTypes, String name, DClass dClass) {
    this(List.of(returnTypes), name, new SymbolTable(), dClass);
  }

  public ClassMethod(List<Type> returnTypes, String name, DClass dClass) {
    this(returnTypes, name, new SymbolTable(), dClass);
  }

  public void addThisToMethod() {
    this.thisVariable = dClass.getThis();
    for (Variable arg : thisVariable.getSymbolTableArgs()) {
      getSymbolTable().addVariable(arg);
      arg.setDeclared();
    }
  }

  @Override
  public CallMethodExpression generateCall(SymbolTable symbolTable) {
    RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();

    List<Type> argTypes = getArgTypes();
    List<Expression> args = new ArrayList<>();
    for (Type t : argTypes) {
      Type concrete = t.concrete(symbolTable);
      Expression exp = expressionGenerator.generateExpression(concrete, symbolTable);
      args.add(exp);
    }

    Expression classExp = expressionGenerator.generateExpression(dClass, symbolTable);

    CallClassMethodExpression expression = new CallClassMethodExpression(symbolTable, this,
      classExp, args);
    return expression;
  }

  @Override
  public void assignThis(Variable classVariable) {
    thisVariable.set(classVariable);
  }

  @Override
  protected List<String> getRequiresClauses(Map<Variable, Variable> requiresEnsures) {
    List<String> requiresClauses = super.getRequiresClauses(requiresEnsures);
    if (requiresClauses == null) {
      return null;
    }

    List<Variable> symbolTableArgs = thisVariable.getSymbolTableArgs().stream()
      .filter(v -> v != thisVariable)
      .collect(Collectors.toList());
    for (Variable variable : symbolTableArgs) {
      String name = variable.getName();
      String v = variable.getType().formatEnsures(name, variable.getValue().get(0));
      if (v == null) {
        return null;
      }
      requiresClauses.add(v);
    }
    return requiresClauses;
  }
}
