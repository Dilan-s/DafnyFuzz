package AST.SymbolTable.Types.DCollectionTypes;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.MultisetLiteral;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.HashMap;
import java.util.Map;

public class Multiset implements DCollection {

    public static final int MAX_SIZE_OF_MULTISET = 10;
    public static final double PROB_USE_DSET = 0.3;
    public static final double PROB_USE_SEQ = PROB_USE_DSET + 0.3;
    private Type type;
    private Map<Expression, Integer> multiset;

    public Multiset(Type type) {
        this.type = type;
        multiset = new HashMap<>();
    }

    public Multiset() {
        this(null);
    }

    @Override
    public String getName() {
        return "multiset";
    }

    @Override
    public Type setInnerType(Type type) {
        return new Multiset(type);
    }

    @Override
    public Type getInnerType() {
        return type;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Type)) {
            return false;
        }
        Type other = (Type) obj;
        if (!(other instanceof Multiset)) {
            return false;
        }

        Multiset dsetOther = (Multiset) other;

        if (type == null || dsetOther.type == null) {
            return true;
        }

        return dsetOther.type.equals(type);
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        MultisetLiteral expression = new MultisetLiteral(symbolTable, this);
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();
        double probType = GeneratorConfig.getRandom().nextDouble();
        if (probType < PROB_USE_DSET) {
            DSet t = new DSet(this.type.concrete(symbolTable));
            Expression exp = expressionGenerator.generateExpression(t, symbolTable);
            expression.setCollection(exp);
            for (Expression e : t.getSet()) {
                multiset.put(e, multiset.getOrDefault(e, 0) + 1);
            }
        } else if (probType < PROB_USE_SEQ) {
            Seq t = new Seq(this.type.concrete(symbolTable));
            Expression exp = expressionGenerator.generateExpression(t, symbolTable);
            expression.setCollection(exp);
            for (Expression e : t.getSequence()) {
                multiset.put(e, multiset.getOrDefault(e, 0) + 1);
            }
        } else {
            int noOfElems = GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_MULTISET) + 1;
            for (int i = 0; i < noOfElems; i++) {
                Expression exp = expressionGenerator.generateExpression(type.concrete(symbolTable), symbolTable);
                expression.addValue(exp);
                multiset.put(exp, multiset.getOrDefault(exp, 0) + 1);
            }
        }
        return expression;
    }

    @Override
    public String getVariableType() {
        if (type == null) {
            return "multiset";
        }
        return String.format("multiset<%s>", type.getVariableType());
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        if (type == null) {
            RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
            Type t = typeGenerator.generateBaseTypes(1, symbolTable).get(0);
            return new Multiset(t);
        }
        return new Multiset(type.concrete(symbolTable));
    }


    @Override
    public boolean operatorExists() {
        return true;
    }

    @Override
    public boolean isPrintable() {
        return false;
    }
}
