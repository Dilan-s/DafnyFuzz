package AST.Statements.Expressions.Operator;

import AST.Errors.SemanticException;
import AST.Generator.RandomStatementGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.Method;
import AST.SymbolTable.PrimitiveTypes.Bool;
import AST.SymbolTable.PrimitiveTypes.DSet;
import AST.SymbolTable.PrimitiveTypes.Int;
import AST.SymbolTable.PrimitiveTypes.Seq;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum BinaryOperator implements Operator {
    Equivalence("<==>", List.of(Args.BOOL_BOOL), new Bool()),
    Implies("==>", List.of(Args.BOOL_BOOL), new Bool()),
    ReverseImplies("<==", List.of(Args.BOOL_BOOL), new Bool()),
    And("&&", List.of(Args.BOOL_BOOL), new Bool()),
    Or("||", List.of(Args.BOOL_BOOL), new Bool()),
    Equals("==", List.of(Args.INT_INT, Args.BOOL_BOOL, Args.CHAR_CHAR, Args.DSET_DSET, Args.SEQ_SEQ), new Bool()),
    Not_Equals("!=", List.of(Args.INT_INT, Args.BOOL_BOOL, Args.CHAR_CHAR, Args.DSET_DSET, Args.SEQ_SEQ), new Bool()),
    Less_Than("<", List.of(Args.INT_INT, Args.REAL_REAL, Args.CHAR_CHAR, Args.DSET_DSET, Args.SEQ_SEQ), new Bool()),
    Less_Than_Or_Equal("<=", List.of(Args.INT_INT, Args.REAL_REAL, Args.CHAR_CHAR, Args.DSET_DSET, Args.SEQ_SEQ), new Bool()),
    Greater_Than(">", List.of(Args.INT_INT, Args.REAL_REAL, Args.CHAR_CHAR, Args.DSET_DSET), new Bool()),
    Greater_Than_Or_Equal(">=", List.of(Args.INT_INT, Args.REAL_REAL, Args.CHAR_CHAR, Args.DSET_DSET), new Bool()),
    Plus("+", List.of(Args.INT_INT), new Int()),
    Minus("-", List.of(Args.INT_INT), new Int()),
    Times("*", List.of(Args.INT_INT), new Int()),
    Divide("/", List.of(Args.INT_INT), new Int()),
    Modulus("%", List.of(Args.INT_INT), new Int()),
    Disjoint("!!", List.of(Args.DSET_DSET), new Bool()),
    Union("+", List.of(Args.DSET_DSET), List.of(new DSet(), new Seq())) {
        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable,
            Type expected) {
            List<Type> ret = new ArrayList<>();
            for (Type ignored : types) {
                ret.add(expected);
            }
            return ret;
        }
    },
    Difference("-", List.of(Args.DSET_DSET), new DSet()) {
        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable,
            Type expected) {
            List<Type> ret = new ArrayList<>();
            for (Type ignored : types) {
                ret.add(expected);
            }
            return ret;
        }
    },
    Intersection("*", List.of(Args.DSET_DSET), new DSet()) {
        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable,
            Type expected) {
            List<Type> ret = new ArrayList<>();
            for (Type ignored : types) {
                ret.add(expected);
            }
            return ret;
        }
    },
    Membership("in", List.of(Args.SEQ, Args.DSET), new Bool()) {
        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable,
            Type expected) {
            RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
            Type t = typeGenerator.generateNonCollectionType(1, symbolTable);
            List<Type> ret = new ArrayList<>();
            ret.add(t);
            ret.add(types.get(0).setInnerType(t));
            return ret;
        }
    },
    NotMembership("!in", List.of(Args.SEQ, Args.DSET), new Bool()) {
        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable,
            Type expected) {
            RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
            Type t = typeGenerator.generateNonCollectionType(1, symbolTable);
            List<Type> ret = new ArrayList<>();
            ret.add(t);
            ret.add(types.get(0).setInnerType(t));
            return ret;
        }
    },

    ;

    private final String operator;
    private final List<List<Type>> typeArgs;
    private final List<Type> retTypes;

    BinaryOperator(String operator, List<List<Type>> typeArgs, Type retType) {
        this.operator = operator;
        this.typeArgs = typeArgs;
        this.retTypes = List.of(retType);
    }

    BinaryOperator(String operator, List<List<Type>> typeArgs, List<Type> retType) {
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
    public List<Type> getType() {
        return retTypes;
    }

    @Override
    public void semanticCheck(Method method, List<Expression> expressions)
        throws SemanticException {
    }

    @Override
    public List<List<Type>> getTypeArgs() {
        return typeArgs;
    }

    public int getNumberOfArgs() {
        return 2;
    }

}
