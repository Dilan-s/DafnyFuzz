package AST.Statements.Expressions.Operator;

import AST.Errors.SemanticException;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.Method;
import AST.SymbolTable.PrimitiveTypes.Bool;
import AST.SymbolTable.PrimitiveTypes.Char;
import AST.SymbolTable.PrimitiveTypes.Int;
import AST.SymbolTable.PrimitiveTypes.Real;
import AST.SymbolTable.Type;
import java.util.List;

public enum BoolOperator implements Operator {
    Equivalence("<==>", 2, List.of(new Bool())),
    Implies("==>", 2, List.of(new Bool())),
    ReverseImplies("<==", 2, List.of(new Bool())),
    And("&&", 2, List.of(new Bool())),
    Or("||", 2, List.of(new Bool())),
    Equals("==", 2, List.of(new Int(), new Bool(), new Char())),
    Not_Equals("!=", 2, List.of(new Int(), new Bool(), new Char())),
    Less_Than("<", 2, List.of(new Int(), new Char(), new Real())) {
        @Override
        public void typeCheck(Expression lhs, Expression rhs) throws SemanticException {
            Operator.numericTypeCheck(lhs, rhs, "<");
        }
    },
    Less_Than_Or_Equal("<=", 2, List.of(new Int(), new Char(), new Real())) {
        @Override
        public void typeCheck(Expression lhs, Expression rhs) throws SemanticException {
            Operator.numericTypeCheck(lhs, rhs, "<=");
        }
    },
    Greater_Than(">", 2, List.of(new Int(), new Char(), new Real())) {
        @Override
        public void typeCheck(Expression lhs, Expression rhs) throws SemanticException {
            Operator.numericTypeCheck(lhs, rhs, ">");
        }
    },
    Greater_Than_Or_Equal(">=", 2, List.of(new Int(), new Char(), new Real())) {
        @Override
        public void typeCheck(Expression lhs, Expression rhs) throws SemanticException {
            Operator.numericTypeCheck(lhs, rhs, ">=");
        }
    };

    private final String operator;
    private final int numberOfArgs;
    private final List<Type> typeArgs;

    BoolOperator(String operator, int numberOfArgs, List<Type> typeArgs) {
        this.operator = operator;
        this.numberOfArgs = numberOfArgs;
        this.typeArgs = typeArgs;
    }


    @Override
    public String formExpression(Expression lhs, Expression rhs) {
        return String.format("(%s %s %s)", lhs, operator, rhs);
    }

    @Override
    public List<Type> getTypes() {
        return List.of(new Bool());
    }

    @Override
    public void semanticCheck(Method method, Expression lhs, Expression rhs)
        throws SemanticException {
        typeCheck(lhs, rhs);
    }

    @Override
    public List<Type> getTypeArgs() {
        return typeArgs;
    }

    public void typeCheck(Expression lhs, Expression rhs) throws SemanticException {
        Operator.boolTypeCheck(lhs, rhs, operator);
    }

    public int getNumberOfArgs() {
        return numberOfArgs;
    }
}
