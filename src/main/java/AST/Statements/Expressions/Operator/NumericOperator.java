package AST.Statements.Expressions.Operator;

import AST.Errors.SemanticException;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.Method;
import AST.SymbolTable.PrimitiveTypes.Bool;
import AST.SymbolTable.PrimitiveTypes.Char;
import AST.SymbolTable.PrimitiveTypes.Int;
import AST.SymbolTable.Type;
import java.util.List;

public enum NumericOperator implements Operator {
    Plus("+", 2),
    Minus("-", 2),
    Times("*", 2) {
        @Override
        public List<Type> getTypeArgs() {
            return List.of(new Int());
        }
    },
    Divide("/", 2) {
        @Override
        public List<Type> getTypeArgs() {
            return List.of(new Int());
        }
    },
    Modulus("%", 2) {
        @Override
        public List<Type> getTypeArgs() {
            return List.of(new Int());
        }
    },
    ;

    private final String operator;
    private final int numberOfArgs;

    NumericOperator(String operator, int numberOfArgs) {
        this.operator = operator;
        this.numberOfArgs = numberOfArgs;
    }



    @Override
    public String formExpression(Expression lhs, Expression rhs) {
        return String.format("(%s %s %s)", lhs, operator, rhs);
    }

    @Override
    public Type getType() {
        return new Bool();
    }

    @Override
    public void semanticCheck(Method method, Expression lhs, Expression rhs) throws SemanticException {
        typeCheck(lhs, rhs);
    }

    @Override
    public List<Type> getTypeArgs() {
        return List.of(new Int());
    }

    public void typeCheck(Expression lhs, Expression rhs) throws SemanticException {
        Operator.numericTypeCheck(lhs, rhs, operator);
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}
