package AST.SymbolTable.PrimitiveTypes;

import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.RealLiteral;
import AST.SymbolTable.Type;
import java.util.Random;

public class Real implements Type {

    private static final int MAX_DOUBLE = 30;
    public static final double PROB_NEGATION = 0.5;

    @Override
    public String getName() {
        return "real";
    }

    @Override
    public boolean isSameType(Type other) {
        return other instanceof Real;
    }

    @Override
    public Expression generateLiteral(Random random) {
        double value = random.nextDouble() * MAX_DOUBLE;
        value *= random.nextDouble() < PROB_NEGATION ? -1 : 1;
        return new RealLiteral(value);
    }

    @Override
    public boolean operatorExists() {
        return true;
    }

}
