package AST.Statements.Assignments;

import AST.Statements.Expressions.BoolExpression.BoolExpression;
import AST.Statements.Statement;
import AST.Statements.Type.PrimitiveTypes;
import AST.Statements.Type.Type;
import AST.Statements.Type.ValueGenerator;
import AST.StringUtils.Constants;
import AST.Variables.VariableAssigner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BoolAssignment extends Assignment {

    private final Random random;
    private BoolExpression variableValue;

    public BoolAssignment(Random random, boolean printAssignment) {
        super(VariableAssigner.generateBoolVariableName(), printAssignment);
        this.random = random;
    }

    public BoolAssignment(Random random) {
        this(random, true);
    }

    @Override
    public String generateCode() {
        StringBuilder code = new StringBuilder();
        code.append(Constants.VARIABLE);
        code.append(variableName);
        code.append(PrimitiveTypes.BOOL.getTypeIndicatorString());
        code.append(Constants.ASSIGNMENT);
        code.append(variableValue);
        code.append(Constants.END_OF_LINE);
        return code.toString();
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public List<Statement> generateValue(Map<String, Statement> symbolTable) {
        this.variableValue = ValueGenerator.generateBoolExpressionValue(random, symbolTable);
//        this.variableValue = ValueGenerator.generateBoolValue(random, symbolTable);
        return variableValue.getStatements();
    }
}
