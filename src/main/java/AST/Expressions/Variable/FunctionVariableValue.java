package AST.Expressions.Variable;

import AST.SymbolTable.Function.Function;
import java.util.Objects;

public class FunctionVariableValue {

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
}
