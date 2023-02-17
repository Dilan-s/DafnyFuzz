package AST.Statements.Expressions.BoolExpression;

import AST.Statements.Expressions.Expression;
import AST.Statements.Statement;
import AST.Statements.Type.ValueGenerator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NegationExpression extends Expression<Boolean> {

    private static final String NEGATION = "!";
    private final Expression<Boolean> expression;

    public NegationExpression(Random random, Map<String, Statement> symbolTable) {
        this.expression = ValueGenerator.generateBoolExpressionValue(random, symbolTable);
    }

    @Override
    public List<Statement> getStatements() {
        return expression.getStatements();
    }

    @Override
    public Boolean getValue() {
        return !expression.getValue();
    }

    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder();
        representation.append(NEGATION);
        representation.append(expression);
        return representation.toString();
    }
}
