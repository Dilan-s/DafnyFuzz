package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DSetLiteral implements Expression {

    private final Type type;
    private final List<Expression> values;
    private SymbolTable symbolTable;

    public DSetLiteral(SymbolTable symbolTable, Type type, List<Expression> values) {
        this.symbolTable = symbolTable;
        this.type = type;
        this.values = values;
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    @Override
    public List<String> toCode() {
        return values.stream()
            .map(Expression::toCode)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {
    }

    @Override
    public String toString() {
        String value = values.stream()
            .map(Expression::toString)
            .collect(Collectors.joining(", "));
        return String.format("{%s}", value);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DSetLiteral)) {
            return false;
        }
        DSetLiteral other = (DSetLiteral) obj;
        if (values.size() != other.values.size()) {
            return false;
        }

        for (int i = 0; i < values.size(); i++) {
            if (!values.get(i).equals(other.values.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap) {
        List<Object> r = new ArrayList<>();

        Set<Object> s = new HashSet<>();
        for (Expression e : values) {
            List<Object> value = e.getValue(paramsMap);
            for (Object v : value) {
                if (v == null) {
                    r.add(null);
                    return r;
                }
                s.add(v);
            }
        }

        r.add(s);
        return r;
    }
}
