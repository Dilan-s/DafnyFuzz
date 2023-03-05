package AST.Statements.Expressions.Operator;

import AST.Errors.SemanticException;
import AST.Generator.RandomTokenGenerator;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.Method;
import AST.SymbolTable.PrimitiveTypes.Bool;
import AST.SymbolTable.PrimitiveTypes.Char;
import AST.SymbolTable.PrimitiveTypes.DSet;
import AST.SymbolTable.PrimitiveTypes.Int;
import AST.SymbolTable.PrimitiveTypes.Real;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum UnaryOperator implements Operator {
    Cardinality("|%s|", List.of(Args.SEQ, Args.DSET), new Int()) {
        @Override
        public String formExpression(List<Expression> args) {
            String res = args.get(0).toString();
            return String.format("|%s|", res);
        }

        @Override
        public List<Type> concreteType(Random random, List<Type> types, SymbolTable symbolTable,
            Type expected) {
            RandomTokenGenerator tokenGenerator = new RandomTokenGenerator(random);
            Type t = tokenGenerator.generateNonCollectionType(1, symbolTable);
            List<Type> ret = new ArrayList<>();
            ret.add(types.get(0).setInnerType(t));
            return ret;
        }
    },
    ;

    private final String operator;
    private final List<List<Type>> typeArgs;
    private final List<Type> retTypes;

    UnaryOperator(String operator, List<List<Type>> typeArgs, Type retTypes) {
        this.operator = operator;
        this.typeArgs = typeArgs;
        this.retTypes = List.of(retTypes);
    }

    UnaryOperator(String operator, List<List<Type>> typeArgs, List<Type> retTypes) {
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
        return 1;
    }
}
