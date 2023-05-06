package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Expression {

    List<Type> getTypes();

    default boolean isValidReturn() {
        return true;
    }

    default List<Object> getValue() {
        return getValue(new HashMap<>());
    }

    default List<Object> getValue(Map<Variable, Variable> paramsMap) {
        return getValue(paramsMap, new StringBuilder());
    }
    List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s);

    List<Statement> expand();

    default List<String> toOutput() {
        return List.of(toString());
    }

    default boolean requireUpdate() {
        return false;
    }

    default String minimizedTestCase() {
        return toString();
    }
}
