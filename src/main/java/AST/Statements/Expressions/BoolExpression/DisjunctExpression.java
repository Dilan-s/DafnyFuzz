package AST.Statements.Expressions.BoolExpression;

import AST.Statements.Statement;
import AST.Statements.Type.Type;
import AST.Statements.Type.ValueGenerator;
import AST.StringUtils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DisjunctExpression extends BoolExpression {

    public static final String DISJUNCT = " || ";
    private final BoolExpression lhs;
    private final BoolExpression rhs;

    public DisjunctExpression(Random random, Map<String, Statement> symbolTable) {
        this.lhs = ValueGenerator.generateBoolExpressionValue(random, symbolTable);
        this.rhs = ValueGenerator.generateBoolExpressionValue(random, symbolTable);
    }

    @Override
    public List<Statement> getStatements() {
        List<Statement> statements = new ArrayList<>();
        statements.addAll(lhs.getStatements());
        statements.addAll(rhs.getStatements());
        return statements;
    }

    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder();
        representation.append(Constants.OPENING_ARGS);
        representation.append(lhs);
        representation.append(DISJUNCT);
        representation.append(rhs);
        representation.append(Constants.CLOSING_ARGS);
        return representation.toString();
    }

    public static BoolExpression create(Random random, Map<String, Statement> symbolTable) {
        return new DisjunctExpression(random, symbolTable);
    }
}
