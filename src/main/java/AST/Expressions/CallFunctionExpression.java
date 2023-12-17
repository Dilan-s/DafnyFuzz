package AST.Expressions;

import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.Statement;
import AST.SymbolTable.Function.Function;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CallFunctionExpression extends BaseExpression {

    private SymbolTable symbolTable;
    private Function function;
    private List<Expression> args;

    private List<Statement> assignments;
    private List<Variable> variables;
    private List<List<Statement>> expanded;

    private CallFunctionExpression(SymbolTable symbolTable, Function function) {
        super();
        this.symbolTable = symbolTable;
        this.function = function;

        this.assignments = new ArrayList<>();
        this.variables = new ArrayList<>();

        this.expanded = new ArrayList<>();
    }

    public CallFunctionExpression(SymbolTable symbolTable, Function function, VariableExpression var) {
        this(symbolTable, function);
        this.args = List.of(var);
        variables.add(var.getVariable());
    }

    public CallFunctionExpression(SymbolTable symbolTable, Function function, List<Expression> args) {
        this(symbolTable, function);
        this.args = args;

        args.forEach(e -> {
            Type type = e.getTypes().get(0);
            String var = VariableNameGenerator.generateVariableValueName(type, symbolTable);
            Variable variable = new Variable(var, type);
            variables.add(variable);

            AssignmentStatement stat = new AssignmentStatement(symbolTable, List.of(variable), e);
            assignments.add(stat);
        });
        assignments.forEach(s -> expanded.add(s.expand()));
    }

    @Override
    public List<Type> getTypes() {
        return List.of(function.getReturnType());
    }

    @Override
    public List<Statement> expand() {
        int i;
        for (i = 0; i < assignments.size(); i++) {
            Statement assignment = assignments.get(i);
            if (assignment.requireUpdate()) {
                expanded.set(i, assignment.expand());
            }
        }

        return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public boolean validForFunction() {
        return assignments.stream().anyMatch(Statement::validForFunction);
    }

    @Override
    public boolean requireUpdate() {
        return args.stream().anyMatch(Expression::requireUpdate);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", function.getName(), variables.stream()
            .map(Variable::getName)
            .collect(Collectors.joining(", ")));
    }

    @Override
    protected List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s, boolean unused) {
        List<Object> r = new ArrayList<>();

        List<Object> l = new ArrayList<>();
        for (Variable arg : variables) {
            List<Object> value = arg.getValue(paramsMap);
            for (Object v : value) {
                if (v == null) {
                    r.add(null);
                    return r;
                }
                l.add(v);
            }
        }
        return function.execute(variables, s);
    }
}
