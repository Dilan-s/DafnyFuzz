package AST.Expressions.Method;

import AST.Expressions.BaseExpression;
import AST.Expressions.Expression;
import AST.Statements.Statement;
import AST.SymbolTable.Method.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CallBaseMethodExpression extends CallMethodExpression {

  public CallBaseMethodExpression(SymbolTable symbolTable, Method method, List<Expression> args) {
    super(symbolTable, method, args);
  }

  @Override
  protected Expression getCallExpression() {
    return new CallMethod(method, variables);
  }

  private class CallMethod extends BaseExpression {

    private final Method method;
    private final List<Variable> args;

    public CallMethod(Method method, List<Variable> args) {
      super();
      this.method = method;
      this.args = args;
    }

    @Override
    public List<Type> getTypes() {
      return method.getReturnTypes();
    }

    @Override
    public String toString() {
      return String.format("%s(%s)", method.getName(), args.stream()
        .map(Variable::getName)
        .collect(Collectors.joining(", ")));
    }

    @Override
    public List<Statement> expand() {
      return new ArrayList<>();
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramMap, StringBuilder s,
      boolean unused) {
      List<Object> r = new ArrayList<>();

      List<Object> l = new ArrayList<>();
      for (Variable arg : args) {
        List<Object> value = arg.getValue(paramMap);
        for (Object v : value) {
          if (v == null) {
            method.getReturnTypes().forEach(t -> r.add(null));
            return r;
          }
          l.add(v);
        }
      }
      return method.execute(args, s);
    }
  }
}
