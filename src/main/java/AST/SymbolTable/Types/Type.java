package AST.SymbolTable.Types;

import AST.Statements.Expressions.Expression;
import AST.SymbolTable.Identifier;
import AST.SymbolTable.SymbolTable.SymbolTable;
import java.util.List;

public interface Type extends Identifier {

    Expression generateLiteral(SymbolTable symbolTable);

    default String getVariableType() {
        return getName();
    }

    boolean operatorExists();

    default boolean isPrintable() {
        return true;
    }

    Type concrete(SymbolTable symbolTable);

    boolean isCollection();

    Boolean lessThan(Object lhsV, Object rhsV);

    Boolean equal(Object lhsV, Object rhsV);

    default Boolean lessThanOrEqual(Object lhsV, Object rhsV) {
        return lessThan(lhsV, rhsV) || equal(lhsV, rhsV);
    }

    default Boolean greaterThan(Object lhsV, Object rhsV) {
        return lessThan(rhsV, lhsV);
    }

    default Boolean greaterThanOrEqual(Object lhsV, Object rhsV) {
        return greaterThan(lhsV, rhsV) || equal(lhsV, rhsV);
    }

    String formatPrint(Object object);

    String formatEnsures(String variableName, Object object);

    default boolean validMethodType() {
        return true;
    }
}
