package AST.Expressions.Method;

import AST.Expressions.BaseExpression;
import AST.Expressions.Expression;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.Statement;
import AST.SymbolTable.Method.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class CallMethodExpression extends BaseExpression {

  protected SymbolTable symbolTable;
  protected Method method;
  protected List<Variable> variables;
  protected List<Statement> assignments;
  protected List<Variable> assignedVariables;
  protected Expression callExpr;
  protected AssignmentStatement assignStat;

  protected List<List<Statement>> expanded;


  public CallMethodExpression(SymbolTable symbolTable, Method method, List<Expression> args) {
    super();
    this.symbolTable = symbolTable;
    this.method = method;
    this.variables = new ArrayList<>();
    this.assignments = new ArrayList<>();
    this.assignedVariables = new ArrayList<>();
    addArg(args);
    generateReturnAssignment();

    this.expanded = new ArrayList<>();
    assignments.forEach(s -> expanded.add(s.expand()));
    expanded.add(assignStat.expand());
  }

  private void addArg(List<Expression> expressions) {
    for (Expression e : expressions) {
      Type type = e.getTypes().get(0);
      String var = VariableNameGenerator.generateVariableValueName(type, symbolTable);
      Variable variable = new Variable(var, type);
      variables.add(variable);

      AssignmentStatement stat = new AssignmentStatement(symbolTable, List.of(variable), e);
      assignments.add(stat);
    }
  }


  private void generateReturnAssignment() {
    assignedVariables = new ArrayList<>();

    for (Type returnType : method.getReturnTypes()) {
      Type rt = returnType.concrete(symbolTable);
      String var = VariableNameGenerator.generateVariableValueName(rt, symbolTable);
      Variable variable = new Variable(var, rt);
      assignedVariables.add(variable);
    }

    callExpr = getCallExpression();

    assignStat = new AssignmentStatement(symbolTable, assignedVariables, callExpr);
  }

  protected abstract Expression getCallExpression();

  @Override
  public List<Type> getTypes() {
    return method.getReturnTypes();
  }

  @Override
  public List<Statement> expand() {
    int i;
    for (i = 0; i < assignments.size(); i++) {
      Statement assignment = assignments.get(i);
      expanded.set(i, assignment.expand());
    }
    expanded.set(i, assignStat.expand());
    return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
  }

  @Override
  public boolean isValidReturn() {
    return false;
  }

  @Override
  public String toString() {
    return assignedVariables.stream()
      .map(Variable::getName)
      .collect(Collectors.joining(", "));
  }

  @Override
  protected List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s,
    boolean unused) {
    return assignedVariables.stream()
      .map(v -> v.getValue(paramsMap))
      .flatMap(Collection::stream)
      .collect(Collectors.toList());
  }

  @Override
  public boolean validForFunctionBody() {
    return false;
  }

}
