package AST.SymbolTable.Types.PrimitiveTypes;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.CharLiteral;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;

public class Char implements BaseType {

    private static final double PROB_UPPERCASE = 0.5;
    public static final int LOWER_TO_UPPER_SHIFT = 'A' - 'a';

    @Override
    public String getName() {
        return "char";
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
        return other instanceof Char;
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        char c = (char) ('a' + GeneratorConfig.getRandom().nextInt(26));
        c += GeneratorConfig.getRandom().nextDouble() < PROB_UPPERCASE ? LOWER_TO_UPPER_SHIFT : 0;
        return new CharLiteral(symbolTable, c);
    }

    @Override
    public boolean operatorExists() {
        return true;
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        return new Char();
    }
}