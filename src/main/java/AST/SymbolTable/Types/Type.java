package AST.SymbolTable.Types;

import AST.Expressions.Expression;
import AST.SymbolTable.Identifier;
import AST.SymbolTable.SymbolTable.SymbolTable;
import java.math.BigInteger;

public interface Type extends Identifier {

    /**
     * Generate a literal expression of the current type
     * @param symbolTable
     * @return Expression
     */
    Expression generateLiteral(SymbolTable symbolTable);

    /**
     * Given the value of the type, generate a new Expression with an equal value
     * @param symbolTable
     * @param value
     * @return Expression
     */
    Expression generateExpressionFromValue(SymbolTable symbolTable, Object value);

    /**
     * Return the type of the variable as a string
     * @return String
     */
    default String getVariableType() {
        return getName();
    }

    /**
     * Indicates if there is an operator (binary or unary) for the current type
     * @return boolean
     */
    boolean operatorExists();

    /**
     * Indicates if a value of the current type has deterministic printing in Dafny
     * @return boolean
     */
    default boolean isPrintable() {
        return true;
    }

    /**
     * Converts the current type in to a new instance with any fields created. For example, a seq
     * type requires an inner type to be created
     * @param symbolTable
     * @return Type
     */
    default Type concrete(SymbolTable symbolTable) {
        return this;
    }

    /**
     * Whether the collection is a collection of more than than one element
     * @return boolean
     */
    boolean isCollection();

    /**
     * Whether two values of this type satisfy lhsV < rhsV
     * @param lhsV
     * @param rhsV
     * @return Boolean
     */
    default Boolean lessThan(Object lhsV, Object rhsV) {
        System.err.printf("Could not use less than with class %s", this.getVariableType());
        return null;
    }

    /**
     * Whether two values of this type satisfy lhsV == rhsV
     * @param lhsV
     * @param rhsV
     * @return Boolean
     */
    default Boolean equal(Object lhsV, Object rhsV) {
        System.err.printf("Could not use equal with class %s", this.getVariableType());
        return null;
    }

    /**
     * Whether two values of this type satisfy lhsV <= rhsV
     * @param lhsV
     * @param rhsV
     * @return Boolean
     */
    default Boolean lessThanOrEqual(Object lhsV, Object rhsV) {
        return lessThan(lhsV, rhsV) || equal(lhsV, rhsV);
    }

    /**
     * Whether two values of this type satisfy lhsV > rhsV
     * @param lhsV
     * @param rhsV
     * @return Boolean
     */
    default Boolean greaterThan(Object lhsV, Object rhsV) {
        return lessThan(rhsV, lhsV);
    }

    /**
     * Whether two values of this type satisfy lhsV >= rhsV
     * @param lhsV
     * @param rhsV
     * @return Boolean
     */
    default Boolean greaterThanOrEqual(Object lhsV, Object rhsV) {
        return greaterThan(lhsV, rhsV) || equal(lhsV, rhsV);
    }

    /**
     * The size of the value
     * @param value
     * @return BigInterger
     */
    default BigInteger cardinality(Object value) {
        System.err.printf("Could not use cardinality with class %s", this.getVariableType());
        return null;
    }

    /**
     * Concatenated the values together.
     * @param lhsV
     * @param rhsV
     * @return
     */
    default String concatenate(Object lhsV, Object rhsV) {
        System.err.printf("Could not use concatenate with class %s", this.getVariableType());
        return null;
    }

    /**
     * Converting the value into the required format as if it were printing when executed by Dafny
     * @param object
     * @return String
     */
    String formatPrint(Object object);

    /**
     * Provide a boolean expression as a string which is used for verification
     * @param variableName
     * @param object
     * @return String
     */
    String formatEnsures(String variableName, Object object);

    /**
     * Is the type allowed in methods (either as arguments or as a return type)
     * @return boolean
     */
    default boolean validMethodType() {
        return true;
    }

    /**
     * Is the type allowed in functions (either as arguments or as a return type)
     * @return boolean
     */
    default boolean validFunctionType() {
        return validMethodType();
    }

    /**
     * Return the value given it is of this type (allowing for any additional processing)
     * @return boolean
     */
    default Object of(Object value) {
        return value;
    }

    /**
     * Is the type orderable (i.e. can the operator < be used on it)
     * @return boolean
     */
    default boolean isOrdered() {
        return true;
    }
}
