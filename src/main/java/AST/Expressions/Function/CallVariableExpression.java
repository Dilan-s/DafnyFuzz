package AST.Expressions.Function;

import AST.Expressions.BaseExpression;
import AST.Expressions.Expression;
import AST.Expressions.Variable.Function.FunctionValue;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.Statement;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CallVariableExpression extends BaseExpression {

  private final SymbolTable symbolTable;
  private final Type type;

  private final List<Statement> assignments;
  private final List<Variable> variables;

  private final Variable funcVariable;
  private final Statement funcVariableAssign;

  private final List<List<Statement>> expanded;

  public CallVariableExpression(SymbolTable symbolTable, Type type, Variable funcVariable,
    Statement funcVariableAssign, List<Expression> args) {
    super();
    this.symbolTable = symbolTable;
    this.type = type;

    this.funcVariable = funcVariable;
    this.funcVariableAssign = funcVariableAssign;

    this.assignments = new ArrayList<>();
    this.variables = new ArrayList<>();

    this.expanded = new ArrayList<>();
    expanded.add(funcVariableAssign.expand());

    args.forEach(e -> {
      Type t = e.getTypes().get(0);
      String var = VariableNameGenerator.generateVariableValueName(t, symbolTable);
      Variable variable = new Variable(var, t);
      variables.add(variable);

      AssignmentStatement stat = new AssignmentStatement(symbolTable, List.of(variable), e);
      assignments.add(stat);
      expanded.add(stat.expand());
    });
  }

  @Override
  public List<Statement> expand() {
    int i = 0;
    expanded.set(i, funcVariableAssign.expand());
    i++;

    for (int j = 0; j < assignments.size(); j++) {
      Statement assignment = assignments.get(j);
      expanded.set(i, assignment.expand());
      i++;
    }

    return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return String.format("%s(%s)", funcVariable.getName(), variables.stream()
      .map(Variable::getName)
      .collect(Collectors.joining(", ")));
  }

  @Override
  protected List<Object> getValue(Map<Variable, Variable> paramMap, StringBuilder s,
    boolean unused) {
    List<Object> fValueVar = funcVariable.getValue(paramMap);
    FunctionValue fValue = (FunctionValue) fValueVar.get(0);

    List<Object> r = new ArrayList<>();

    List<Object> l = new ArrayList<>();
    for (Variable arg : variables) {
      List<Object> value = arg.getValue(paramMap);
      for (Object v : value) {
        if (v == null) {
          r.add(null);
          return r;
        }
        l.add(v);
      }
    }
    return fValue.execute(variables, s);
  }

  @Override
  public List<Type> getTypes() {
    return List.of(type);
  }
}
