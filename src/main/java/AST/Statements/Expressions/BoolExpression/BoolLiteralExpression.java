package AST.Statements.Expressions.BoolExpression;

import AST.Statements.Statement;
import AST.Statements.Type.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BoolLiteralExpression extends BoolExpression {

    private final boolean value;

    public BoolLiteralExpression(Random random) {
        this.value = random.nextBoolean();
    }

    @Override
    public List<Statement> getStatements() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder();
        representation.append(value);
        return representation.toString();
    }

    public static BoolExpression create(Random random, Map<String, Statement> symbolTable) {
        return new BoolLiteralExpression(random);
    }
}
