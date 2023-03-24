package AST.SymbolTable.Types;

import AST.Generator.VariableNameGenerator;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.Identifier;
import AST.SymbolTable.SymbolTable.SymbolTable;

public interface Type extends Identifier {

    boolean isSameType(Type other);

    Expression generateLiteral(SymbolTable symbolTable);

    default String getVariableType() {
        return getName();
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
