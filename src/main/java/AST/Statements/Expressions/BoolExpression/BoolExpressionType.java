package AST.Statements.Expressions.BoolExpression;

import AST.Statements.Expressions.ExpressionCreator;
import AST.Statements.Statement;
import AST.Statements.Type.Type;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public enum BoolExpressionType {
    CONJUNCT(ConjunctExpression::create),
    DIJUNCT(DisjunctExpression::create),
//    DISEQUALITY(DisequalityExpression::create),
//    EQUALITY(EqualityExpression::create),
    IF_AND_ONLY_IF(IfAndOnlyIfExpression::create),
    IMPLIES(ImpliesExpression::create),
    LITERAL(BoolLiteralExpression::create),
    NEGATION(NegationExpression::create),
    REVERSE_IMPLICATION(ReverseImpliesExpression::create),
    VARIABLE(BoolVariableExpression::create),
    ;

    private ExpressionCreator<BoolExpression> creator;

    BoolExpressionType(ExpressionCreator<BoolExpression> creator) {

        this.creator = creator;
    }

    public BoolExpression create(Random random, Map<String, Statement> symbolTable) {
        return creator.apply(random, symbolTable);
    }
}