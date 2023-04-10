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
    private static Integer methodName = 0;

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
        return String.format("method_%d", methodName);
    }
}
