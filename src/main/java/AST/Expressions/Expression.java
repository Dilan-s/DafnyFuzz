package AST.Expressions;

import AST.Statements.Statement;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Expression {

    /**
     * Get the types of the expression
     * @return List<Type>
     */
    List<Type> getTypes();

    /**
     * Can the expression be used as a return value
     * @return boolean
     */
    default boolean isValidReturn() {
        return true;
    }

    /**
     * Wrapper for calculating the value of the current expression with no parameters
     * @return List<Object>
     */
    default List<Object> getValue() {
        return getValue(new HashMap<>());
    }

    /**
     * Wrapper for calculating the value of the current expression, ignoring any string outputs
     * @param paramsMap
     * @return List<Object>
     */
    default List<Object> getValue(Map<Variable, Variable> paramsMap) {
        return getValue(paramsMap, new StringBuilder());
    }

    /**
     * Calculate the value of the expression, saving all output to s
     * @param paramsMap
     * @param s
     * @return List<Object>
     */
    List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s);

    /**
     * Expand the current expression to give any additional statements required
     * @return List<Statement>
     */
    List<Statement> expand();

    /**
     * Add a use for every time the expression is executed
     */
    void incrementUse();

    /**
     * Get the number of uses of the current expression
     * @return int
     */
    int getNoOfUses();

    /**
     * Convert the current expression to multiple (at most 5) variations of the current expression
     * via metamorphic transformations
     * @return List<String>
     */
    default List<String> toOutput() {
        return List.of(toString());
    }

    /**
     * Returns true if the expanded statements need to be updated
     * @return boolean
     */
    default boolean requireUpdate() {
        return false;
    }

    /**
     * Get the current expression in the form where there is no dead code (used after execution,
     * hence dead code has zero uses)
     * @return String
     */
    default String minimizedTestCase() {
        return toString();
    }

    /**
     * Is the current statement allowed to be the body of a function
     * @return boolean
     */
    default boolean validForFunction() {
        return false;
    }
}
