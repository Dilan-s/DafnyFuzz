package AST.SymbolTable.Types.DCollectionTypes;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.DSeq.SeqLiteral;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.MultisetLiteral;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Multiset implements DCollection {

    public static final int MAX_SIZE_OF_MULTISET = 5;
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
    public boolean validMethodType() {
        return false;
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

        if (!type.equals(new DArray()) && probType < PROB_USE_DSET) {
            DSet t = new DSet(this.type.concrete(symbolTable));
            Expression exp = expressionGenerator.generateExpression(t, symbolTable);

            MultisetLiteral expression = new MultisetLiteral(symbolTable, this, exp);
            return expression;
        }
        if (type.equals(new DArray()) && probType < PROB_USE_SEQ) {
            Seq t = new Seq(this.type.concrete(symbolTable));
            Expression exp = expressionGenerator.generateExpression(t, symbolTable);

            MultisetLiteral expression = new MultisetLiteral(symbolTable, this, exp);
            return expression;
        }

        int noOfElems = GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_MULTISET);
        if (type.equals(new DArray())) {
            while (noOfElems == 1) {
                noOfElems = GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_MULTISET);
            }
        }
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
    public Expression generateExpressionFromValue(SymbolTable symbolTable, Object value) {
        Map<Object, BigInteger> vs = (Map<Object, BigInteger>) value;
        List<Expression> values = new ArrayList<>();
        for (Map.Entry<Object, BigInteger> v : vs.entrySet()) {
            for (BigInteger i = BigInteger.ZERO; i.compareTo(v.getValue()) < 0; i = i.add(BigInteger.ONE)) {
                Expression exp = type.generateExpressionFromValue(symbolTable, v.getKey());
                if (exp == null) {
                    return null;
                }
                values.add(exp);
            }
        }
        return new MultisetLiteral(symbolTable, this, values);
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
            Type t = typeGenerator.generateTypes(1, symbolTable).get(0);
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
        String res;
        Map<Object, BigInteger> value = (Map<Object, BigInteger>) object;
        res = "multiset([";

        boolean first = true;
        for (Map.Entry<Object, BigInteger> entry : value.entrySet()) {

            for (BigInteger i = BigInteger.ZERO; i.compareTo(entry.getValue()) < 0; i = i.add(BigInteger.ONE)) {
                if (!first) {
                    res = res + ", ";
                }
                first = false;
                res = res + type.formatPrint(entry.getKey());
            }
        }

        res = res + "])";
        return res;
    }

    @Override
    public boolean operatorExists() {
        return true;
    }

    @Override
    public Boolean contains(Object lhsV, Object rhsV) {
        Map<Object, BigInteger> rhsVM = (Map<Object, BigInteger>) rhsV;
        return rhsVM.containsKey(lhsV);
    }

    @Override
    public Boolean disjoint(Object lhsV, Object rhsV) {
        Map<Object, BigInteger> lhsVM = (Map<Object, BigInteger>) lhsV;
        Map<Object, BigInteger> rhsVM = (Map<Object, BigInteger>) rhsV;

        for (Object k : lhsVM.keySet()) {
            if (rhsVM.containsKey(k)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object intersection(Object lhsV, Object rhsV) {
        Map<Object, BigInteger> lhsVM = (Map<Object, BigInteger>) lhsV;
        Map<Object, BigInteger> rhsVM = (Map<Object, BigInteger>) rhsV;

        Map<Object, BigInteger> res = new HashMap<>();
        for (Object k : lhsVM.keySet()) {
            if (rhsVM.containsKey(k)) {
                res.put(k, lhsVM.get(k).min(rhsVM.get(k)));
            }
        }
        return res;
    }

    @Override
    public Object difference(Object lhsV, Object rhsV) {
        Map<Object, BigInteger> lhsVM = (Map<Object, BigInteger>) lhsV;
        Map<Object, BigInteger> rhsVM = (Map<Object, BigInteger>) rhsV;

        Map<Object, BigInteger> res = new HashMap<>();
        for (Object k : lhsVM.keySet()) {
            BigInteger freq = lhsVM.get(k).subtract(rhsVM.getOrDefault(k, BigInteger.ZERO));
            if (freq.compareTo(BigInteger.ZERO) > 0) {
                res.put(k, freq);
            }
        }
        return res;
    }

    @Override
    public Object union(Object lhsV, Object rhsV) {
        Map<Object, BigInteger> lhsVM = (Map<Object, BigInteger>) lhsV;
        Map<Object, BigInteger> rhsVM = (Map<Object, BigInteger>) rhsV;

        Map<Object, BigInteger> res = new HashMap<>();
        for (Object k : lhsVM.keySet()) {
            res.put(k, lhsVM.get(k));
        }
        for (Object k : rhsVM.keySet()) {
            res.put(k, rhsVM.get(k).add(res.getOrDefault(k, BigInteger.ZERO)));
        }
        return res;
    }

    @Override
    public Boolean lessThan(Object lhsV, Object rhsV) {
        Map<Object, BigInteger> lhsVM = (Map<Object, BigInteger>) lhsV;
        Map<Object, BigInteger> rhsVM = (Map<Object, BigInteger>) rhsV;

        boolean atLeastOneSmaller = false;
        for (Object k : lhsVM.keySet()) {
            if (!rhsVM.containsKey(k)) {
                return false;
            }
            if (lhsVM.get(k).compareTo(rhsVM.get(k)) > 0) {
                return false;
            }
            if (lhsVM.get(k).compareTo(rhsVM.get(k)) < 0) {
                atLeastOneSmaller = true;
            }
        }
        return atLeastOneSmaller || rhsVM.keySet().size() > lhsVM.keySet().size();
    }

    @Override
    public Boolean lessThanOrEqual(Object lhsV, Object rhsV) {
        Map<Object, BigInteger> lhsVM = (Map<Object, BigInteger>) lhsV;
        Map<Object, BigInteger> rhsVM = (Map<Object, BigInteger>) rhsV;

        for (Object k : lhsVM.keySet()) {
            if (!rhsVM.containsKey(k)) {
                return false;
            }
            if (lhsVM.get(k).compareTo(rhsVM.get(k)) > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean equal(Object lhsV, Object rhsV) {
        Map<Object, BigInteger> lhsVM = (Map<Object, BigInteger>) lhsV;
        Map<Object, BigInteger> rhsVM = (Map<Object, BigInteger>) rhsV;

        for (Object k : lhsVM.keySet()) {
            if (!rhsVM.containsKey(k) || !Objects.equals(lhsVM.get(k), rhsVM.get(k))) {
                return false;
            }
        }
        return lhsVM.keySet().containsAll(rhsVM.keySet());
    }

    @Override
    public String formatEnsures(String variableName, Object object) {
        if (type == null) {
            return null;
        }

        String res;
        Map<Object, BigInteger> value = (Map<Object, BigInteger>) object;
        res = "multiset([";

        boolean first = true;
        for (Map.Entry<Object, BigInteger> entry : value.entrySet()) {

            for (BigInteger i = BigInteger.ZERO; i.compareTo(entry.getValue()) < 0; i = i.add(BigInteger.ONE)) {
                if (!first) {
                    res = res + ", ";
                }
                first = false;
                res = res + type.formatPrint(entry.getKey());
            }
        }

        res = res + "])";
        return variableName + " == " + res;
    }

    @Override
    public Object of(Object value) {
        Map<Object, BigInteger> r = new HashMap<>();

        Map<Object, BigInteger> multi = (Map<Object, BigInteger>) value;
        for (Map.Entry<Object, BigInteger> entry : multi.entrySet()) {
            r.put(type != null ? type.of(entry.getKey()) : entry.getKey(), entry.getValue());
        }

        return r;
    }
}
