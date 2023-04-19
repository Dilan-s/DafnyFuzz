package AST.Statements;

import AST.Errors.SemanticException;
import AST.Statements.Expressions.Expression;
import AST.Statements.util.ReturnStatus;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.List;
import java.util.Map;

public interface Statement {

    default boolean isReturn() {
        return false;
    }

    default boolean couldReturn() {
        return false;
    }

    ReturnStatus assignReturnIfPossible(Method method, ReturnStatus currStatus, List<Expression> dependencies);

    List<Object> execute(Map<Variable, Variable> paramMap, StringBuilder s);

    List<Statement> expand();

    List<String> toOutput();
}
