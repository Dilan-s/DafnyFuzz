package AST.SymbolTable.Types.PrimitiveTypes;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.CharLiteral;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.AbstractType;
import AST.SymbolTable.Types.Type;
import java.util.Objects;

public class Char extends AbstractType implements BaseType {

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
        t.setValue(value);
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
    public boolean lessThanOrEqual(Type rhsT) {
        Char rhsChar = (Char) rhsT;
        return value <= rhsChar.value;
    }

    @Override
    public boolean lessThan(Type rhsT) {
        Char rhsChar = (Char) rhsT;
        return value <= rhsChar.value;
    }

    @Override
    public boolean equal(Type rhsT) {
        Char rhsChar = (Char) rhsT;
        return Objects.equals(value, rhsChar.value);
    }

    @Override
    public void setValue(Object value) {
        this.value = (Character) value;
    }

    @Override
    public Object getValue() {
        return value;
    }
}