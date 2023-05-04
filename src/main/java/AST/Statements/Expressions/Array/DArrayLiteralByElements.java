package AST.Statements.Expressions.Array;

import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.Expressions.Expression;
import AST.Statements.Statement;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import AST.SymbolTable.Types.Variables.VariableArrayIndex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DArrayLiteralByElements implements Expression {

    private final SymbolTable symbolTable;
    private final Type type;
    private final List<Expression> values;
    private final Variable variable;
    private final Statement statement;
    private final List<Statement> assignments;

    private List<List<Statement>> expanded;

    public DArrayLiteralByElements(SymbolTable symbolTable, Type type, List<Expression> values) {
        this.symbolTable = symbolTable;
        this.type = type;
        this.values = values;

        this.variable = new Variable(VariableNameGenerator.generateVariableValueName(type, symbolTable), type);
        this.statement = new AssignmentStatement(symbolTable, List.of(variable), new ArrayInitValues(values));
        this.assignments = new ArrayList<>();
        generateAssignments();

        this.expanded = new ArrayList<>();
        expanded.add(statement.expand());
        assignments.forEach(v -> expanded.add(v.expand()));
    }

    private void generateAssignments() {
        DCollection collection = (DCollection) this.type;

        for (int i = 0; i < values.size(); i++) {
            VariableArrayIndex v = new VariableArrayIndex(variable, collection.getInnerType(), i);
            v.setDeclared();
            AssignmentStatement stat = new AssignmentStatement(symbolTable, List.of(v), values.get(i));

            assignments.add(stat);
        }
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    @Override
    public String toString() {
        return variable.getName();
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
    public List<Statement> expand() {
        int i = 0;
        if (statement.requireUpdate()) {
            expanded.set(i, statement.expand());
        }
        i++;
        for (int j = 0; j < assignments.size(); j++) {
            Statement value = assignments.get(j);
            if (value.requireUpdate()) {
                expanded.set(i + j, value.expand());
            }
        }
        return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public boolean requireUpdate() {
        return assignments.stream().anyMatch(Statement::requireUpdate) || statement.requireUpdate();
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
            DCollection t = (DCollection) type;
            return String.format("new %s[%d]", t.getInnerType().getVariableType(), values.size());
        }

        @Override
        public List<Statement> expand() {
            return new ArrayList<>();
        }

        @Override
        public boolean requireUpdate() {
            return false;
        }
    }
}
