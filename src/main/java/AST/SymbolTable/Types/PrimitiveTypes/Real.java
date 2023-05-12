package AST.SymbolTable.Types.PrimitiveTypes;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.RealLiteral;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.Objects;

public class Real implements BaseType {

    private static final int MAX_DOUBLE = 30;
    public static final double PROB_NEGATION = 0.5;

    public Real() {
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
        Double value = GeneratorConfig.getRandom().nextDouble() * MAX_DOUBLE;
        value *= GeneratorConfig.getRandom().nextDouble() < PROB_NEGATION ? -1 : 1;
        return new RealLiteral(this, symbolTable, value);
    }

    @Override
    public Boolean lessThan(Object lhsV, Object rhsV) {
        Double lhs = Double.parseDouble(String.valueOf(lhsV));
        Double rhs = Double.parseDouble(String.valueOf(rhsV));
        return lhs < rhs;
    }

    @Override
    public Boolean equal(Object lhsV, Object rhsV) {
        Double lhs = Double.parseDouble(String.valueOf(lhsV));
        Double rhs = Double.parseDouble(String.valueOf(rhsV));
        return Objects.equals(lhs, rhs);
    }

    @Override
    public String formatPrint(Object object) {
        return object.toString();
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
    public String formatEnsures(String variableName, Object object) {
        if (object == null) {
            return null;
        }
        double v = Double.parseDouble(object.toString());

        return String.format("(%.2f < %s < %.2f)", v - 0.1, variableName, v + 0.1);
    }
}
