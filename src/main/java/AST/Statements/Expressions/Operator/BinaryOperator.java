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

public enum BinaryOperator implements Operator {
    Equivalence("<==>", List.of(new Bool()), new Bool()),
    Implies("==>", List.of(new Bool()), new Bool()),
    ReverseImplies("<==", List.of(new Bool()), new Bool()),
    And("&&", List.of(new Bool()), new Bool()),
    Or("||", List.of(new Bool()), new Bool()),
    Equals("==", List.of(new Int(), new Bool(), new Char()), new Bool()),
    Not_Equals("!=", List.of(new Int(), new Bool(), new Char(), new DSet()), new Bool()),
    Less_Than("<", List.of(new Int(), new Char(), new Real(), new DSet()), new Bool()),
    Less_Than_Or_Equal("<=", List.of(new Int(), new Char(), new Real(), new DSet()), new Bool()),
    Greater_Than(">", List.of(new Int(), new Char(), new Real(), new DSet()), new Bool()),
    Greater_Than_Or_Equal(">=", List.of(new Int(), new Char(), new Real(), new DSet()), new Bool()),
    Plus("+", List.of(new Int()), new Int()),
    Minus("-", List.of(new Int()), new Int()),
    Times("*", List.of(new Int()), new Int()),
    Divide("/", List.of(new Int()), new Int()),
    Modulus("%", List.of(new Int()), new Int()),
    Disjoint("!!", List.of(new DSet()), new Bool()),
    Union("+", List.of(new DSet()), new DSet()),
    Difference("-", List.of(new DSet()), new DSet()),
    Intersection("*", List.of(new DSet()), new DSet()),
    ;

    private final String operator;
    private final List<Type> typeArgs;
    private final Type retTypes;

    BinaryOperator(String operator, List<Type> typeArgs, Type retType) {
        this.operator = operator;
        this.typeArgs = typeArgs;
        this.retTypes = retType;
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
    public Type getType() {
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
