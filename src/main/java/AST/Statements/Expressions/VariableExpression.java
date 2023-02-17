package AST.Statements.Expressions;

import AST.Statements.Assignments.Assignment;
import AST.Statements.Assignments.AssignmentCreator;
import AST.Statements.Assignments.BoolAssignment;
import AST.Statements.Assignments.IntAssignment;
import AST.Statements.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class VariableExpression<T> extends Expression<T> {

    private static final double REUSE_VARIABLE = 0.6;
    private final List<Statement> statements;
    private Assignment<T> variable;
    private final Class<? extends Assignment<T>> assignmentClass;
    private final AssignmentCreator<T> creator;

    public VariableExpression(Random random, Map<String, Statement> symbolTable, Class<? extends Assignment<T>> assignmentClass, AssignmentCreator<T> creator) {
        this.assignmentClass = assignmentClass;
        this.creator = creator;
        this.statements = new ArrayList<>();
        statements.addAll(generateBoolVariable(random, symbolTable));

    }

    private List<Statement> generateBoolVariable(Random random, Map<String, Statement> symbolTable) {
        List<Assignment<T>> variables = new ArrayList<>();
        for (Entry<String, Statement> value : symbolTable.entrySet()) {
            if (this.assignmentClass.isInstance(value.getValue())) {
                variables.add((Assignment<T>) value.getValue());
            }
        }

        if (!variables.isEmpty() && random.nextDouble() < REUSE_VARIABLE) {
            int i = random.nextInt(variables.size());
            variable = variables.get(i);
            return new ArrayList<>();
        }

        variable = creator.apply(random, false);
        List<Statement> statements = variable.generateValue(symbolTable);
        symbolTable.put(variable.getVariableName(), variable);
        statements.add(variable);
        return statements;
    }

    @Override
    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public T getValue() {
        return variable.getExpression().getValue();
    }

    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder();
        representation.append(variable.getVariableName());
        return representation.toString();
    }

    public static Expression<Boolean> createBoolVariable(Random random, Map<String, Statement> symbolTable) {
        return new VariableExpression<Boolean>(random, symbolTable, BoolAssignment.class,
            BoolAssignment::new);
    }

    public static Expression<Integer> createIntVariable(Random random, Map<String, Statement> symbolTable) {
        return new VariableExpression<Integer>(random, symbolTable, IntAssignment.class,
            IntAssignment::new);
    }
}
