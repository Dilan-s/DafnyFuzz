package AST.SymbolTable.Types.DMap;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Expressions.DMap.DMapLiteral;
import AST.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

public class DMap implements Type {

    private static final int MAX_NUMBER_OF_ELEMS = 5;
    private Type keyType;
    private Type valueType;

    public DMap(Type keyType, Type valueType) {
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public DMap() {
        this(null, null);
    }

    @Override
    public String formatEnsures(String variableName, Object object) {
        if (keyType == null || valueType == null) {
            return null;
        }
        Map<Object, Object> m = (Map<Object, Object>) object;

        List<String> res = new ArrayList<>();
        List<String> mapContents = new ArrayList<>();

        int i = 0;
        for (Entry<Object, Object> x : m.entrySet()) {
            String s = String.format("%s := %s", keyType.formatPrint(x.getKey()), valueType.formatPrint(x.getValue()));
            mapContents.add(s);
        }
        res.add(String.format("(%s == map[%s])", variableName,
            String.join(", ", mapContents)));
        return String.join(" && ", res);

    }

    public Type setKeyAndValueType(Type keyType, Type valueType) {
        return new DMap(keyType, valueType);
    }

    public Type setKeyType(Type keyType) {
        return new DMap(keyType, null);
    }

    public Type setValueType(Type valueType) {
        return new DMap(null, valueType);
    }

    @Override
    public boolean validMethodType() {
        return keyType.validMethodType() && keyType.isPrintable() && valueType.validMethodType() && valueType.isPrintable() && !keyType.equals(new DMap()) && !valueType.equals(new DMap());
    }

    @Override
    public String getName() {
        return "map";
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();

        int noOfElems = GeneratorConfig.getRandom().nextInt(MAX_NUMBER_OF_ELEMS) + 1;
        List<DMapEntry> entries = new ArrayList<>();

        for (int i = 0; i < noOfElems; i++) {
            Type k = keyType.concrete(symbolTable);
            Type v = valueType.concrete(symbolTable);

            Expression kE = expressionGenerator.generateExpression(k, symbolTable);
            Expression vE = expressionGenerator.generateExpression(v, symbolTable);
            entries.add(new DMapEntry(kE, vE));
        }

        return new DMapLiteral(symbolTable, this, entries);
    }

    @Override
    public Expression generateExpressionFromValue(SymbolTable symbolTable, Object value) {
        Map<Object, Object> v = (Map<Object, Object>) value;
        List<DMapEntry> entries = new ArrayList<>();
        for (Entry<Object, Object> entry : v.entrySet()) {
            Expression kExp = keyType.generateExpressionFromValue(symbolTable, entry.getKey());
            if (kExp == null) {
                return null;
            }
            Expression vExp = valueType.generateExpressionFromValue(symbolTable, entry.getValue());
            if (vExp == null) {
                return null;
            }
            entries.add(new DMapEntry(kExp, vExp));
        }
        return new DMapLiteral(symbolTable, this, entries);
    }

    @Override
    public String getVariableType() {
        if (keyType == null || valueType == null) {
            return "map";
        }
        return String.format("map<%s, %s>", keyType.getVariableType(), valueType.getVariableType());
    }

    @Override
    public boolean operatorExists() {
        return true;
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
        if (keyType == null && valueType == null) {
            List<Type> types = typeGenerator.generateMapTypes(2, symbolTable);
            Type k = types.get(0).concrete(symbolTable);
            Type v = types.get(1).concrete(symbolTable);

            return new DMap(k, v);
        } else if (keyType == null) {
            List<Type> types = typeGenerator.generateMapTypes(1, symbolTable);
            Type k = types.get(0).concrete(symbolTable);
            return new DMap(k, valueType.concrete(symbolTable));
        } else if (valueType == null) {
            List<Type> types = typeGenerator.generateMapTypes(1, symbolTable);
            Type v = types.get(0).concrete(symbolTable);
            return new DMap(keyType.concrete(symbolTable), v);
        } else {
            return new DMap(keyType.concrete(symbolTable), valueType.concrete(symbolTable));

        }
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public Boolean lessThan(Object lhsV, Object rhsV) {
        return null;
    }

    @Override
    public Boolean equal(Object lhsV, Object rhsV) {
        Map<Object, Object> lhsM = (Map<Object, Object>) lhsV;
        Map<Object, Object> rhsM = (Map<Object, Object>) rhsV;

        if (!Objects.equals(lhsM.keySet(), rhsM.keySet())) {
            return false;
        }

        for (Object k : lhsM.keySet()) {
            if (!valueType.equal(lhsM.get(k), rhsM.get(k))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public BigInteger cardinality(Object value) {
        Map<Object, Object> valM = (Map<Object, Object>) value;
        return BigInteger.valueOf(valM.keySet().size());
    }

    @Override
    public boolean isPrintable() {
        return false;
    }

    @Override
    public String formatPrint(Object object) {
        Map<Object, Object> m = (Map<Object, Object>) object;
        List<String> mapContents = new ArrayList<>();
        for (Entry<Object, Object> x : m.entrySet()) {
            String s = String.format("%s := %s", keyType.formatPrint(x.getKey()), valueType.formatPrint(x.getValue()));
            mapContents.add(s);
        }
        return String.format("map[%s]", String.join(", ", mapContents));
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
        if (!(other instanceof DMap)) {
            return false;
        }

        DMap dsetOther = (DMap) other;

        if (keyType == null || dsetOther.keyType == null || valueType == null || dsetOther.valueType == null) {
            return true;
        }

        return dsetOther.keyType.equals(keyType) && dsetOther.valueType.equals(valueType);
    }

    public Boolean contains(Object lhsV, Object rhsV) {
        Map<Object, Object> rhsVM = (Map<Object, Object>) rhsV;
        return rhsVM.containsKey(lhsV);
    }

    public Type getKeyType() {
        return keyType;
    }

    public Type getValueType() {
        return valueType;
    }

    public Object add(Object lhsV, Object rhsV) {
        Map<Object, Object> lhsVM = (Map<Object, Object>) lhsV;
        Map<Object, Object> rhsVM = (Map<Object, Object>) rhsV;

        Map<Object, Object> m = new HashMap<>();

        m.putAll(lhsVM);
        m.putAll(rhsVM);

        return m;
    }

    public Object remove(Object lhsV, Object rhsV) {
        Map<Object, Object> lhsVM = (Map<Object, Object>) lhsV;
        Set<Object> rhsVS = (Set<Object>) rhsV;

        Map<Object, Object> m = new HashMap<>();

        for (Map.Entry<Object, Object> entry : lhsVM.entrySet()) {
            if (!rhsVS.contains(entry.getKey())) {
                m.put(entry.getKey(), entry.getValue());
            }
        }

        return m;
    }

    @Override
    public Object of(Object value) {
        Map<Object, Object> r = new HashMap<>();

        Map<Object, Object> m = (Map<Object, Object>) value;
        for (Map.Entry<Object, Object> entry : m.entrySet()) {
            Object keyV = keyType != null ? keyType.of(entry.getKey()) : entry.getKey();
            Object valV = valueType != null ? valueType.of(entry.getValue()) : entry.getValue();

            r.put(keyV, valV);
        }
        return r;
    }

    @Override
    public boolean isOrdered() {
        return false;
    }
}
