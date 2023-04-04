package AST.SymbolTable.Types.PrimitiveTypes;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.RealLiteral;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.AbstractType;
import AST.SymbolTable.Types.Type;
import java.util.Objects;

public class Real extends AbstractType implements BaseType{

    private static final int MAX_DOUBLE = 30;
    public static final double PROB_NEGATION = 0.5;
    private Double value;

    public Real() {
        this.value = null;
    }

    @Override
    public String getName() {
        return "real";
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
        return other instanceof Real;
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        value = GeneratorConfig.getRandom().nextDouble() * MAX_DOUBLE;
        value *= GeneratorConfig.getRandom().nextDouble() < PROB_NEGATION ? -1 : 1;
        return new RealLiteral(this, symbolTable, value);
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable, Object value) {
        Type t = this.concrete(symbolTable);
        t.setValue(value);
        return new RealLiteral(t, symbolTable, (Double) value);
    }

    @Override
    public boolean operatorExists() {
        return true;
    }

    @Override
    public boolean isPrintable() {
        return false;
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        return new Real();
    }

    @Override
    public boolean lessThanOrEqual(Type rhsT) {
        Real rhsReal = (Real) rhsT;
        return value <= rhsReal.value;
    }

    @Override
    public boolean lessThan(Type rhsT) {
        Real rhsReal = (Real) rhsT;
        return value <= rhsReal.value;
    }

    @Override
    public boolean equal(Type rhsT) {
        Real rhsReal = (Real) rhsT;
        return Objects.equals(value, rhsReal.value);
    }

    @Override
    public void setValue(Object value) {
        this.value = (Double) value;
    }

    @Override
    public Object getValue() {
        return value;
    }
}
