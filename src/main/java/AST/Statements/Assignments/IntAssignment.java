package AST.Statements.Assignments;

import AST.Statements.Expressions.Expression;
import AST.Statements.Statement;
import AST.Statements.Type.PrimitiveTypes;
import AST.StringUtils.Constants;
import AST.Statements.Type.ValueGenerator;
import AST.Variables.VariableAssigner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class IntAssignment extends Assignment<Integer> {

    private final Random random;
    private Expression<Integer> variableValue;

    public IntAssignment(Random random) {
        this(random, true);
    }

    public IntAssignment(Random random, boolean printAssignment) {
        super(VariableAssigner.generateIntVariableName(), printAssignment);
        this.random = random;
    }

    @Override
    public List<String> generateCode() {
        List<String> statements = new ArrayList<>();

        StringBuilder code = new StringBuilder();
        code.append(Constants.VARIABLE);
        code.append(getVariableName());
        code.append(PrimitiveTypes.INT.getTypeIndicatorString());
        code.append(Constants.ASSIGNMENT);
        code.append(variableValue);
        code.append(Constants.END_OF_LINE);
        statements.add(code.toString());

        if (super.printAssignment) {
            statements.add(super.printResult());
        }

        return statements;
    }

    @Override
    public List<Statement> generateValue(Map<String, Statement> symbolTable) {
        variableValue = ValueGenerator.generateIntExpressionValue(random, symbolTable);
        return variableValue.getStatements();
    }

    @Override
    public Expression<Integer> getExpression() {
        return variableValue;
    }
}
