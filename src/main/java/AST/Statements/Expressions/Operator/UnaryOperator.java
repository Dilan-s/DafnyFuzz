package AST.Statements.Expressions.Operator;

import AST.Errors.SemanticException;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.DCollection;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.List;

public enum UnaryOperator implements Operator {
    Cardinality("|%s|", List.of(Args.SEQ, Args.DSET, Args.MULTISET), new Int()) {
        @Override
        public String formExpression(List<Expression> args) {
            String res = args.get(0).toString();
            return String.format("|%s|", res);
        }

        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable,
            Type expected) {
            RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
            Type t = typeGenerator.generateNonCollectionType(1, symbolTable);
            List<Type> ret = new ArrayList<>();
            DCollection collection = (DCollection) types.get(0);
            ret.add(collection.setInnerType(t));
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
