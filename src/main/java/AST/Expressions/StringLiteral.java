package AST.Expressions;

import AST.Statements.Statement;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StringLiteral extends BaseExpression {

    private final String value;
    private final Type type;
    private SymbolTable symbolTable;

    public StringLiteral(Type type, SymbolTable symbolTable, String value) {
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
        return String.format("\"%s\"", value);
    }

    @Override
    public List<String> toOutput() {
        return List.of(toString(),
            String.format("[%s]",
                value.chars()
                    .mapToObj(x -> String.format("'%s'", (char) x)).collect(
                        Collectors.joining(", "))));
    }

    @Override
    protected List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s,
        boolean unused) {
        List<Object> r = new ArrayList<>();
        r.add(value);
        return r;
    }

    @Override
    public List<Statement> expand() {
        return new ArrayList<>();
    }
}
