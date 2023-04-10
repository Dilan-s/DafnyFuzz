package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IntLiteral implements Expression {

    private final int value;
    private final boolean asHex;
    private final Type type;
    private SymbolTable symbolTable;

    public IntLiteral(Type type, SymbolTable symbolTable, int value, boolean asHex) {
        this.type = type;
        this.symbolTable = symbolTable;
        this.value = value;
        this.asHex = asHex;
    }

    public IntLiteral(Type type, SymbolTable symbolTable, int value) {
        this(type, symbolTable, value, false);
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
        if (asHex) {
            return String.format("0x%X", value);
        }
        return String.valueOf(value);
    }

    @Override
    public List<Statement> expand() {
        return new ArrayList<>();
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IntLiteral)) {
            return false;
        }
        IntLiteral other = (IntLiteral) obj;
        return value == other.value;
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap) {
        List<Object> r = new ArrayList<>();
        r.add(value);
        return r;
    }
}
