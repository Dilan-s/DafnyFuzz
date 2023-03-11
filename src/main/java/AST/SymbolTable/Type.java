package AST.SymbolTable;

import AST.Generator.VariableNameGenerator;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;

public interface Type extends Identifier {

    boolean isSameType(Type other);

    Expression generateLiteral(SymbolTable symbolTable);

    default String getTypeIndicatorString() {
        return String.format(": %s", getName());
    }

    default String getReturnTypeIndicator(String method) {
        String returnParameter = VariableNameGenerator.generateReturnVariableName(method);
        return String.format("%s%s", returnParameter, getTypeIndicatorString());
    }

    boolean operatorExists();

    default boolean isPrintable() {
        return true;
    }

    default Type concrete(SymbolTable symbolTable) {
        return this;
    }

    default boolean isCollection() {
        return false;
    }

}
