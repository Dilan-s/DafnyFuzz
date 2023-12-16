package AST.Generator;

import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.List;

public class RandomMethodGenerator {

    public static final double PROB_REUSE_METHOD = 0.75;
    public static final double PROB_CLASS_METHOD = 0.25;
    public static final int MAX_METHOD_DEPTH = 5;
    public static final int MAX_NO_OF_ARGS = 5;

    private static int methodDepth = 0;

    public Method generateMethod(List<Type> returnTypes, SymbolTable symbolTable) {
        if (methodDepth > MAX_METHOD_DEPTH) {
            return null;
        }

        List<Method> methodWithSameType = symbolTable.getMethodWithTypes(returnTypes);

        double probReuseMethod = GeneratorConfig.getRandom().nextDouble();

        if (!methodWithSameType.isEmpty() && probReuseMethod < PROB_REUSE_METHOD) {
            int i = GeneratorConfig.getRandom().nextInt(methodWithSameType.size());
            return methodWithSameType.get(i);
        }

        double probClassMethod = GeneratorConfig.getRandom().nextDouble();
        if (probClassMethod < PROB_CLASS_METHOD) {
            return generateClassMethod(returnTypes, symbolTable);
        }

        return generateBaseMethod(returnTypes, symbolTable);
    }

    private Method generateBaseMethod(List<Type> returnTypes, SymbolTable symbolTable) {
        String methodName = VariableNameGenerator.generateMethodName();
        Method m = new Method(returnTypes, methodName);

        List<Type> args = generateArgTypes(symbolTable);
        for (Type t : args) {
            Variable var = new Variable(VariableNameGenerator.generateArgumentName(m), t);
            m.addArgument(var);
        }

        methodDepth++;
        RandomStatementGenerator statementGenerator = new RandomStatementGenerator();
        Statement statement = statementGenerator.generateBody(m, m.getSymbolTable());
        methodDepth--;
        m.setBody(statement);

        symbolTable.addMethod(m);

        return m;
    }

    private List<Type> generateArgTypes(SymbolTable symbolTable) {
        RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
        int noOfArgs = GeneratorConfig.getRandom().nextInt(MAX_NO_OF_ARGS);
        List<Type> args = typeGenerator.generateMethodTypes(noOfArgs, symbolTable);
        return args;
    }

    private Method generateClassMethod(List<Type> returnTypes, SymbolTable symbolTable) {
        return generateBaseMethod(returnTypes, symbolTable);
    }


    public void enableMethods() {
        methodDepth -= MAX_METHOD_DEPTH;
    }

    public void disableMethods() {
        methodDepth += MAX_METHOD_DEPTH;
    }
}
