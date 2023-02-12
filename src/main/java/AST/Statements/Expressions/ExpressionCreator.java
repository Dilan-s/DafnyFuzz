package AST.Statements.Expressions;

import AST.Statements.Statement;
import AST.Statements.Type.Type;
import java.util.Map;
import java.util.Random;

@FunctionalInterface
public interface ExpressionCreator<T> {
    public T apply(Random random, Map<String, Statement> symbolTable);

}
