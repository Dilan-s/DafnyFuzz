package AST.SymbolTable.Types;

import AST.Statements.Expressions.Expression;
import AST.SymbolTable.Identifier;
import AST.SymbolTable.SymbolTable.SymbolTable;

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

}
