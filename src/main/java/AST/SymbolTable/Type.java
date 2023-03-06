package AST.SymbolTable;

import AST.Generator.VariableNameGenerator;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import java.util.Random;

public interface Type extends Identifier {

    boolean isSameType(Type other);

    Expression generateLiteral(Random random, SymbolTable symbolTable);

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

    default boolean isCollection() {
        return false;
    }

    default Type setInnerType(Type type) {
        return null;
    }

    default Type getInnerType() {
        return null;
    }
}
