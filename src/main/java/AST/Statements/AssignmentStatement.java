package AST.Statements;

import AST.Errors.SemanticException;
import AST.Generator.VariableNameGenerator;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AssignmentStatement implements Statement {

    private final SymbolTable symbolTable;
    private final List<Variable> variables;
    private final List<Expression> values;

    public AssignmentStatement(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.variables = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    public void addAssignment(Expression e) {
        List<Variable> variables = new ArrayList<>();
        for (Type t : e.getTypes()) {
            variables.add(new Variable(VariableNameGenerator.generateVariableValueName(t), t));
        }
        addAssignment(variables, e);
    }

    public void addAssignment(List<Variable> variablesToAssign, Expression expression) {
        variables.addAll(variablesToAssign);
        values.add(expression);
    }

    public void addAssignmentsToSymbolTable() {
        for (Variable variable : variables) {
            symbolTable.addVariable(variable);
            variable.setDeclared();
        }
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {
        List<Type> assignmentTypes = variables.stream().map(Variable::getType)
            .collect(Collectors.toList());
        List<Type> valueTypes = values.stream()
            .map(Expression::getTypes)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        if (valueTypes.size() > 1
            && values.stream()
            .map(Expression::getTypes)
            .anyMatch(x -> x.size() > 1)) {
            throw new SemanticException(
                "If more than 1 expression given, then they must all be 1 valued");
        }

        int noValues = valueTypes.size();
        int noAssignTypes = assignmentTypes.size();
        if (noValues != noAssignTypes) {
            throw new SemanticException(String.format(
                "Expected %d arguments but actually got %d arguments in return statement",
                noAssignTypes, noValues));
        }

        for (int i = 0; i < noValues; i++) {
            Type expressionType = valueTypes.get(i);
            Type assignType = assignmentTypes.get(i);

            if (!assignType.isSameType(expressionType)) {
                throw new SemanticException(
                    String.format("Expected %dth argument to be %s but actually go %s", i,
                        assignType.getName(), expressionType.getName()));
            }
        }

        for (Expression e : values) {
            e.semanticCheck(method);
        }
    }

    @Override
    public List<String> toCode() {
        List<String> code = new ArrayList<>();

        List<String> codeForExpressions = values.stream()
            .map(Expression::toCode)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        code.addAll(codeForExpressions);

        String lhs = variables.stream()
            .map(Variable::toString)
            .collect(Collectors.joining(", "));

        String rhs = values.stream()
            .map(Expression::toString)
            .collect(Collectors.joining(", "));
        code.add(String.format("var %s := %s;\n", lhs, rhs));

        return code;
    }
}
