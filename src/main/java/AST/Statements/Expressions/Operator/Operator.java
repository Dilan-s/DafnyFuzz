package AST.Statements.Expressions.Operator;

import AST.Errors.SemanticException;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface Operator {

    String formExpression(List<Expression> args);

    List<Type> getType();

    void semanticCheck(Method method, List<Expression> expressions) throws SemanticException;

    List<List<Type>> getTypeArgs();

    int getNumberOfArgs();

    default boolean returnType(Type type) {
        return getType().stream().anyMatch(t -> t.equals(type));
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
        if (!(leftType.equals(rightType))) {
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
        if (!(leftType.equals(new Bool()) && rightType.equals(new Bool()))) {
            throw new SemanticException(String.format(
                "Expected arguments to %s to be (Bool, Bool) but actually got (%s, %s)", operator,
                leftType.getName(), rightType.getName()));
        }
    }

    default List<Type> concreteType(List<Type> types, SymbolTable symbolTable, Type expected) {
        RandomTypeGenerator typeGenerator = new RandomTypeGenerator();

        Type t = typeGenerator.generateTypes(1, symbolTable).get(0);
        List<Type> ret = new ArrayList<>();
        for (Type type: types) {
            if (type.isCollection()) {
                DCollection collection = (DCollection) type;
                ret.add(collection.setInnerType(t.concrete(symbolTable)).concrete(symbolTable));
            } else {
                ret.add(type.concrete(symbolTable));
            }
        }
        return ret;
    }

    Object apply(List<Expression> args, Map<Variable, Variable> paramsMap);

    List<String> formOutput(List<Expression> args);

    default boolean requiresSafe(List<Object> vals) {
        return false;
    }
}
