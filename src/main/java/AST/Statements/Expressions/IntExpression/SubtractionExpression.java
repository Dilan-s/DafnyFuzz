package AST.Statements.Expressions.IntExpression;

import AST.Statements.Expressions.Expression;
import AST.Statements.Statement;
import AST.Statements.Type.ValueGenerator;
import AST.StringUtils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SubtractionExpression extends Expression<Integer> {
    //Avoid underflow/overflow

    public static final String MINUS = " - ";
    private final Expression<Integer> lhs;
    private final Expression<Integer> rhs;

    public SubtractionExpression(Random random, Map<String, Statement> symbolTable) {
        this.lhs = ValueGenerator.generateIntExpressionValue(random, symbolTable);
        this.rhs = ValueGenerator.generateIntExpressionValue(random, symbolTable);
    }

    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        statements.addAll(lhs.getStatements());
        statements.addAll(rhs.getStatements());
        return statements;
    }

    @Override
    public Integer getValue() {
        return lhs.getValue() - rhs.getValue();
    }

    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder();
        representation.append(Constants.OPENING_ARGS);
        representation.append(lhs);
        representation.append(MINUS);
        representation.append(rhs);
        representation.append(Constants.CLOSING_ARGS);
        return representation.toString();
    }


}
