package AST.SymbolTable.PrimitiveTypes;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.CharLiteral;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import java.util.Random;

public class Char implements Type {

    private static final double PROB_UPPERCASE = 0.5;
    public static final int LOWER_TO_UPPER_SHIFT = 'A' - 'a';

    @Override
    public String getName() {
        return "char";
    }

    @Override
    public boolean isSameType(Type other) {
        return other instanceof Char;
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        char c = (char) ('a' + GeneratorConfig.getRandom().nextInt(26));
        c += GeneratorConfig.getRandom().nextDouble() < PROB_UPPERCASE ? LOWER_TO_UPPER_SHIFT : 0;
        return new CharLiteral(c);
    }

    @Override
    public boolean operatorExists() {
        return true;
    }
}