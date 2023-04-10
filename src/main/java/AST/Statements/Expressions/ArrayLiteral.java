package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.Statement;
import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArrayLiteral implements Expression {

    private final Type type;
    private SymbolTable symbolTable;

    private Variable variable;
    private List<Expression> values;
    private AssignmentStatement statement;
    private boolean toAssign;

    public ArrayLiteral(SymbolTable symbolTable, Type type, List<Expression> values, boolean toAssign) {
        this.symbolTable = symbolTable;
        this.type = type;
        this.values = values;
        this.toAssign = toAssign;

        if (toAssign) {
            this.variable = new Variable(VariableNameGenerator.generateVariableValueName(type, symbolTable), type);
            statement = new AssignmentStatement(symbolTable, List.of(variable), new ArrayInitValues(values));
        }
    }

    public ArrayLiteral(SymbolTable symbolTable, Type type, List<Expression> values) {
        this(symbolTable, type, values, true);
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    public void addValue(Expression expression) {
        values.add(expression);
    }

    @Override
    public List<Statement> expand() {
        List<Statement> r = new ArrayList<>();
        r.addAll(values.stream()
            .map(Expression::expand)
            .flatMap(Collection::stream)
            .collect(Collectors.toList()));
        if (toAssign) {
            r.addAll(statement.expand());
        }
        return r;
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {
    }

    @Override
    public String toString() {
        return variable.getName();
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, values);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ArrayLiteral)) {
            return false;
        }
        ArrayLiteral other = (ArrayLiteral) obj;

        if (other.values.size() != values.size()) {
            return false;
        }

        for (int i = 0; i < values.size(); i++) {
            if (!other.values.get(i).equals(values.get(i))) {
                return false;
            }
        }
        return true;
    }

    private class ArrayInitValues implements Expression {

        private final List<Expression> values;

        public ArrayInitValues(List<Expression> values) {
            this.values = values;
        }

        @Override
        public List<Type> getTypes() {
            return List.of(type);
        }

        @Override
        public void semanticCheck(Method method) throws SemanticException {
        }

        @Override
        public List<Object> getValue(Map<Variable, Variable> paramsMap) {
            List<Object> r = new ArrayList<>();

            List<Object> l = new ArrayList<>();
            for (Expression exp : values) {
                List<Object> value = exp.getValue(paramsMap);
                for (Object v : value) {
                    if (v == null) {
                        r.add(null);
                        return r;
                    }
                    l.add(v);
                }
            }
            r.add(l);
            return r;
        }

        @Override
        public String toString() {
            String value = values.stream()
                .map(Expression::toString)
                .collect(Collectors.joining(", "));
            DCollection t = (DCollection) type;
            return String.format("new %s[] [%s]", t.getInnerType().getName(), value);
        }

        @Override
        public List<Statement> expand() {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap) {
        List<Object> r = new ArrayList<>();

        List<Object> l = new ArrayList<>();
        for (Expression exp : values) {
            List<Object> value = exp.getValue(paramsMap);
            for (Object v : value) {
                if (v == null) {
                    r.add(null);
                    return r;
                }
                l.add(v);
            }
        }
        r.add(l);
        return r;
    }
}
