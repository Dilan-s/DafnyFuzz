package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CharLiteral extends BaseExpression {

    private final char value;
    private final Type type;
    private SymbolTable symbolTable;

    public CharLiteral(Type type, SymbolTable symbolTable, char value) {
        super();
        this.type = type;
        this.symbolTable = symbolTable;
        this.value = value;
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
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
    protected List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s, boolean unused) {
        List<Object> r = new ArrayList<>();
        r.add(value);
        return r;
    }

    @Override
    public List<Statement> expand() {
        return new ArrayList<>();
    }
}
