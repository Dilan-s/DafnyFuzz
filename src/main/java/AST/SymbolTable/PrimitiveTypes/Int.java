package AST.SymbolTable.PrimitiveTypes;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.IntLiteral;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import java.util.Random;

public class Int implements Type {

    private static final int MAX_INT = 30;
    private int max;
    public static final double PROB_NEGATION = 0.1;

    public Int(int max) {
        this.max = max;
    }

    public Int() {
        this.max = MAX_INT;
    }

    @Override
    public String getName() {
        return "int";
    }

    @Override
    public boolean isSameType(Type other) {
        return other instanceof Int;
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        int value = GeneratorConfig.getRandom().nextInt(max);
        value *= GeneratorConfig.getRandom().nextDouble() < PROB_NEGATION ? -1 : 1;
        return new IntLiteral(value, value > 0 &&  GeneratorConfig.getRandom().nextBoolean());
    }

    @Override
    public boolean operatorExists() {
        return true;
    }
}
