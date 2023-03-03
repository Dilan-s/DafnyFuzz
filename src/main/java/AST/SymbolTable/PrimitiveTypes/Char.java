package AST.SymbolTable.PrimitiveTypes;

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
    public Expression generateLiteral(Random random,
        SymbolTable symbolTable) {
        char c = (char) ('a' + random.nextInt(26));
        c += random.nextDouble() < PROB_UPPERCASE ? LOWER_TO_UPPER_SHIFT : 0;
        return new CharLiteral(c);
    }

    @Override
    public boolean operatorExists() {
        return true;
    }
}