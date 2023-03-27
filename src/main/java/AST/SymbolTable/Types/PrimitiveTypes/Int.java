package AST.SymbolTable.Types.PrimitiveTypes;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.IntLiteral;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;

public class Int implements BaseType {

    private static final int MAX_INT = 30;
    private static final double PROB_HEX = 0.2;
    public static final double PROB_NEGATION = 0.1;
    private int max;

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
        int value = GeneratorConfig.getRandom().nextInt(max);
        value *= GeneratorConfig.getRandom().nextDouble() < PROB_NEGATION ? -1 : 1;
        return new IntLiteral(symbolTable, value, value > 0 &&  GeneratorConfig.getRandom().nextDouble() < PROB_HEX);
    }

    @Override
    public boolean operatorExists() {
        return true;
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        return new Bool();
    }
}
