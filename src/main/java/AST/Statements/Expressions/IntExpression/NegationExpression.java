package AST.Statements.Expressions.IntExpression;

import AST.Statements.Expressions.Expression;
import AST.Statements.Statement;
import AST.Statements.Type.ValueGenerator;
import AST.StringUtils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NegationExpression extends Expression<Integer> {
    //Avoid negation of negation

    public static final String NEGATION = " -";
    private final Expression<Integer> value;

    public NegationExpression(Random random, Map<String, Statement> symbolTable) {
        this.value = ValueGenerator.generateIntExpressionValue(random, symbolTable);
    }

    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        statements.addAll(value.getStatements());
        return statements;
    }

    @Override
    public Integer getValue() {
        return -1 * value.getValue();
    }

    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder();
        representation.append(Constants.OPENING_ARGS);
        representation.append(NEGATION);
        representation.append(Constants.OPENING_ARGS);
        representation.append(value);
        representation.append(Constants.CLOSING_ARGS);
        representation.append(Constants.CLOSING_ARGS);
        return representation.toString();
    }


}
