package AST.SymbolTable.Types.PrimitiveTypes;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.IntLiteral;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.Objects;

public class Int implements BaseType {

    private static final int MAX_INT = 30;
    private static final double PROB_HEX = 0.2;
    public static final double PROB_NEGATION = 0.1;
    private int max;
    private Integer value;
    private boolean asHex;

    public Int(int max) {
        this.max = max;
        this.value = null;
    }

    public Int() {
        this(MAX_INT);
    }

    @Override
    public String getName() {
        return "int";
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
        return other instanceof Int;
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        value = GeneratorConfig.getRandom().nextInt(max);
        value *= GeneratorConfig.getRandom().nextDouble() < PROB_NEGATION ? -1 : 1;
        asHex = value > 0 && GeneratorConfig.getRandom().nextDouble() < PROB_HEX;
        return new IntLiteral(this, symbolTable, value, asHex);
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable, Object value) {
        Type t = this.concrete(symbolTable);
        return new IntLiteral(t, symbolTable, (Integer) value, asHex);
    }

    @Override
    public boolean operatorExists() {
        return true;
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        return new Int();
    }

    @Override
    public Boolean lessThan(Object lhsV, Object rhsV) {
        Integer lhs = (Integer) lhsV;
        Integer rhs = (Integer) rhsV;
        return lhs < rhs;
    }

    @Override
    public Boolean equal(Object lhsV, Object rhsV) {
        Integer lhs = (Integer) lhsV;
        Integer rhs = (Integer) rhsV;
        return Objects.equals(lhs, rhs);
    }
}
