package AST.SymbolTable.Types.PrimitiveTypes;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.RealLiteral;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;

public class Real implements BaseType {

    private static final int MAX_DOUBLE = 30;
    public static final double PROB_NEGATION = 0.5;

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
        double value = GeneratorConfig.getRandom().nextDouble() * MAX_DOUBLE;
        value *= GeneratorConfig.getRandom().nextDouble() < PROB_NEGATION ? -1 : 1;
        return new RealLiteral(symbolTable, value);
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
        return new Bool();
    }
}
