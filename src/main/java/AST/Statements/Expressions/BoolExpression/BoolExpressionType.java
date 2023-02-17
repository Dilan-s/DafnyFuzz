package AST.Statements.Expressions.BoolExpression;

import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.ExpressionCreator;
import AST.Statements.Expressions.VariableExpression;
import AST.Statements.Statement;
import java.util.Map;
import java.util.Random;

public enum BoolExpressionType {
    CONJUNCT(ConjunctExpression::new),
    DIJUNCT(DisjunctExpression::new),
    //    DISEQUALITY(DisequalityExpression::create),
    //    EQUALITY(EqualityExpression::create),
    IF_AND_ONLY_IF(IfAndOnlyIfExpression::new),
    IMPLIES(ImpliesExpression::new),
    LITERAL(BoolLiteralExpression::new),
    NEGATION(NegationExpression::new),
    REVERSE_IMPLICATION(ReverseImpliesExpression::new),
    VARIABLE(VariableExpression::createBoolVariable),
    ;

    private final ExpressionCreator<Expression<Boolean>> creator;

    BoolExpressionType(ExpressionCreator<Expression<Boolean>> creator) {
        this.creator = creator;
    }

    public Expression<Boolean> create(Random random, Map<String, Statement> symbolTable) {
        return creator.apply(random, symbolTable);
    }
}