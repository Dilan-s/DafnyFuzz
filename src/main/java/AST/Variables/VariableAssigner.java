package AST.Variables;

public class VariableAssigner {

    private static int intCount = 0;
    private static int boolCount = 0;
    private static int methodCount = 0;

    public static String generateIntVariableName() {
        StringBuilder variable = new StringBuilder();
        variable.append("i_");
        variable.append(intCount);
        intCount++;
        return variable.toString();
    }

    public static String generateBoolVariableName() {
        StringBuilder variable = new StringBuilder();
        variable.append("b_");
        variable.append(boolCount);
        boolCount++;
        return variable.toString();
    }

    public static String generateMethodName() {
        StringBuilder method = new StringBuilder();
        method.append("method_");
        method.append(methodCount);
        methodCount++;
        return method.toString();
    }

}
