package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.List;

public interface Expression {

    List<Type> getTypes();

    void semanticCheck(Method method) throws SemanticException;

    default boolean isValidReturn() {
        return true;
    }

    default List<String> toCode() {
        return new ArrayList<>();
    }

}
