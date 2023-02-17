package AST.Statements.Expressions.IntExpression;

import AST.Statements.Expressions.VariableExpression;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.ExpressionCreator;
import AST.Statements.Statement;
import java.util.Map;
import java.util.Random;

public enum IntExpressionType {
    PLUS(PlusExpression::new),
    SUBTRACTION(SubtractionExpression::new),
    MULTIPLICATION(MultiplicationExpression::new),
    DIVISION(DivisionExpression::new),
    MODULUS(ModulusExpression::new),
    NEGATION(NegationExpression::new),
    LITERAL(IntLiteralExpression::new),
    VARIABLE(VariableExpression::createIntVariable),
    ;

    private final ExpressionCreator<Expression<Integer>> creator;

    IntExpressionType(ExpressionCreator<Expression<Integer>> creator) {
        this.creator = creator;
    }

    public Expression<Integer> create(Random random, Map<String, Statement> symbolTable) {
        return creator.apply(random, symbolTable);
    }
}