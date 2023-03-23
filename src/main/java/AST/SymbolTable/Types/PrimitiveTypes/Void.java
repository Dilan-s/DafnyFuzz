package AST.SymbolTable.Types.PrimitiveTypes;

import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;

public class Void implements Type {

    @Override
    public String getName() {
        return "";
    }

    @Override
    public boolean isSameType(Type other) {
        return other instanceof Void;
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        return null;
    }

    @Override
    public String getTypeIndicatorString() {
        return "";
    }

    @Override
    public String getReturnTypeIndicator(String method) {
        return "";
    }

    @Override
    public boolean operatorExists() {
        return false;
    }
}
