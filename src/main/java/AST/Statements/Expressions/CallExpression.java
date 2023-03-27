package AST.Statements.Expressions;

import AST.Errors.InvalidArgumentException;
import AST.Errors.SemanticException;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CallExpression implements Expression {

    private SymbolTable symbolTable;
    private final Method method;
    private final List<Variable> variables;
    private final List<Statement> assignments;
    private List<Variable> assignedVariables;

    public CallExpression(SymbolTable symbolTable, Method method) {
        this.symbolTable = symbolTable;
        this.method = method;
        this.variables = new ArrayList<>();
        this.assignments = new ArrayList<>();
        this.assignedVariables = new ArrayList<>();
    }

    public void addArg(List<Expression> expressions) throws InvalidArgumentException {
        for (Expression e : expressions) {
            addArg(e);
        }
    }

    public void addArg(Expression expression) throws InvalidArgumentException {

        if (expression.getTypes().size() != 1) {
            throw new InvalidArgumentException("Cannot add argument with more than 1 return value");
        }

        Type type = expression.getTypes().get(0);
        String var = VariableNameGenerator.generateVariableValueName(type);
        Variable variable = new Variable(var, type);
        variables.add(variable);

        AssignmentStatement stat = new AssignmentStatement(symbolTable);
        stat.addAssignment(List.of(variable), expression);
        stat.addAssignmentsToSymbolTable();

        assignments.add(stat);
    }


    @Override
    public List<Type> getTypes() {
        return method.getReturnTypes();
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {
        List<Type> methodTypes = method.getArgTypes();

        if (methodTypes.size() != variables.size()) {
            throw new SemanticException(
                String.format("Expected %d arguments but got %d arguments to method %s",
                    methodTypes.size(), variables.size(), method.getName()));
        }

        for (int i = 0; i < methodTypes.size(); i++) {
            Type methodType = methodTypes.get(i);
            Type varType = variables.get(i).getType();

            if (!methodType.equals(varType)) {
                throw new SemanticException(
                    String.format("Expected %dth argument to be %s, but actually was %s type", i,
                        methodType.getName(), varType.getName()));
            }
        }

    }

    @Override
    public List<String> toCode() {
        AssignmentStatement stat = new AssignmentStatement(symbolTable);
        assignedVariables = new ArrayList<>();
        for (Type returnType : method.getReturnTypes()) {
            String var = VariableNameGenerator.generateVariableValueName(returnType);
            Variable variable = new Variable(var, returnType);
            assignedVariables.add(variable);
            symbolTable.addVariable(variable);
        }

        CallMethodExpression callMethodExpression = new CallMethodExpression(method, variables);
        stat.addAssignment(assignedVariables, callMethodExpression);
        stat.addAssignmentsToSymbolTable();

        assignments.add(stat);

        return assignments.stream()
            .map(Statement::toCode)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    @Override
    public boolean isValidReturn() {
        return false;
    }

    @Override
    public String toString() {
        return assignedVariables.stream()
            .map(Variable::getName)
            .collect(Collectors.joining(", "));
    }


    private static class CallMethodExpression implements Expression {

        private Method method;
        private List<Variable> args;

        public CallMethodExpression(Method method, List<Variable> args) {
            this.method = method;
            this.args = args;
        }

        @Override
        public List<Type> getTypes() {
            return method.getReturnTypes();
        }

        @Override
        public void semanticCheck(Method method) throws SemanticException {

        }

        @Override
        public String toString() {
            return String.format("%s(%s)", method.getName(), args.stream()
                .map(Variable::getName)
                .collect(Collectors.joining(", ")));
        }
    }
}
