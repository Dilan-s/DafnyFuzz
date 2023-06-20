package AST.SymbolTable.Types.UserDefinedTypes.DataType;

import AST.Generator.RandomExpressionGenerator;
import AST.Generator.VariableNameGenerator;
import AST.Expressions.DataType.DataTypeLiteral;
import AST.Expressions.DataType.DataTypeValue;
import AST.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.GenericType.GenericType;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.UserDefinedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DataTypeRule implements UserDefinedType {

    private Type parentType;
    private String ruleName;
    private int uses;
    private List<Type> fields;
    private List<GenericType> generics;
    private Map<GenericType, Type> genericMap;
    private List<String> fieldNames;

    private boolean init;


    public DataTypeRule(Type parentType, String ruleName, List<Type> fields, List<GenericType> generics, boolean init, Map<GenericType, Type> genericMap, List<String> fieldNames) {
        this.parentType = parentType;
        this.ruleName = ruleName;
        this.fields = fields;
        this.generics = generics;
        this.init = init;
        this.genericMap = genericMap;
        this.uses = 0;

        if (fieldNames == null) {
            this.fieldNames = new ArrayList<>();
            if (fields != null) {
                for (int i = 0; i < fields.size(); i++) {
                    this.fieldNames.add(VariableNameGenerator.generateDatatypeRuleFieldName(ruleName));
                }
            }
        } else {
            this.fieldNames = fieldNames;
        }
    }

    public DataTypeRule() {
        this(null, null, null, null, false, new HashMap<>(), null);
    }

    public DataTypeRule(Type parentType, String ruleName, List<GenericType> generics) {
        this(parentType, ruleName, new ArrayList<>(), generics);
    }

    public DataTypeRule(Type parentType, String ruleName, List<Type> fields, List<GenericType> generics) {
        this(parentType, ruleName, fields, generics, false, new HashMap<>(), null);
    }

    @Override
    public boolean isPrintable() {
        return fields.stream().allMatch(Type::isPrintable);
    }

    @Override
    public boolean validMethodType() {
        return fields.stream().allMatch(Type::validMethodType);
    }

    @Override
    public String getVariableType() {
        List<String> gener = generics.stream()
            .map(x -> genericMap.get(x))
            .map(Type::getVariableType)
            .collect(Collectors.toList());
        String gen = gener.isEmpty() ? "" : String.format("<%s>", String.join(", ", gener));
        String variableType = String.format("%s%s", parentType.getVariableType(), gen);
        return variableType;
    }

    @Override
    public String getName() {
        return ruleName;
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        uses++;
        RandomExpressionGenerator randomExpressionGenerator = new RandomExpressionGenerator();
        List<Expression> exps = new ArrayList<>();
        for (Type t : fields) {
            Expression e = randomExpressionGenerator.generateExpression(t, symbolTable);
            exps.add(e);
        }
        return new DataTypeLiteral(symbolTable, this, exps);
    }

    @Override
    public Expression generateExpressionFromValue(SymbolTable symbolTable, Object value) {
        DataTypeValue vs = (DataTypeValue) value;
        List<Expression> values = new ArrayList<>();
        for (int i = 0; i < fields.size(); i++) {
            Expression exp = fields.get(i).generateExpressionFromValue(symbolTable, vs.get(i));
            if (exp == null) {
                return null;
            }
            values.add(exp);
        }
        return new DataTypeLiteral(symbolTable, this, values);
    }

    public int getUses() {
        return uses;
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        if (!init) {
            List<Type> fs = new ArrayList<>();
            for (Type f : fields) {
                Type concrete;
                if (!genericMap.containsKey(f) && generics.contains(f)) {
                    concrete = f.concrete(symbolTable);
                    genericMap.put((GenericType) f, concrete);
                } else if (genericMap.containsKey(f)) {
                    concrete = genericMap.get(f);
                } else {
                    concrete = f.concrete(symbolTable);

                }
                fs.add(concrete);
            }
            for (GenericType t : generics) {
                if (!genericMap.containsKey(t)) {
                    genericMap.put(t, t.concrete(symbolTable));
                }
            }
            this.uses++;
            return new DataTypeRule(parentType, ruleName, fs, generics, true, genericMap, fieldNames);
        }
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleName, parentType);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Type)) {
            return false;
        }
        Type other = (Type) obj;
        if (!(other instanceof DataTypeRule)) {
            return false;
        }

        DataTypeRule dataTypeRuleOther = (DataTypeRule) other;

        if (parentType == null || ruleName == null || dataTypeRuleOther.parentType == null || dataTypeRuleOther.ruleName == null || fields == null || dataTypeRuleOther.fields == null) {
            return true;
        }

        return dataTypeRuleOther.parentType.equals(parentType) && ruleName.equals(dataTypeRuleOther.ruleName) && dataTypeRuleOther.fields.equals(fields);
    }

    @Override
    public Boolean equal(Object lhsV, Object rhsV) {
        DataTypeValue lhsVal = (DataTypeValue) lhsV;
        DataTypeValue rhsVal = (DataTypeValue) rhsV;
        if (!lhsVal.getType().equals(rhsVal.getType())) {
            return false;
        }

        for (int i = 0; i < fields.size(); i++) {
            Type type = fields.get(i);
            if (!type.equal(lhsVal.get(i), rhsVal.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String formatPrint(Object object) {
        DataTypeValue val = (DataTypeValue) object;
        String res = parentType.getName() + "." + ruleName;
        if (!fields.isEmpty()) {
            res = res + "(";
            boolean first = true;
            for (int i = 0; i < fields.size(); i++) {
                if (!first) {
                    res = res + ", ";
                }
                first = false;
                res = res + fields.get(i).formatPrint(val.get(i));
            }
            res = res + ")";
        }
        return res;
    }

    @Override
    public String formatEnsures(String variableName, Object object) {
        DataTypeValue val = (DataTypeValue) object;

        List<String> res = new ArrayList<>();
        if (fields.isEmpty()) {
            res.add(String.format("(%s == %s)", variableName, ruleName));
        }
        for (int i = 0; i < fieldNames.size(); i++) {
            String e = fields.get(i).formatEnsures(String.format("%s.%s", variableName, fieldNames.get(i)), val.get(i));
            if (e == null) {
                return null;
            }
            res.add(e);
        }

        String fieldCheck = String.join(" && ", res);
        String r = String.format("(%s.%s? && (%s))", variableName, ruleName, fieldCheck);
        return r;
    }

    public List<Type> getFieldTypes() {
        return fields;
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }

    public String declaration() {
        if (fields.isEmpty() && fieldNames.isEmpty()) {
            return ruleName;
        }

        List<String> fieldsMap = new ArrayList<>();
        for (int i = 0; i < fields.size(); i++) {
            String s = fieldNames.get(i);
            Type t = fields.get(i);

            String curr = String.format("%s: %s", s, t.getVariableType());
            fieldsMap.add(curr);
        }

        return String.format("%s(%s)", ruleName, String.join(", ", fieldsMap));
    }

    @Override
    public boolean validFunctionType() {
        return false;
    }
}
