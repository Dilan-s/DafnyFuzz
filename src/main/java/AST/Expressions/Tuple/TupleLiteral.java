package AST.Expressions.Tuple;

import AST.Generator.GeneratorConfig;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Expressions.BaseExpression;
import AST.Expressions.Expression;
import AST.Statements.Statement;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.Tuple;
import AST.SymbolTable.Types.Variables.Variable;
import AST.SymbolTable.Types.Variables.VariableTupleIndex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TupleLiteral extends BaseExpression {

    private final SymbolTable symbolTable;
    private final Type type;
    private final List<Expression> values;
    private final Variable variable;
    private final AssignmentStatement statement;

    private List<List<Statement>> expanded;

    public TupleLiteral(SymbolTable symbolTable, Type type, List<Expression> values) {
        super();
        this.symbolTable = symbolTable;
        this.type = type;
        this.values = values;

        this.variable = new Variable(VariableNameGenerator.generateVariableValueName(type, symbolTable), type);
        this.statement = new AssignmentStatement(symbolTable, List.of(variable), new TupleInitValues(values));
        generateAssignments();

        this.expanded = new ArrayList<>();
        values.forEach(v -> expanded.add(v.expand()));
        expanded.add(statement.expand());
    }

    private void generateAssignments() {
        Tuple t = this.type.asTuple();

        for (int i = 0; i < values.size(); i++) {
            Type valType = t.getType(i);
            VariableTupleIndex v = new VariableTupleIndex(variable, valType, i);
            v.setDeclared();
            v.setConstant();
            new AssignmentStatement(symbolTable, List.of(v), values.get(i));
        }
    }

    @Override
    public String toString() {
        return variable.getName();
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    @Override
    protected List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s, boolean unused) {
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
        r.add(l);
        return r;
    }

    @Override
    public List<Statement> expand() {
        int i;
        for (i = 0; i < values.size(); i++) {
            Expression expression = values.get(i);
            if (expression.requireUpdate()) {
                expanded.set(i, expression.expand());
            }
        }
        if (statement.requireUpdate()) {
            expanded.set(i, statement.expand());
        }
        return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public boolean validForFunction() {
        return values.stream().anyMatch(Expression::validForFunction) || statement.validForFunction();
    }

    @Override
    public boolean requireUpdate() {
        return values.stream().anyMatch(Expression::requireUpdate) || statement.requireUpdate();
    }

    private class TupleInitValues extends BaseExpression {

        private final List<Expression> values;

        public TupleInitValues(List<Expression> values) {
            super();
            this.values = values;
        }

        @Override
        public List<Type> getTypes() {
            return List.of(type);
        }
        @Override
        public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s, boolean unused) {
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
            r.add(l);
            return r;
        }

        @Override
        public String toString() {
            String value = values.stream()
                .map(Expression::toString)
                .collect(Collectors.joining(", "));
            return String.format("(%s)", value);
        }

        @Override
        public String minimizedTestCase() {
            String value = values.stream()
                .map(Expression::minimizedTestCase)
                .collect(Collectors.joining(", "));
            return String.format("(%s)", value);
        }

        @Override
        public List<String> toOutput() {
            Set<String> res = new HashSet<>();
            List<String> temp = new ArrayList<>();

            res.add("(");

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
                Collections.shuffle(temp, GeneratorConfig.getRandom());
                temp = temp.subList(0, Math.min(5, temp.size()));
                res = new HashSet(temp);
            }

            temp = new ArrayList<>();
            for (String f : res) {
                temp.add(f + ")");
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
