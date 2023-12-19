package AST.Expressions.Function;

import AST.Expressions.Expression;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.SymbolTable.Function.Function;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CallBaseFunctionExpression extends CallFunctionExpression {

    public CallBaseFunctionExpression(SymbolTable symbolTable, Function function,
        List<Expression> args) {
        super(symbolTable, function, args);

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
    public String toString() {
        return String.format("%s(%s)", function.getName(), variables.stream()
            .map(Variable::getName)
            .collect(Collectors.joining(", ")));
    }

    @Override
    protected List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s,
        boolean unused) {
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
