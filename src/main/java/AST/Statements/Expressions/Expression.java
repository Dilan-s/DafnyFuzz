package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Expression {

    List<Type> getTypes();

    void semanticCheck(Method method) throws SemanticException;

    default boolean isValidReturn() {
        return true;
    }

    default List<String> toCode() {
        return new ArrayList<>();
    }

    default List<Object> getValue() {
        return getValue(new HashMap<>());
    }

    default List<Object> getValue(Map<Variable, Variable> paramsMap) {
        List<Object> l = new ArrayList<>();
        l.add(null);
        return l;
    }
}
