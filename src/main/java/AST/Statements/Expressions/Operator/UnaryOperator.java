package AST.Statements.Expressions.Operator;

import AST.Errors.SemanticException;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.Method;
import AST.SymbolTable.PrimitiveTypes.Bool;
import AST.SymbolTable.PrimitiveTypes.Char;
import AST.SymbolTable.PrimitiveTypes.DSet;
import AST.SymbolTable.PrimitiveTypes.Int;
import AST.SymbolTable.PrimitiveTypes.Real;
import AST.SymbolTable.Type;
import java.util.List;

public enum UnaryOperator implements Operator {
    Equivalence("<==>", List.of(new Bool()), List.of(new Bool())),
    Implies("==>", List.of(new Bool()), List.of(new Bool())),
    ReverseImplies("<==", List.of(new Bool()), List.of(new Bool())),
    And("&&", List.of(new Bool()), List.of(new Bool())),
    Or("||", List.of(new Bool()), List.of(new Bool())),
    Equals("==", List.of(new Int(), new Bool(), new Char()), List.of(new Bool())),
    Not_Equals("!=", List.of(new Int(), new Bool(), new Char(), new DSet()), List.of(new Bool())),
    Less_Than("<", List.of(new Int(), new Char(), new Real(), new DSet()), List.of(new Bool())),
    Less_Than_Or_Equal("<=", List.of(new Int(), new Char(), new Real(), new DSet()), List.of(new Bool())),
    Greater_Than(">", List.of(new Int(), new Char(), new Real(), new DSet()), List.of(new Bool())),
    Greater_Than_Or_Equal(">=", List.of(new Int(), new Char(), new Real(), new DSet()), List.of(new Bool())),
    Plus("+", List.of(new Int()), List.of(new Int())),
    Minus("-", List.of(new Int()), List.of(new Int())),
    Times("*", List.of(new Int()), List.of(new Int())),
    Divide("/", List.of(new Int()), List.of(new Int())),
    Modulus("%", List.of(new Int()), List.of(new Int())),
    ;

    private final String operator;
    private final List<Type> typeArgs;
    private final List<Type> retTypes;

    UnaryOperator(String operator, List<Type> typeArgs, List<Type> retTypes) {
        this.operator = operator;
        this.typeArgs = typeArgs;
        this.retTypes = retTypes;
    }


    @Override
    public String formExpression(List<Expression> args) {
        String res = args.get(0).toString();
        for (int i = 1; i < args.size(); i++) {
            Expression rhs = args.get(i);
            res = String.format("(%s %s %s)", res, operator, rhs);

        }
        return res;
    }

    @Override
    public List<Type> getTypes() {
        return retTypes;
    }

    @Override
    public void semanticCheck(Method method, List<Expression> expressions)
        throws SemanticException {
    }

    @Override
    public List<Type> getTypeArgs() {
        return typeArgs;
    }

    public int getNumberOfArgs() {
        return 2;
    }
}
