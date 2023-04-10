package AST.SymbolTable.Types.PrimitiveTypes;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.CharLiteral;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.Objects;

public class Char implements BaseType {

    private static final double PROB_UPPERCASE = 0.5;
    private Character value;

    public Char() {
        this.value = null;
    }

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
        value = (char) ('a' + GeneratorConfig.getRandom().nextInt(26));
        if (GeneratorConfig.getRandom().nextDouble() < PROB_UPPERCASE) {
            value = Character.toUpperCase(value);
        }
        return new CharLiteral(this, symbolTable, value);
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable, Object value) {
        Type t = this.concrete(symbolTable);
        return new CharLiteral(t, symbolTable, (Character) value);
    }

    @Override
    public boolean operatorExists() {
        return true;
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        return new Char();
    }

    @Override
    public Boolean lessThan(Object lhsV, Object rhsV) {
        Character lhs = (Character) lhsV;
        Character rhs = (Character) rhsV;
        return lhs < rhs;
    }

    @Override
    public Boolean equal(Object lhsV, Object rhsV) {
        Character lhs = (Character) lhsV;
        Character rhs = (Character) rhsV;
        return Objects.equals(lhs, rhs);
    }
}