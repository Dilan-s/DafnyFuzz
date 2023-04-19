package AST.SymbolTable.Types.DMap;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.DMap.DMapLiteral;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.management.ObjectName;

public class DMap implements Type {

    private static final int MAX_NUMBER_OF_ELEMS = 10;
    private Type keyType;
    private Type valueType;

    public DMap(Type keyType, Type valueType) {
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public DMap() {
        this(null, null);
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
            List<Type> types = typeGenerator.generateTypes(2, symbolTable);
            Type k = types.get(0);
            Type v = types.get(1);

            return new DMap(k, v);
        } else if (keyType == null) {
            List<Type> types = typeGenerator.generateTypes(1, symbolTable);
            Type k = types.get(0);
            return new DMap(k, valueType.concrete(symbolTable));
        } else if (valueType == null) {
            List<Type> types = typeGenerator.generateTypes(1, symbolTable);
            Type v = types.get(0);
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
        return null;
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
}
