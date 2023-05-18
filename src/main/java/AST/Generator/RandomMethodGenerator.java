package AST.Generator;

import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.List;

public class RandomMethodGenerator {

    public static final double PROB_REUSE_METHOD = 0.75;
    public static final int MAX_METHOD_DEPTH = 5;
    public static final int MAX_NO_OF_ARGS = 4;

    private static int methodDepth = 0;

    public Method generateMethod(List<Type> returnTypes, SymbolTable symbolTable) {
        if (methodDepth > MAX_METHOD_DEPTH) {
            return null;
        }

        RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
        RandomStatementGenerator statementGenerator = new RandomStatementGenerator();

        methodDepth++;
        List<Method> methodWithSameType = symbolTable.getMethodWithTypes(returnTypes);

        double probReuseMethod = GeneratorConfig.getRandom().nextDouble() * Math.pow(GeneratorConfig.OPTION_DECAY_FACTOR,
            methodDepth - 1);
        if (!methodWithSameType.isEmpty() && probReuseMethod < PROB_REUSE_METHOD) {
            int i = GeneratorConfig.getRandom().nextInt(methodWithSameType.size());
            return methodWithSameType.get(i);
        }

        String methodName = VariableNameGenerator.generateMethodName();
        Method m = new Method(returnTypes, methodName);

        int noOfArgs = GeneratorConfig.getRandom().nextInt(MAX_NO_OF_ARGS) + 1;
        List<Type> args = typeGenerator.generateMethodTypes(noOfArgs, symbolTable);
        for (Type t : args) {
            Variable var = new Variable(VariableNameGenerator.generateArgumentName(m), t);
            m.addArgument(var);
        }
        symbolTable.addMethod(m);

        Statement statement = statementGenerator.generateBody(m, m.getSymbolTable());
        methodDepth--;
        m.setBody(statement);

        //        symbolTable.addMethod(msimple);

        return m;
    }

}
