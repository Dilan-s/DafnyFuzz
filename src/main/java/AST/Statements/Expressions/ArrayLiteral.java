package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.Generator.GeneratorConfig;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
            r.add(statement);
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

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s) {
        List<Object> r = new ArrayList<>();

        List<Object> l = new ArrayList<>();
        for (Expression exp : values) {
            List<Object> value = exp.getValue(paramsMap, s);
            for (Object v : value) {
                if (v == null) {
                    r.add(null);
                    return r;
                }
                l.add(v);
            }
        }
        r.add(new ArrayValue(variable.getName(), l));
        return r;
    }

    private class ArrayValue {
        String name;
        List<Object> contents;

        public ArrayValue(String name, List<Object> contents) {
            this.name = name;
            this.contents = contents;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, contents);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ArrayValue)) {
                return false;
            }

            ArrayValue other = (ArrayValue) obj;
            return other.name.equals(name) && other.contents.equals(contents);
        }

        @Override
        public String toString() {
            return contents.toString();
        }
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
        public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s) {
            List<Object> r = new ArrayList<>();

            List<Object> l = new ArrayList<>();
            for (Expression exp : values) {
                List<Object> value = exp.getValue(paramsMap, s);
                for (Object v : value) {
                    if (v == null) {
                        r.add(null);
                        return r;
                    }
                    l.add(v);
                }
            }
            r.add(new ArrayValue(variable.getName(), l));
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
        public List<String> toOutput() {
            Set<String> res = new HashSet<>();
            List<String> temp = new ArrayList<>();

            DCollection t = (DCollection) type;
            res.add(String.format("new %s[] [", t.getInnerType().getName()));

            boolean first = true;
            for (Expression exp : values) {
                List<String> expOptions = exp.toOutput();
                temp = new ArrayList<>();
                for (String f : res) {
                    for (String expOption : expOptions) {
                        if (!first) {
                            expOption = ", " + expOption;
                        }
                        String curr = f + expOption;
                        temp.add(curr);
                    }
                }
                if (expOptions.isEmpty()) {
                    temp.addAll(res);
                }
                first = false;
                res = new HashSet(temp);
            }

            temp = new ArrayList<>();
            for (String f : res) {
                temp.add(f + "]");
            }
            res = new HashSet(temp);

            List<String> r = new ArrayList<>(res);
            Collections.shuffle(r, GeneratorConfig.getRandom());
            return r.subList(0, Math.min(5, res.size()));
        }

        @Override
        public List<Statement> expand() {
            return new ArrayList<>();
        }

    }
}
