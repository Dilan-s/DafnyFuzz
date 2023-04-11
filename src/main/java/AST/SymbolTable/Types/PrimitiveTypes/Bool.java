package AST.SymbolTable.Types.PrimitiveTypes;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.BoolLiteral;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.Objects;

public class Bool implements BaseType {

    private Boolean value;

    public Bool() {
        this.value = null;
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
        return other instanceof Bool;
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        value = GeneratorConfig.getRandom().nextBoolean();
        return new BoolLiteral(this, symbolTable, value);
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable, Object value) {
        Type t = this.concrete(symbolTable);
        return new BoolLiteral(t, symbolTable, (Boolean) value);
    }

    @Override
    public boolean operatorExists() {
        return true;
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        return new Bool();
    }

    @Override
    public Boolean lessThan(Object lhsV, Object rhsV) {
        return false;
    }

    @Override
    public Boolean equal(Object lhsV, Object rhsV) {
        Boolean l = (Boolean) lhsV;
        Boolean r = (Boolean) rhsV;
        return Objects.equals(l, r);
    }

    @Override
    public String formatPrint(Object object) {
        return object.toString();
    }
}
