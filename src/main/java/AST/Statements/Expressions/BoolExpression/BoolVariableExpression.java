package AST.Statements.Expressions.BoolExpression;

import AST.Statements.Assignments.BoolAssignment;
import AST.Statements.Statement;
import AST.Statements.Type.PrimitiveTypes;
import AST.Statements.Type.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class BoolVariableExpression extends BoolExpression {

    private static final double REUSE_VARIABLE = 0.25;
    private final List<Statement> statements;
    private BoolAssignment boolVariable;

    public BoolVariableExpression(Random random, Map<String, Statement> symbolTable) {
        this.statements = new ArrayList<>();
        statements.addAll(generateBoolVariable(random, symbolTable));

    }

    private List<Statement> generateBoolVariable(Random random, Map<String, Statement> symbolTable) {
        List<String> boolVariables = new ArrayList<>();
        for (Entry<String, Statement> value : symbolTable.entrySet()) {
            if (value.getValue() instanceof BoolAssignment) {
                boolVariables.add(value.getKey());
            }
        }

        if (!boolVariables.isEmpty() && random.nextDouble() < REUSE_VARIABLE) {
            int i = random.nextInt(boolVariables.size());
            boolVariable = (BoolAssignment) symbolTable.get(boolVariables.get(i));
            return new ArrayList<>();
        }

        boolVariable = new BoolAssignment(random, false);
        List<Statement> statements = boolVariable.generateValue(symbolTable);
        symbolTable.put(boolVariable.getVariableName(), boolVariable);
        statements.add(boolVariable);
        return statements;
    }

    @Override
    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder();
        representation.append(boolVariable.getVariableName());
        return representation.toString();
    }
    public static BoolExpression create(Random random, Map<String, Statement> symbolTable) {
        return new BoolVariableExpression(random, symbolTable);
    }
}
