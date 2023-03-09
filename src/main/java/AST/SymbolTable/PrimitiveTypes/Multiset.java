package AST.SymbolTable.PrimitiveTypes;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.MultisetLiteral;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;

public class Multiset implements Type {

    public static final int MAX_SIZE_OF_MULTISET = 10;
    public static final double PROB_USE_DSET = 0.3;
    public static final double PROB_USE_SEQ = PROB_USE_DSET + 0.3;
    private Type type;

    public Multiset(Type type) {
        this.type = type;
    }

    public Multiset() {
        this(null);
    }

    @Override
    public String getName() {
        return String.format("multiset");
    }

    @Override
    public String getTypeIndicatorString() {
        return String.format(": multiset<%s>", type.getName());
    }

    @Override
    public Type setInnerType(Type type) {
        return new Multiset(type);
    }

    @Override
    public boolean isSameType(Type other) {
        if (!(other instanceof Multiset)) {
            return false;
        }

        Multiset dsetOther = (Multiset) other;

        if (type == null || dsetOther.type == null) {
            return true;
        }

        return dsetOther.type.isSameType(type);
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        MultisetLiteral expression = new MultisetLiteral(symbolTable, this);
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();
        double probType = GeneratorConfig.getRandom().nextDouble();
        if (probType < PROB_USE_DSET) {
            Expression exp = expressionGenerator.generateExpression(new DSet(type), symbolTable);
            expression.setCollection(exp);
        } else if (probType < PROB_USE_SEQ) {
            Expression exp = expressionGenerator.generateExpression(new Seq(type), symbolTable);
            expression.setCollection(exp);
        } else {
            int noOfElems = GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_MULTISET) + 1;
            for (int i = 0; i < noOfElems; i++) {
                expression.addValue(expressionGenerator.generateExpression(type, symbolTable));
            }
        }
        return expression;
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        if (type == null) {
            RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
            Type t = typeGenerator.generateNonCollectionType(1, symbolTable);
            return new Multiset(t);
        }
        return this;
    }


    @Override
    public boolean operatorExists() {
        return true;
    }

    @Override
    public boolean isPrintable() {
        return false;
    }

    @Override
    public boolean isCollection() {
        return true;
    }
}
