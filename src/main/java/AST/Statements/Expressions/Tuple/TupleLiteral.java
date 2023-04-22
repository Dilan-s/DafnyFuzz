package AST.Statements.Expressions.Tuple;

import AST.Generator.GeneratorConfig;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.Expressions.Expression;
import AST.Statements.Statement;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.Tuple;
import AST.SymbolTable.Types.Variables.Variable;
import AST.SymbolTable.Types.Variables.VariableArrayIndex;
import AST.SymbolTable.Types.Variables.VariableDatatypeIndex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TupleLiteral implements Expression {

    private final SymbolTable symbolTable;
    private final Type type;
    private final List<Expression> values;
    private final Variable variable;
    private final Statement statement;

    public TupleLiteral(SymbolTable symbolTable, Type type, List<Expression> values) {
        this.symbolTable = symbolTable;
        this.type = type;
        this.values = values;

        this.variable = new Variable(VariableNameGenerator.generateVariableValueName(type, symbolTable), type);
        this.statement = new AssignmentStatement(symbolTable, List.of(variable), new TupleInitValues(values));
        generateAssignments();
    }

    private void generateAssignments() {
        Tuple t = (Tuple) this.type;

        for (int i = 0; i < values.size(); i++) {
            Type valType = t.getType(i);
            VariableDatatypeIndex v = new VariableDatatypeIndex(this.variable.getName(), valType, i);
            v.setDeclared();
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
        r.add(l);
        return r;
    }

    @Override
    public List<Statement> expand() {
        List<Statement> r = new ArrayList<>();
        r.addAll(values.stream()
            .map(Expression::expand)
            .flatMap(Collection::stream)
            .collect(Collectors.toList()));
        r.add(statement);
        return r;
    }

    private class TupleInitValues implements Expression {

        private final List<Expression> values;

        public TupleInitValues(List<Expression> values) {
            this.values = values;
        }

        @Override
        public List<Type> getTypes() {
            return List.of(type);
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
