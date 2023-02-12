package AST.Statements.Expressions.BoolExpression;

import AST.Statements.Statement;
import AST.Statements.Type.Type;
import AST.Statements.Type.ValueGenerator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NegationExpression extends BoolExpression {

    private static final String NEGATION = "!";
    private final BoolExpression expression;

    public NegationExpression(Random random, Map<String, Statement> symbolTable) {
        this.expression = ValueGenerator.generateBoolExpressionValue(random, symbolTable);
    }

    @Override
    public List<Statement> getStatements() {
        return expression.getStatements();
    }

    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder();
        representation.append(NEGATION);
        representation.append(expression);
        return representation.toString();
    }

    public static BoolExpression create(Random random, Map<String, Statement> symbolTable) {
        return new NegationExpression(random, symbolTable);
    }

}
