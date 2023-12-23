package AST.Expressions.Function;

import AST.Expressions.BaseExpression;
import AST.Expressions.Expression;
import AST.Statements.Statement;
import AST.SymbolTable.Function.Function;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CallFunctionExpression extends BaseExpression {

    protected SymbolTable symbolTable;
    protected Function function;
    protected List<Expression> args;

    protected List<Statement> assignments;
    protected List<Variable> variables;
    protected List<List<Statement>> expanded;

    protected CallFunctionExpression(SymbolTable symbolTable, Function function,
        List<Expression> args) {
        super();
        this.symbolTable = symbolTable;
        this.function = function;
        this.args = args;

        this.assignments = new ArrayList<>();
        this.variables = new ArrayList<>();
        this.expanded = new ArrayList<>();
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
    public boolean validForFunctionBody() {
        return super.validForFunctionBody() && args.stream().allMatch(Expression::validForFunctionBody);
    }

    @Override
    public boolean requireUpdate() {
        return args.stream().anyMatch(Expression::requireUpdate);
    }

}
