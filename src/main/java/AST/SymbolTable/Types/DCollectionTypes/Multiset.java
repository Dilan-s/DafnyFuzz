package AST.SymbolTable.Types.DCollectionTypes;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.MultisetLiteral;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

public class Multiset implements DCollection {

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

        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();
        double probType = GeneratorConfig.getRandom().nextDouble();

        if (probType < PROB_USE_DSET) {
            DSet t = new DSet(this.type.concrete(symbolTable));
            Expression exp = expressionGenerator.generateExpression(t, symbolTable);

            MultisetLiteral expression = new MultisetLiteral(symbolTable, this, Optional.of(exp));
            return expression;
        }
        if (probType < PROB_USE_SEQ) {
            Seq t = new Seq(this.type.concrete(symbolTable));
            Expression exp = expressionGenerator.generateExpression(t, symbolTable);

            MultisetLiteral expression = new MultisetLiteral(symbolTable, this, Optional.of(exp));
            return expression;
        }

        int noOfElems = GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_MULTISET) + 1;
        List<Expression> values = new ArrayList<>();
        for (int i = 0; i < noOfElems; i++) {
            Type t = type.concrete(symbolTable);

            Expression exp = expressionGenerator.generateExpression(t, symbolTable);
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
    public boolean isPrintable() {
        return false;
    }

    @Override
    public String formatPrint(Object object) {
        return "";
    }

    @Override
    public boolean operatorExists() {
        return true;
    }

    @Override
    public Boolean contains(Object lhsV, Object rhsV) {
        Map<Object, Integer> rhsVM = (Map<Object, Integer>) rhsV;
        return rhsVM.containsKey(lhsV);
    }

    @Override
    public Boolean disjoint(Object lhsV, Object rhsV) {
        Map<Object, Integer> lhsVM = (Map<Object, Integer>) lhsV;
        Map<Object, Integer> rhsVM = (Map<Object, Integer>) rhsV;

        for (Object k : lhsVM.keySet()) {
            if (rhsVM.containsKey(k)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object intersection(Object lhsV, Object rhsV) {
        Map<Object, Integer> lhsVM = (Map<Object, Integer>) lhsV;
        Map<Object, Integer> rhsVM = (Map<Object, Integer>) rhsV;

        Map<Object, Integer> res = new HashMap<>();
        for (Object k : lhsVM.keySet()) {
            if (rhsVM.containsKey(k)) {
                res.put(k, Math.min(lhsVM.get(k), rhsVM.get(k)));
            }
        }
        return res;
    }

    @Override
    public Object difference(Object lhsV, Object rhsV) {
        Map<Object, Integer> lhsVM = (Map<Object, Integer>) lhsV;
        Map<Object, Integer> rhsVM = (Map<Object, Integer>) rhsV;

        Map<Object, Integer> res = new HashMap<>();
        for (Object k : lhsVM.keySet()) {
            int freq = lhsVM.get(k) - rhsVM.getOrDefault(k, 0);
            if (freq > 0) {
                res.put(k, freq);
            }
        }
        return res;
    }

    @Override
    public Object union(Object lhsV, Object rhsV) {
        Map<Object, Integer> lhsVM = (Map<Object, Integer>) lhsV;
        Map<Object, Integer> rhsVM = (Map<Object, Integer>) rhsV;

        Map<Object, Integer> res = new HashMap<>();
        for (Object k : lhsVM.keySet()) {
            res.put(k, lhsVM.get(k));
        }
        for (Object k : rhsVM.keySet()) {
            res.put(k, rhsVM.get(k) + res.getOrDefault(k, 0));
        }
        return res;
    }

    @Override
    public Boolean lessThan(Object lhsV, Object rhsV) {
        Map<Object, Integer> lhsVM = (Map<Object, Integer>) lhsV;
        Map<Object, Integer> rhsVM = (Map<Object, Integer>) rhsV;

        boolean atLeastOneSmaller = false;
        for (Object k : lhsVM.keySet()) {
            if (!rhsVM.containsKey(k)) {
                return false;
            }
            if (lhsVM.get(k) < rhsVM.get(k)) {
                atLeastOneSmaller = true;
            }
        }
        return atLeastOneSmaller || !lhsVM.keySet().containsAll(rhsVM.keySet());
    }

    @Override
    public Boolean equal(Object lhsV, Object rhsV) {
        Map<Object, Integer> lhsVM = (Map<Object, Integer>) lhsV;
        Map<Object, Integer> rhsVM = (Map<Object, Integer>) rhsV;

        for (Object k : lhsVM.keySet()) {
            if (!rhsVM.containsKey(k) || !Objects.equals(lhsVM.get(k), rhsVM.get(k))) {
                return false;
            }
        }
        return lhsVM.keySet().containsAll(rhsVM.keySet());
    }
}
