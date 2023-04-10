package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BoolLiteral implements Expression {

    private final Type type;
    private boolean value;
    private SymbolTable symbolTable;

    public BoolLiteral(Type type, SymbolTable symbolTable, boolean value) {
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
        return String.valueOf(value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BoolLiteral)) {
            return false;
        }
        BoolLiteral other = (BoolLiteral) obj;
        return value == other.value;
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap) {
        List<Object> r = new ArrayList<>();
        r.add(value);
        return r;
    }
}
