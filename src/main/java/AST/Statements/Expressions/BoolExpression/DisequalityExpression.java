package AST.Statements.Expressions.BoolExpression;

import AST.Statements.Expressions.Expression;
import AST.Statements.Statement;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DisequalityExpression extends Expression<Boolean> {

    public DisequalityExpression(Random random, Map<String, Statement> symbolTable) {

    }

    @Override
    public List<Statement> getStatements() {
        return null;
    }

    @Override
    public Boolean getValue() {
        return null;
    }
}
