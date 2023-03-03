package AST.Statements.Expressions.Operator;

import AST.Errors.SemanticException;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.Method;
import AST.SymbolTable.PrimitiveTypes.Bool;
import AST.SymbolTable.PrimitiveTypes.Int;
import AST.SymbolTable.Type;
import java.util.List;
import java.util.stream.Collectors;

public interface Operator {

    String formExpression(List<Expression> args);

    List<Type> getTypes();

    void semanticCheck(Method method, List<Expression> expressions) throws SemanticException;

    List<Type> getTypeArgs();

    int getNumberOfArgs();

    default boolean returnType(Type type) {
        return getTypes().stream().anyMatch(t -> t.isSameType(type));
    }

    static void numericTypeCheck(Expression lhs, Expression rhs, String operator)
        throws SemanticException {
        List<Type> leftTypes = lhs.getTypes();
        List<Type> rightTypes = rhs.getTypes();

        if (leftTypes.size() != 1) {
            throw new SemanticException(
                String.format("Expected lhs to have 1 type, but instead got types %s",
                    leftTypes.stream().map(Type::getName).collect(Collectors.joining(", ")))
            );
        }
        if (rightTypes.size() != 1) {
            throw new SemanticException(
                String.format("Expected rhs to have 1 type, but instead got types %s",
                    rightTypes.stream().map(Type::getName).collect(Collectors.joining(", ")))
            );
        }
        Type leftType = leftTypes.get(0);
        Type rightType = rightTypes.get(0);
        if (!(leftType.isSameType(rightType))) {
            throw new SemanticException(
                String.format("Expected arguments to %s to be (Num, Num) but actually got (%s, %s)",
                    operator, leftType.getName(), rightType.getName()));
        }
    }

    static void boolTypeCheck(Expression lhs, Expression rhs, String operator)
        throws SemanticException {
        List<Type> leftTypes = lhs.getTypes();
        List<Type> rightTypes = rhs.getTypes();

        if (leftTypes.size() != 1) {
            throw new SemanticException(
                String.format("Expected lhs to have 1 type, but instead got types %s",
                    leftTypes.stream().map(Type::getName).collect(Collectors.joining(", ")))
            );
        }
        if (rightTypes.size() != 1) {
            throw new SemanticException(
                String.format("Expected rhs to have 1 type, but instead got types %s",
                    rightTypes.stream().map(Type::getName).collect(Collectors.joining(", ")))
            );
        }
        Type leftType = leftTypes.get(0);
        Type rightType = rightTypes.get(0);
        if (!(leftType.isSameType(new Bool()) && rightType.isSameType(new Bool()))) {
            throw new SemanticException(String.format(
                "Expected arguments to %s to be (Bool, Bool) but actually got (%s, %s)", operator,
                leftType.getName(), rightType.getName()));
        }
    }

}
