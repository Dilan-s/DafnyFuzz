package AST.Statements.Expressions.BoolExpression;

import AST.Statements.Statement;
import AST.Statements.Type.Type;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DisequalityExpression extends BoolExpression {

    public DisequalityExpression(Random random, Map<String, Statement> symbolTable) {

    }

    @Override
    public List<Statement> getStatements() {
        return null;
    }

    public static BoolExpression create(Random random, Map<String, Statement> symbolTable) {
        return new DisequalityExpression(random, symbolTable);
    }
}
