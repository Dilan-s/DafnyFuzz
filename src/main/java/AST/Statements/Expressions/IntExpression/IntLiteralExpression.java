package AST.Statements.Expressions.IntExpression;

import AST.Statements.Expressions.Expression;
import AST.Statements.Statement;
import AST.Statements.Type.ValueGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class IntLiteralExpression extends Expression<Integer> {

    private final int value;

    public IntLiteralExpression(Random random, Map<String, Statement> symbolTable) {
        this.value = ValueGenerator.generateIntValue(random);
    }

    @Override
    public List<Statement> getStatements() {
        return new ArrayList<>();
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder();
        representation.append(value);
        return representation.toString();
    }
}
