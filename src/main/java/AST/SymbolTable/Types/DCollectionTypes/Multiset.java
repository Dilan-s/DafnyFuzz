package AST.SymbolTable.Types.DCollectionTypes;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.MultisetLiteral;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.AbstractType;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

public class Multiset extends AbstractType implements DCollection{

    public static final int MAX_SIZE_OF_MULTISET = 10;
    public static final double PROB_USE_DSET = 0.3;
    public static final double PROB_USE_SEQ = PROB_USE_DSET + 0.3;
    private Type type;
    private Map<Expression, Integer> multiset;

    public Multiset(Type type) {
        this.type = type;
        this.multiset = null;
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

        this.multiset = new HashMap<>();
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();
        double probType = GeneratorConfig.getRandom().nextDouble();

        if (probType < PROB_USE_DSET) {
            DSet t = new DSet(this.type.concrete(symbolTable));
            Expression exp = expressionGenerator.generateExpression(t, symbolTable);

            Expression expLiteral = t.generateLiteral(symbolTable, exp, t.getValue());
            Set<Expression> value = (Set<Expression>) expLiteral.getTypes().get(0).getValue();

            if (value == null) {
                this.multiset = null;
            } else {
                for (Expression e : value) {
                    multiset.put(e, 1);
                }
            }

            MultisetLiteral expression = new MultisetLiteral(symbolTable, this, Optional.of(exp));
            return expression;
        }
        if (probType < PROB_USE_SEQ) {
            Seq t = new Seq(this.type.concrete(symbolTable));
            Expression exp = expressionGenerator.generateExpression(t, symbolTable);

            Expression expLiteral = t.generateLiteral(symbolTable, exp, t.getValue());
            List<Expression> value = (List<Expression>) expLiteral.getTypes().get(0).getValue();

            if (value == null) {
                this.multiset = null;
            } else {
                for (Expression e : value) {
                    multiset.put(e, multiset.getOrDefault(e, 0) + 1);
                }
            }

            MultisetLiteral expression = new MultisetLiteral(symbolTable, this, Optional.of(exp));
            return expression;
        }

        int noOfElems = GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_MULTISET) + 1;
        List<Expression> values = new ArrayList<>();
        for (int i = 0; i < noOfElems; i++) {
            Type t = type.concrete(symbolTable);

            Expression exp = expressionGenerator.generateExpression(t, symbolTable);
            Expression expLiteral = t.generateLiteral(symbolTable, exp, t.getValue());

            multiset.put(expLiteral, multiset.getOrDefault(expLiteral, 0) + 1);
            values.add(exp);
        }
        MultisetLiteral expression = new MultisetLiteral(symbolTable, this, values);
        return expression;
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable, Object value) {
        Map<Expression, Integer> v = (Map<Expression, Integer>) value;
        List<Expression> res = new ArrayList<>();
        for (Entry<Expression, Integer> e : v.entrySet()) {
            for (int i = 0; i < e.getValue(); i++) {
                res.add(e.getKey());
            }
        }
        Type t = this.concrete(symbolTable);
        t.setValue(value);
        return new MultisetLiteral(symbolTable, t, res);
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
    public void setValue(Object value) {
        this.multiset = (Map<Expression, Integer>) value;
    }

    @Override
    public Object getValue() {
        return multiset;
    }

    @Override
    public int getSize() {
        return multiset.values().stream().reduce(0, Integer::sum);
    }

    @Override
    public boolean contains(Expression val) {
        return multiset.containsKey(val) && multiset.get(val) != 0;
    }

    @Override
    public boolean disjoint(DCollection rhs) {
        Multiset rhsMultiset = (Multiset) rhs;

        return rhsMultiset.multiset.entrySet().stream()
            .filter(x -> x.getValue() > 0)
            .map(Entry::getKey)
            .noneMatch(this::contains);
    }

    @Override
    public boolean isPrintable() {
        return false;
    }

    public Map<Expression, Integer> intersection(Multiset rhsMultiset) {
        Map<Expression, Integer> lhsm = multiset;
        Map<Expression, Integer> rhsm = rhsMultiset.multiset;

        Map<Expression, Integer> res = new HashMap<>();
        for (Expression e : lhsm.keySet()) {
            if (rhsm.containsKey(e)) {
                res.put(e, Math.min(lhsm.get(e), rhsm.get(e)));
            }
        }
        return res;
    }

    public Map<Expression, Integer> difference(Multiset rhsMultiset) {
        Map<Expression, Integer> lhsm = multiset;
        Map<Expression, Integer> rhsm = rhsMultiset.multiset;

        Map<Expression, Integer> res = new HashMap<>();
        for (Expression e : lhsm.keySet()) {
            int freq = lhsm.get(e) - rhsm.getOrDefault(e, 0);
            if (freq > 0) {
                res.put(e, freq);
            }
        }
        return res;
    }

    @Override
    public Object union(DCollection rhs) {
        Multiset rhsMultiset = (Multiset) rhs;
        Map<Expression, Integer> lhsm = multiset;
        Map<Expression, Integer> rhsm = rhsMultiset.multiset;

        Map<Expression, Integer> res = new HashMap<>();
        for (Expression e : lhsm.keySet()) {
            res.put(e, lhsm.get(e));
        }

        for (Expression e : rhsm.keySet()) {
            res.put(e, rhsm.get(e) + res.getOrDefault(e, 0));
        }

        return res;
    }

    @Override
    public boolean lessThanOrEqual(Type rhsT) {
        Multiset rhsM = (Multiset) rhsT;
        Map<Expression, Integer> rhsMultiset = rhsM.multiset;

        return rhsMultiset.keySet().containsAll(multiset.keySet())
            && multiset.entrySet().stream().allMatch(x -> x.getValue() <= rhsMultiset.getOrDefault(x.getKey(), 0));
    }

    @Override
    public boolean lessThan(Type rhsT) {
        Multiset rhsM = (Multiset) rhsT;
        Map<Expression, Integer> rhsMultiset = rhsM.multiset;

        return rhsMultiset.keySet().containsAll(multiset.keySet())
            && (multiset.entrySet().stream().anyMatch(x -> x.getValue() < rhsMultiset.getOrDefault(x.getKey(), 0))
            || !multiset.keySet().containsAll(rhsMultiset.keySet()));
    }

    @Override
    public boolean equal(Type rhsT) {
        Multiset rhsM = (Multiset) rhsT;
        Map<Expression, Integer> rhsMultiset = rhsM.multiset;

        return rhsMultiset.keySet().containsAll(multiset.keySet())
            && multiset.entrySet().stream().allMatch(x -> x.getValue().equals(rhsMultiset.getOrDefault(x.getKey(), 0)))
            && multiset.keySet().containsAll(rhsMultiset.keySet());
    }
}
