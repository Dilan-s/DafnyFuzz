package AST.Statements;

import AST.Errors.SemanticException;
import AST.SymbolTable.Method;
import java.util.List;

public interface Statement {

    void semanticCheck(Method method) throws SemanticException;

    List<String> toCode();

    default boolean isReturn() {
        return false;
    }
}
