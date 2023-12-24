package AST.Expressions.Variable.Function;

import AST.SymbolTable.Function.Function;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.List;
import java.util.Objects;

public class FunctionClassVariableValue implements FunctionValue {

  private Variable classExpVariable;
  private Function function;

  public FunctionClassVariableValue(Variable classExpVariable, Function function) {
    this.classExpVariable = classExpVariable;
    this.function = function;
  }

  @Override
  public int hashCode() {
    return Objects.hash(function, classExpVariable);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof FunctionClassVariableValue)) {
      return false;
    }

    FunctionClassVariableValue other = (FunctionClassVariableValue) obj;
    return other.function == function && other.classExpVariable == classExpVariable;
  }

  public Function getFunction() {
    return function;
  }

  @Override
  public List<Object> execute(List<Variable> variables, StringBuilder s) {
    function.assignThis(classExpVariable);
    return function.execute(variables, s);
  }

  @Override
  public String getFunctionName() {
    return String.format("%s.%s", classExpVariable.getName(), function.getName());
  }
}
