package AST.Statements;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AssignmentStatement extends BaseStatement {

    private final SymbolTable symbolTable;
    private final List<Variable> variables;
    private final List<Expression> values;
    private boolean declared;

    private List<List<Statement>> expanded;

    public AssignmentStatement(SymbolTable symbolTable, List<Variable> variables, List<Expression> values) {
        super();
        this.symbolTable = symbolTable;
        this.variables = variables;
        this.values = values;
        this.declared = variables.stream().allMatch(Variable::isDeclared);
        declareVariables();

        this.expanded = new ArrayList<>();
        values.forEach(e -> expanded.add(e.expand()));
        expanded.add(List.of(this));
    }

    public AssignmentStatement(SymbolTable symbolTable, List<Variable> variables, Expression value) {
        this(symbolTable, variables, List.of(value));
    }

    private void declareVariables() {
        for (int i = 0, variablesSize = variables.size(); i < variablesSize; i++) {
            Variable v = variables.get(i);
            v.setDeclared();
            symbolTable.addVariable(v);
        }

        /*
        List<Object> expValues = new ArrayList<>();
        for (Expression value : values) {
            List<Object> expressionValue = value.getValue();
            for (Object object : expressionValue) {
                expValues.add(object);
            }
        }

        for (int i = 0; i < variables.size(); i++) {
            Object expV = expValues.get(i);
            Variable v = variables.get(i);
            v.setValue(expV);
        }
        */
    }

    @Override
    public String toString() {
        String rhs = values.stream()
            .map(Expression::toString)
            .collect(Collectors.joining(", "));
        if (declared) {
            String lhs = variables.stream()
                .map(Variable::getName)
                .collect(Collectors.joining(", "));

            return String.format("%s := %s;", lhs, rhs);
        } else {
            String lhs = variables.stream()
                .map(Variable::toString)
                .collect(Collectors.joining(", "));

            return String.format("var %s := %s;", lhs, rhs);
        }
    }

    @Override
    public String minimizedTestCase() {
        String rhs = values.stream()
            .map(Expression::minimizedTestCase)
            .collect(Collectors.joining(", "));
        if (declared) {
            String lhs = variables.stream()
                .map(Variable::getName)
                .collect(Collectors.joining(", "));

            return String.format("%s := %s;", lhs, rhs);
        } else {
            String lhs = variables.stream()
                .map(Variable::toString)
                .collect(Collectors.joining(", "));

            return String.format("var %s := %s;", lhs, rhs);
        }
    }

    @Override
    public List<String> toOutput() {
        Set<String> res = new HashSet<>();
        List<String> temp = new ArrayList<>();

        if (declared) {
            String lhs = variables.stream()
                .map(Variable::getName)
                .collect(Collectors.joining(", "));

            res.add(String.format("%s := ", lhs));
        } else {
            String lhs = variables.stream()
                .map(Variable::toString)
                .collect(Collectors.joining(", "));

            res.add(String.format("var %s := ", lhs));
        }

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
            res = new HashSet<>(temp);
        }

        temp = new ArrayList<>();
        for (String f : res) {
            temp.add(f + ";");
        }
        res = new HashSet<>(temp);

        List<String> r = new ArrayList<>(res);
        Collections.shuffle(r, GeneratorConfig.getRandom());
        return r.subList(0, Math.min(5, res.size()));
    }

    @Override
    protected List<Object> execute(Map<Variable, Variable> paramMap, StringBuilder s, boolean unused) {
        List<Object> expValues = new ArrayList<>();
        for (Expression value : values) {
            List<Object> expressionValue = value.getValue(paramMap, s);
            for (Object object : expressionValue) {
                expValues.add(object);
            }
        }

        for (int i = 0; i < variables.size(); i++) {
            Object expV = expValues.get(i);
            Variable v = variables.get(i);
            v.setValue(expV);
        }
        return null;
    }

    @Override
    public boolean requireUpdate() {
        return values.stream().anyMatch(Expression::requireUpdate);
    }

    @Override
    public List<Statement> expand() {
        for (int i = 0, valuesSize = values.size(); i < valuesSize; i++) {
            Expression value = values.get(i);
            if (value.requireUpdate()) {
                expanded.set(i, value.expand());
            }
        }
        return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }
}
