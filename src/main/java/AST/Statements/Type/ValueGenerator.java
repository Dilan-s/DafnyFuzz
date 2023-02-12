package AST.Statements.Type;

import AST.Statements.Expressions.BoolExpression.BoolExpression;
import AST.Statements.Expressions.BoolExpression.BoolExpressionType;
import AST.Statements.Expressions.BoolExpression.BoolLiteralExpression;
import AST.Statements.Statement;
import java.util.Map;
import java.util.Random;

public class ValueGenerator {

    private static final double PROB_LITERAL = 1./3;

    public static Integer generateIntValue(Random random) {
        return random.nextInt();
    }

    public static boolean generateBoolValue(Random random, Map<String, Type> symbolTable) {
        return random.nextBoolean();
    }

    public static BoolExpression generateBoolExpressionValue(Random random, Map<String, Statement> symbolTable) {
        if (random.nextDouble() < PROB_LITERAL) {
            return new BoolLiteralExpression(random);
        }
        BoolExpressionType[] boolExpressionTypes = BoolExpressionType.values();
        int i = random.nextInt(boolExpressionTypes.length);
        BoolExpression expression = boolExpressionTypes[i].create(random, symbolTable);

        return expression;
    }
}
