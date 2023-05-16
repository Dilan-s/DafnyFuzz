package AST.SymbolTable.Types.UserDefinedTypes.DataType;

import AST.Generator.RandomExpressionGenerator;
import AST.Generator.VariableNameGenerator;
import AST.Statements.Expressions.DataType.DataTypeLiteral;
import AST.Statements.Expressions.DataType.DataTypeValue;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.UserDefinedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.xml.crypto.Data;
import javax.xml.transform.sax.SAXResult;

public class DataTypeRule implements UserDefinedType {

    private Type parentType;
    private String ruleName;
    private int uses;
    private List<Type> fields;
    private List<String> fieldNames;


    public DataTypeRule(Type parentType, String ruleName, List<Type> fields) {
        this.parentType = parentType;
        this.ruleName = ruleName;
        this.fields = fields;
        this.uses = 0;

        this.fieldNames = new ArrayList<>();
        if (fields != null) {
            for (int i = 0; i < fields.size(); i++) {
                fieldNames.add(VariableNameGenerator.generateDatatypeRuleFieldName(ruleName));
            }
        }
    }

    public DataTypeRule() {
        this(null, null, null);
    }

    public DataTypeRule(Type parentType, String ruleName) {
        this(parentType, ruleName, new ArrayList<>());
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
        return parentType.getVariableType();
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
        fields = fields.stream().map(f -> f.concrete(symbolTable)).collect(Collectors.toList());
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
}
