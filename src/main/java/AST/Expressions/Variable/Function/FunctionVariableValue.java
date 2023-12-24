package AST.Expressions.Variable.Function;

import AST.SymbolTable.Function.Function;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.List;
import java.util.Objects;

public class FunctionVariableValue implements FunctionValue {

  private Function function;

  public FunctionVariableValue(Function function) {
    this.function = function;
  }

  @Override
  public int hashCode() {
    return Objects.hash(function);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof FunctionVariableValue)) {
      return false;
    }

    FunctionVariableValue other = (FunctionVariableValue) obj;
    return other.function == function;
  }

  public Function getFunction() {
    return function;
  }

  @Override
  public List<Object> execute(List<Variable> variables, StringBuilder s) {
    return function.execute(variables, s);
  }

  @Override
  public String getFunctionName() {
    return function.getName();
  }
}
