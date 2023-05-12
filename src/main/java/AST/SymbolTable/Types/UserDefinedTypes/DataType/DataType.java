package AST.SymbolTable.Types.UserDefinedTypes.DataType;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomTypeGenerator;
import AST.Generator.VariableNameGenerator;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.DCollectionTypes.Seq;
import AST.SymbolTable.Types.UserDefinedTypes.UserDefinedType;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DataType implements UserDefinedType {

    public static final int MAX_NO_OF_RULES = 5;
    public static final int MAX_NO_OF_TYPES_IN_RULE = 3;
    private String datatypeName;
    private List<DataTypeRule> rules;

    public DataType() {
        this(null);
    }

    public DataType(String datatypeName) {
        this.datatypeName = datatypeName;
        this.rules = null;
    }

    @Override
    public String getName() {
        return datatypeName;
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        int ind = GeneratorConfig.getRandom().nextInt(rules.size());
        DataTypeRule dataTypeRule = rules.get(ind);
        Expression expression = dataTypeRule.generateLiteral(symbolTable);
        return expression;
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
    public Type concrete(SymbolTable symbolTable) {
        if (rules == null) {
            RandomTypeGenerator randomTypeGenerator = new RandomTypeGenerator();
            this.rules = new ArrayList<>();

            String ruleName = VariableNameGenerator.generateDatatypeRuleName(datatypeName);
            DataTypeRule defaultRule = new DataTypeRule(this, ruleName);
            rules.add(defaultRule);


            int noOfRules = GeneratorConfig.getRandom().nextInt(MAX_NO_OF_RULES);
            for (int i = 0; i < noOfRules; i++) {

                int noOfTypes = GeneratorConfig.getRandom().nextInt(MAX_NO_OF_TYPES_IN_RULE);
                List<Type> types = randomTypeGenerator.generateTypes(noOfTypes, symbolTable);
                List<Type> concrete = types.stream()
                    .map(t -> t.concrete(symbolTable))
                    .collect(Collectors.toList());

                ruleName = VariableNameGenerator.generateDatatypeRuleName(datatypeName);
                DataTypeRule rule = new DataTypeRule(this, ruleName, concrete);
                rules.add(rule);
            }
        }
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(datatypeName);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Type)) {
            return false;
        }
        Type other = (Type) obj;
        if (!(other instanceof DataType)) {
            return false;
        }

        DataType dataTypeOther = (DataType) other;

        if (datatypeName == null || dataTypeOther.datatypeName == null) {
            return true;
        }

        return datatypeName.equals(dataTypeOther.datatypeName);
    }

    @Override
    public String formatPrint(Object object) {
        return null;
    }

    @Override
    public String formatEnsures(String variableName, Object object) {
        return null;
    }

    @Override
    public String toString() {
        if (rules == null) {
            return datatypeName;
        }
        return declaration();
    }

    public String declaration() {
        String rules = this.rules.stream()
            .filter(r -> r.getUses() > 0)
            .map(DataTypeRule::declaration)
            .collect(Collectors.joining(" | "));

        if (rules.isEmpty()) {
            return "";
        }
        return String.format("datatype %s = %s", datatypeName, rules);
    }

}
