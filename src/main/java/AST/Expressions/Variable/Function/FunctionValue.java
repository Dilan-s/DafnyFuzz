package AST.Expressions.Variable.Function;

import AST.SymbolTable.Types.Variables.Variable;
import java.util.List;

public interface FunctionValue {

  List<Object> execute(List<Variable> variables, StringBuilder s);

  String getFunctionName();
}
