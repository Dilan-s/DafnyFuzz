package AST.SymbolTable.Types.UserDefinedTypes.DataType;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomTypeGenerator;
import AST.Generator.VariableNameGenerator;
import AST.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.GenericType.GenericType;
import AST.SymbolTable.Types.UserDefinedTypes.UserDefinedType;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DataType implements UserDefinedType {

    public static final int MAX_NO_OF_RULES = 5;
    public static final int MAX_NO_OF_TYPES_IN_RULE = 5;
    private static final int MAX_NO_OF_GENERICS = 3;
    private static final double USE_GENERIC = 0.9;
    private String datatypeName;
    private List<DataTypeRule> rules;
    private DataTypeRule defRule;
    private List<GenericType> generics;

    public DataType() {
        this(null);
    }

    public DataType(String datatypeName) {
        this.datatypeName = datatypeName;
        this.rules = null;
        this.defRule = null;
        this.generics = null;
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
    public Expression generateExpressionFromValue(SymbolTable symbolTable, Object value) {
        return null;
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
            if (datatypeName == null) {
                datatypeName = VariableNameGenerator.generateDatatypeName();
            }

            RandomTypeGenerator randomTypeGenerator = new RandomTypeGenerator();
            this.rules = new ArrayList<>();

            String ruleName = VariableNameGenerator.generateDatatypeRuleName(datatypeName);

            int noOfGenerics = GeneratorConfig.getRandom().nextInt(MAX_NO_OF_GENERICS);
            this.generics = new ArrayList<>();
            for (int i = 0; i < noOfGenerics; i++) {
                generics.add(new GenericType(VariableNameGenerator.generateGenericName()));
            }
            defRule = new DataTypeRule(this, ruleName, generics);

            int noOfRules = GeneratorConfig.getRandom().nextInt(MAX_NO_OF_RULES);
            for (int i = 0; i < noOfRules; i++) {

                int noOfTypes = GeneratorConfig.getRandom().nextInt(MAX_NO_OF_TYPES_IN_RULE);
                List<Type> types = new ArrayList<>();
                while (types.size() < noOfTypes) {
                    double probGeneric = GeneratorConfig.getRandom().nextDouble();
                    if (!generics.isEmpty() && probGeneric < USE_GENERIC) {
                        int t = GeneratorConfig.getRandom().nextInt(generics.size());
                        types.add(generics.get(t));
                    } else {
                        List<Type> ts = randomTypeGenerator.generateTypes(1, symbolTable)
                            .stream().map(t -> t.equals(new DataType()) ? t : t.concrete(symbolTable))
                            .collect(Collectors.toList());
                        types.addAll(ts);
                    }

                }

                ruleName = VariableNameGenerator.generateDatatypeRuleName(datatypeName);
                DataTypeRule rule = new DataTypeRule(this, ruleName, types, generics);
                rules.add(rule);
            }

            if (!RandomTypeGenerator.DEFINED_DATA_TYPES.contains(this)) {
                RandomTypeGenerator.DEFINED_DATA_TYPES.add(this);
            }
        }
        List<DataTypeRule> rs = new ArrayList<>();
        rs.add(defRule);
        rs.addAll(rules);
        int ind = GeneratorConfig.getRandom().nextInt(rs.size());
        Type dataTypeRule = rs.get(ind).concrete(symbolTable);
        return dataTypeRule;
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

        DataType dataTypeOther = other.asDataType();

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
        List<DataTypeRule> rs = this.rules.stream().filter(r -> r.getUses() > 0)
            .collect(Collectors.toList());

        rs.add(0, defRule);

        String rules = rs.stream()
            .map(DataTypeRule::declaration)
            .collect(Collectors.joining(" | "));

        String genericsRep = generics.isEmpty() ? "" : String.format("<%s>", generics.stream()
            .map(GenericType::getRepresentation)
            .collect(Collectors.joining(", ")));

        return String.format("datatype %s%s = %s", datatypeName, genericsRep, rules);
    }

    @Override
    public boolean validFunctionType() {
        return false;
    }
}
