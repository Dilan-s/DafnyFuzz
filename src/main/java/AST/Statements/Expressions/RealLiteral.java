package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealLiteral implements Expression {

    private final Type type;
    private final double value;
    private SymbolTable symbolTable;

    public RealLiteral(Type type, SymbolTable symbolTable, double value) {
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
        return String.format("%.2f", value);
    }

    @Override
    public int hashCode() {
        return String.format("%.2f", value).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RealLiteral)) {
            return false;
        }
        RealLiteral other = (RealLiteral) obj;
        return String.format("%.2f", value).equals(String.format("%.2f", other.value));
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap) {
        List<Object> r = new ArrayList<>();
        r.add(value);
        return r;
    }

    @Override
    public List<Statement> expand() {
        return new ArrayList<>();
    }
}
