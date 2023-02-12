package AST.Statements.Assignments;

import AST.Statements.Statement;
import AST.Statements.Type.PrimitiveTypes;
import AST.Statements.Type.Type;
import AST.StringUtils.Constants;
import AST.Statements.Type.ValueGenerator;
import AST.Variables.VariableAssigner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class IntAssignment extends Assignment {

    private final Random random;
    private Integer variableValue;

    public IntAssignment(Random random) {
        this(random, true);
    }

    public IntAssignment(Random random, boolean printAssignment) {
        super(VariableAssigner.generateIntVariableName(), printAssignment);
        this.random = random;
    }

    @Override
    public String generateCode() {
        StringBuilder code = new StringBuilder();
        code.append(Constants.VARIABLE);
        code.append(variableName);
        code.append(PrimitiveTypes.INT.getTypeIndicatorString());
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
        variableValue = ValueGenerator.generateIntValue(random);
        return new ArrayList<>();
    }
}
