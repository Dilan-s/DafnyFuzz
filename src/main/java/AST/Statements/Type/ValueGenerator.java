package AST.Statements.Type;

import AST.Statements.Expressions.BoolExpression.BoolExpressionType;
import AST.Statements.Expressions.BoolExpression.BoolLiteralExpression;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.IntExpression.IntExpressionType;
import AST.Statements.Expressions.IntExpression.IntLiteralExpression;
import AST.Statements.Statement;
import java.util.Map;
import java.util.Random;

public class ValueGenerator {

    private static final double PROB_LITERAL = 0.4;

    public static Integer generateIntValue(Random random) {
        return random.nextInt();
    }

    public static boolean generateBoolValue(Random random) {
        return random.nextBoolean();
    }

    public static Expression<Boolean> generateBoolExpressionValue(Random random, Map<String, Statement> symbolTable) {
        if (random.nextDouble() < PROB_LITERAL) {
            return new BoolLiteralExpression(random, symbolTable);
        }
        BoolExpressionType[] boolExpressionTypes = BoolExpressionType.values();
        int i = random.nextInt(boolExpressionTypes.length);

        return boolExpressionTypes[i].create(random, symbolTable);
    }

    public static Expression<Integer> generateIntExpressionValue(Random random,
        Map<String, Statement> symbolTable) {
        if (random.nextDouble() < PROB_LITERAL) {
            return new IntLiteralExpression(random, symbolTable);
        }
        IntExpressionType[] intExpressionTypes = IntExpressionType.values();
        int i = random.nextInt(intExpressionTypes.length);

        return intExpressionTypes[i].create(random, symbolTable);
    }
}
