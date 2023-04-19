package AST.Statements.Expressions.Array;

import AST.Errors.SemanticException;
import AST.Generator.GeneratorConfig;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.Expressions.Expression;
import AST.Statements.Statement;
import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import AST.SymbolTable.Types.Variables.VariableIndex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DArrayLiteralInline implements Expression {

    private final Type type;
    private final List<Variable> assignments;
    private SymbolTable symbolTable;

    private Variable variable;
    private List<Expression> values;
    private Statement statement;

    public DArrayLiteralInline(SymbolTable symbolTable, Type type, List<Expression> values) {
        this.symbolTable = symbolTable;
        this.type = type;
        this.values = values;

        this.variable = new Variable(VariableNameGenerator.generateVariableValueName(type, symbolTable), type);
        this.statement = new AssignmentStatement(symbolTable, List.of(variable), new ArrayInitValues(values));
        this.assignments = new ArrayList<>();

        generateAssignments();
    }

    private void generateAssignments() {
        DCollection t = (DCollection) this.type;
        Type valType = t.getInnerType();

        for (int i = 0; i < values.size(); i++) {
            VariableIndex v = new VariableIndex(this.variable.getName(), valType, i);
            v.setDeclared();
            symbolTable.addVariable(v);
            this.assignments.add(v);
        }
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
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

    @Override
    public String toString() {
        return variable.getName();
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s) {
        List<Object> r = new ArrayList<>();


        List<Object> l = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            Expression exp = values.get(i);
            List<Object> value = exp.getValue(paramsMap, s);
            for (Object v : value) {
                if (v == null) {
                    r.add(null);
                    return r;
                }
                l.add(v);
            }
            Variable v = assignments.get(i);
            v.setValue(value.get(0));
        }
        r.add(new ArrayValue(variable.getName(), l));
        return r;
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
            return String.format("new %s[%d] [%s]", t.getInnerType().getName(), values.size(), value);
        }

        @Override
        public List<String> toOutput() {
            Set<String> res = new HashSet<>();
            List<String> temp = new ArrayList<>();

            DCollection t = (DCollection) type;
            res.add(String.format("new %s[%d] [", t.getInnerType().getName(), values.size()));

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
