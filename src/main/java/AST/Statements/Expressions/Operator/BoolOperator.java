package AST.Statements.Expressions.Operator;

import AST.Errors.SemanticException;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.Method;
import AST.SymbolTable.PrimitiveTypes.Bool;
import AST.SymbolTable.PrimitiveTypes.Char;
import AST.SymbolTable.PrimitiveTypes.Int;
import AST.SymbolTable.Type;
import java.util.List;

public enum BoolOperator implements Operator {
    Equivalence("<==>", 2),
    Implies("==>", 2),
    ReverseImplies("<==", 2),
    And("&&", 2),
    Or("||", 2),
    Equals("==", 2) {
        @Override
        public List<Type> getTypeArgs() {
            return List.of(new Int(), new Bool(), new Char());
        }
    },
    Not_Equals("!=", 2) {
        @Override
        public List<Type> getTypeArgs() {
            return List.of(new Int(), new Bool(), new Char());
        }
    },
    Less_Than("<", 2) {
        @Override
        public void typeCheck(Expression lhs, Expression rhs) throws SemanticException {
            Operator.numericTypeCheck(lhs, rhs, "<");
        }
        @Override
        public List<Type> getTypeArgs() {
            return List.of(new Int(), new Char());
        }
    },
    Less_Than_Or_Equal("<=", 2) {
        @Override
        public void typeCheck(Expression lhs, Expression rhs) throws SemanticException {
            Operator.numericTypeCheck(lhs, rhs, "<=");
        }

        @Override
        public List<Type> getTypeArgs() {
            return List.of(new Int(), new Char());
        }
    },
    Greater_Than(">", 2) {
        @Override
        public void typeCheck(Expression lhs, Expression rhs) throws SemanticException {
            Operator.numericTypeCheck(lhs, rhs, ">");
        }

        @Override
        public List<Type> getTypeArgs() {
            return List.of(new Int(), new Char());
        }
    },
    Greater_Than_Or_Equal(">=", 2) {
        @Override
        public void typeCheck(Expression lhs, Expression rhs) throws SemanticException {
            Operator.numericTypeCheck(lhs, rhs, ">=");
        }

        @Override
        public List<Type> getTypeArgs() {
            return List.of(new Int(), new Char());
        }
    };

    private final String operator;
    private final int numberOfArgs;

    BoolOperator(String operator, int numberOfArgs) {
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
        return List.of(new Bool());
    }

    public void typeCheck(Expression lhs, Expression rhs) throws SemanticException {
        Operator.boolTypeCheck(lhs, rhs, operator);
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}
