package AST.SymbolTable.Types.PrimitiveTypes;

import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;

public class Void implements BaseType {

    @Override
    public String getName() {
        return "";
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Type)) {
            return false;
        }
        Type other = (Type) obj;
        return other instanceof Void;
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        return null;
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable, Object value) {
        return null;
    }

    @Override
    public Boolean lessThan(Object lhsV, Object rhsV) {
        return false;
    }

    @Override
    public Boolean equal(Object lhsV, Object rhsV) {
        return false;
    }

    @Override
    public boolean operatorExists() {
        return false;
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        return new Void();
    }
}
