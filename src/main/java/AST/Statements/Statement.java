package AST.Statements;

import AST.Statements.util.ReturnStatus;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Statement {

    /**
     * Does the statement guarantee a return
     * @return boolean
     */
    default boolean isReturn() {
        return false;
    }

    /**
     * Wrapper for execution with no parameters
     * @param s
     * @return ReturnStatus
     */
    default ReturnStatus execute(StringBuilder s) {
        return execute(new HashMap<>(), s);
    }

    /**
     * Executed the method in memory, saving any output that would be produced to s
     * @param paramMap
     * @param s
     * @return ReturnStatus
     */
    ReturnStatus execute(Map<Variable, Variable> paramMap, StringBuilder s);

    /**
     * Expand the current statement to give any additional statements required
     * @return List<Statement>
     */
    List<Statement> expand();

    /**
     * Convert the current statement to multiple (at most 5) variations of the current statement
     * via metamorphic transformations
     * @return List<String>
     */
    List<String> toOutput();

    /**
     * Add a use for every time the statement is executed
     */
    void incrementUse();

    /**
     * Get the number of uses of the current statement
     * @return int
     */
    int getNoOfUses();

    /**
     * Get the current statement in the form where there is no dead code (used after execution,
     * hence dead code has zero uses)
     * @return String
     */
    String minimizedTestCase();

    /**
     * For any verification statement, return a mapping of correct verification strings to be
     * replaced by incorrect verification strings
     * @return Map<String, String>
     */
    default Map<String, String> invalidValidationTests() {
        return new HashMap<>();
    }

    /**
     * Does the minimized statement guarantee a return
     * @return boolean
     */
    default boolean minimizedReturn() {
        return isReturn();
    }

    /**
     * Returns true if the expanded statements need to be updated
     * @return boolean
     */
    default boolean requireUpdate() {
        return false;
    }

    /**
     * Gives the set of a variables which are modified in the statement
     * @return Set<Variable>
     */
    default Set<Variable> getModifies() {
        return new HashSet<>();
    }

    /**
     * Is the current statement allowed to be the body of a function
     * @return boolean
     */
    default boolean validForFunction() {
        return false;
    }
}
