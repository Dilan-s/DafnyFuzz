package AST.Statements.Assignments;

import AST.Statements.Expressions.Expression;
import AST.Statements.Statement;
import AST.Statements.Type.PrimitiveTypes;
import AST.Statements.Type.ValueGenerator;
import AST.StringUtils.Constants;
import AST.Variables.VariableAssigner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BoolAssignment extends Assignment<Boolean> {

    private final Random random;
    private Expression<Boolean> variableValue;

    public BoolAssignment(Random random) {
        this(random, true);
    }

    public BoolAssignment(Random random, boolean printAssignment) {
        super(VariableAssigner.generateBoolVariableName(), printAssignment);
        this.random = random;
    }

    @Override
    public List<String> generateCode() {
        List<String> statements = new ArrayList<>();

        StringBuilder code = new StringBuilder();
        code.append(Constants.VARIABLE);
        code.append(super.getVariableName());
        code.append(PrimitiveTypes.BOOL.getTypeIndicatorString());
        code.append(Constants.ASSIGNMENT);
        code.append(variableValue);
        code.append(Constants.END_OF_LINE);
        statements.add(code.toString());

        if (super.printAssignment) {
            statements.add(super.printResult());
        }

        return statements;
    }

    public String getVariableName() {
        return super.getVariableName();
    }

    public Expression<Boolean> getExpression() {
        return variableValue;
    }

    @Override
    public List<Statement> generateValue(Map<String, Statement> symbolTable) {
        this.variableValue = ValueGenerator.generateBoolExpressionValue(random, symbolTable);
        return variableValue.getStatements();
    }
}
