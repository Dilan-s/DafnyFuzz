package AST.Statements;

import AST.Errors.SemanticException;
import AST.Statements.Expressions.Expression;
import AST.Statements.util.ReturnStatus;
import AST.SymbolTable.Method;
import AST.SymbolTable.Variable;
import java.util.List;
import java.util.Map;

public interface Statement {

    void semanticCheck(Method method) throws SemanticException;

    List<String> toCode();

    default boolean isReturn() {
        return false;
    }

    default boolean couldReturn() {
        return false;
    }

    ReturnStatus assignReturnIfPossible(Method method, ReturnStatus currStatus, List<Expression> dependencies);

    List<Object> execute(Map<Variable, Variable> paramMap);

    List<Statement> expand();
}
