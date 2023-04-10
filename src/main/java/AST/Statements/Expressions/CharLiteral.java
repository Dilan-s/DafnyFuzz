package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CharLiteral implements Expression {

    private final char value;
    private final Type type;
    private SymbolTable symbolTable;

    public CharLiteral(Type type, SymbolTable symbolTable, char value) {
        this.type = type;
        this.symbolTable = symbolTable;
        this.value = value;
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {
    }

    @Override
    public String toString() {
        return String.format("'%c'", value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CharLiteral)) {
            return false;
        }
        CharLiteral other = (CharLiteral) obj;
        return value == other.value;
    }
}
