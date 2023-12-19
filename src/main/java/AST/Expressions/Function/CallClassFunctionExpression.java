package AST.Expressions.Function;

import AST.Expressions.Expression;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.Statement;
import AST.SymbolTable.Function.Function;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.DClass;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CallClassFunctionExpression extends CallFunctionExpression {

    private final Expression classExpression;
    private final AssignmentStatement classAssign;
    private final Variable classVariable;

    public CallClassFunctionExpression(SymbolTable symbolTable, Function function, Expression classExpression, List<Expression> args) {
        super(symbolTable, function, args);

        this.classExpression = classExpression;
        DClass dClass = classExpression.getTypes().get(0).asDClass();
        this.classVariable = new Variable(VariableNameGenerator.generateVariableValueName(dClass, symbolTable), dClass);
        this.classAssign = new AssignmentStatement(symbolTable, List.of(classVariable), classExpression);

        args.forEach(e -> {
            Type type = e.getTypes().get(0);
            String var = VariableNameGenerator.generateVariableValueName(type, symbolTable);
            Variable variable = new Variable(var, type);
            variables.add(variable);

            AssignmentStatement stat = new AssignmentStatement(symbolTable, List.of(variable), e);
            assignments.add(stat);
        });
        assignments.forEach(s -> expanded.add(s.expand()));
        expanded.add(classAssign.expand());
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
        if (classAssign.requireUpdate()) {
            expanded.set(i, classAssign.expand());
        }
        return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public boolean requireUpdate() {
        return super.requireUpdate() || classAssign.requireUpdate();
    }

    @Override
    public String toString() {
        return String.format("%s.%s(%s)", classVariable.getName(), function.getName(), variables.stream()
            .map(Variable::getName)
            .collect(Collectors.joining(", ")));
    }

    @Override
    protected List<Object> getValue(Map<Variable, Variable> paramMap, StringBuilder s,
        boolean unused) {
        function.assignThis(classVariable);
        List<Object> r = new ArrayList<>();

        List<Object> l = new ArrayList<>();
        for (Variable arg : variables) {
            List<Object> value = arg.getValue(paramMap);
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
