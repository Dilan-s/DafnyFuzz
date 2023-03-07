package AST.SymbolTable.PrimitiveTypes;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.RealLiteral;
import AST.SymbolTable.SymbolTable.SymbolTable;
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
    public Expression generateLiteral(SymbolTable symbolTable) {
        double value = GeneratorConfig.getRandom().nextDouble() * MAX_DOUBLE;
        value *= GeneratorConfig.getRandom().nextDouble() < PROB_NEGATION ? -1 : 1;
        return new RealLiteral(value);
    }

    @Override
    public boolean operatorExists() {
        return true;
    }

    @Override
    public boolean isPrintable() {
        return false;
    }
}
