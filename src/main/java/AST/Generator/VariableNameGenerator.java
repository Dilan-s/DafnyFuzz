package AST.Generator;

import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.HashMap;
import java.util.Map;

public class VariableNameGenerator {

    private static final Map<String, Integer> returnValues = new HashMap<>();
    private static final Map<String, Integer> parameterValues = new HashMap<>();
    private static final Map<String, Integer> variableValues = new HashMap<>();
    private static final Map<String, Integer> datatypeValues = new HashMap<>();
    private static final Map<String, Integer> datatypeFieldValues = new HashMap<>();
    private static final Map<String, Integer> dclassFieldValues = new HashMap<>();
    private static Integer methodName = 0;
    private static Integer datatypeName = 0;
    private static Integer typeAliasName = 0;
    private static Integer genericName = 0;
    private static Integer functionName = 0;
    private static Integer dclassName = 0;

    public static String generateReturnVariableName(String method) {
        Integer i = returnValues.getOrDefault(method, 1);
        returnValues.put(method, i + 1);
        return String.format("ret_%d", i);
    }

    public static String generateArgumentName(Method method) {
        return generateArgumentName(method.getName());
    }

    public static String generateArgumentName(String method) {
        Integer i = parameterValues.getOrDefault(method, 1);
        parameterValues.put(method, i + 1);
        return String.format("p_%s_%d", method, i);
    }

    public static String generateVariableValueName(Type type, SymbolTable symbolTable) {
        String typeName = type.getName();
        Integer i = variableValues.getOrDefault(typeName, 1);
        variableValues.put(typeName, i + 1);
        String format = String.format("v_%s_%d", typeName, i);
        return format;
    }

    public static String generateMethodName() {
        methodName++;
        return String.format("m_method_%d", methodName);
    }

    public static String generateFunctionName() {
        functionName++;
        return String.format("f_function_%d", functionName);
    }

    public static String generateGenericName() {
        genericName++;
        return String.format("T_%d", genericName);
    }

    public static String generateDatatypeName() {
        datatypeName++;
        return String.format("DT_%d", datatypeName);
    }

    public static String generateDatatypeRuleName(String datatypeName) {
        Integer i = datatypeValues.getOrDefault(datatypeName, 1);
        datatypeValues.put(datatypeName, i + 1);
        String format = String.format("%s_%d", datatypeName, i);
        return format;
    }

    public static String generateDatatypeRuleFieldName(String datatypeRuleName) {
        Integer i = datatypeFieldValues.getOrDefault(datatypeRuleName, 1);
        datatypeFieldValues.put(datatypeRuleName, i + 1);
        String format = String.format("%s_%d", datatypeRuleName, i);
        return format;
    }

    public static String generateTypeAliasName() {
        typeAliasName++;
        return String.format("TYPE_%d", typeAliasName);
    }

    public static String generateDClassName() {
        dclassName++;
        return String.format("CLASS_%d", dclassName);
    }

    public static String generateDClassFieldName(Type type) {
        String typeName = type.getName();
        Integer i = dclassFieldValues.getOrDefault(typeName, 1);
        dclassFieldValues.put(typeName, i + 1);
        String format = String.format("FIELD_%s_%d", typeName, i);
        return format;
    }
}
