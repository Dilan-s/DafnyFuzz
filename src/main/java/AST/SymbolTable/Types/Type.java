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

    default boolean greaterThan(Type rhsT) {
        return rhsT.lessThan(this);
    }

    default boolean greaterThanOrEqual(Type rhsT) {
        return rhsT.lessThanOrEqual(this);
    }

    default boolean lessThanOrEqual(Type rhsT) {
        return false;
    }

    default boolean lessThan(Type rhsT) {
        return false;
    }

    default boolean equal(Type rhsT) {
        return greaterThanOrEqual(rhsT) && lessThanOrEqual(rhsT);
    }

    void setValue(Object value);

    Object getValue();

    Expression generateLiteral(SymbolTable symbolTable, Object value);

    default Expression generateLiteral(SymbolTable symbolTable, Expression exp, Object value) {
        if (value == null) {
            return exp;
        }
        return generateLiteral(symbolTable, value);
    }

    void setExpressionAndIndAndDependencies(Expression expression, int ind, List<Expression> dependencies);
}
