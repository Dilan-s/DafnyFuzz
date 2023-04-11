package AST.SymbolTable.Types.PrimitiveTypes;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.BoolLiteral;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.StringLiteral;
import AST.StringUtils;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.Random;

public class DString implements BaseType {

    public DString() {
    }

    @Override
    public String getName() {
        return "bool";
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
        return other instanceof DString;
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        return new StringLiteral(this, symbolTable, "");
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable, Object value) {
        Type t = this.concrete(symbolTable);
        return new StringLiteral(t, symbolTable, (String) value);
    }

    @Override
    public boolean operatorExists() {
        return false;
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        return new DString();
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
    public String formatPrint(Object object) {
        return object.toString();
    }
}
