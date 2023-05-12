package AST.SymbolTable.Types.UserDefinedTypes.DataType;

import AST.Generator.RandomExpressionGenerator;
import AST.Generator.VariableNameGenerator;
import AST.Statements.Expressions.DataType.DataTypeLiteral;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.UserDefinedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
        return false;
    }

    @Override
    public boolean validMethodType() {
        return false;
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
    public String formatPrint(Object object) {
        return null;
    }

    @Override
    public String formatEnsures(String variableName, Object object) {
        return null;
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
