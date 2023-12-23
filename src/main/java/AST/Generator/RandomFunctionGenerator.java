package AST.Generator;

import AST.Expressions.Expression;
import AST.SymbolTable.Function.ClassFunction;
import AST.SymbolTable.Function.Function;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.DClass;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.List;
import java.util.stream.Collectors;

public class RandomFunctionGenerator {

    public static final double PROB_REUSE_FUNCTION = 0.75;
    public static final double PROB_CLASS_FUNCTION = 0.25;
    public static final int MAX_FUNCTION_DEPTH = 5;
    public static final int MAX_NO_OF_ARGS = 5;

    private static int functionDepth = 0;

    public Function generateFunction(Type returnType, SymbolTable symbolTable) {
        if (functionDepth > MAX_FUNCTION_DEPTH) {
            return null;
        }

        RandomTypeGenerator typeGenerator = new RandomTypeGenerator();

        List<Function> functionWithSameType = symbolTable.getFunctionWithType(returnType);

        double probReuseFunction = GeneratorConfig.getRandom().nextDouble();

        if (!functionWithSameType.isEmpty() && probReuseFunction < PROB_REUSE_FUNCTION) {
            int i = GeneratorConfig.getRandom().nextInt(functionWithSameType.size());
            return functionWithSameType.get(i);
        }

        int noOfArgs = GeneratorConfig.getRandom().nextInt(MAX_NO_OF_ARGS);
        List<Type> argTypes = typeGenerator.generateFunctionTypes(noOfArgs, symbolTable);

        double probClassFunction = GeneratorConfig.getRandom().nextDouble();
        if (probClassFunction < PROB_CLASS_FUNCTION) {
            return generateClassFunction(returnType, symbolTable, argTypes);
        }

        return generateBaseFunction(returnType, symbolTable, argTypes);
    }

    private Function generateClassFunction(Type returnType, SymbolTable symbolTable, List<Type> argTypes) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();
        RandomMethodGenerator methodGenerator = new RandomMethodGenerator();
        RandomTypeGenerator typeGenerator = new RandomTypeGenerator();

        DClass dClass = typeGenerator.generateClass().concrete(symbolTable).asDClass();

        String functionName = VariableNameGenerator.generateFunctionName();

        List<Variable> args = argTypes.stream()
            .map(t -> new Variable(VariableNameGenerator.generateArgumentName(functionName), t))
            .collect(Collectors.toList());

        functionDepth++;
        ClassFunction f;
        SymbolTable st;
        Expression body;
        do {
            st = new SymbolTable();
            f = new ClassFunction(functionName, returnType, args, st, dClass);
            for (Variable arg : args) {
                arg.setConstant();
                for (Variable tableArg : arg.getSymbolTableArgs()) {
                    st.addVariable(tableArg);
                    tableArg.setDeclared();
                }
            }

            methodGenerator.disableMethods();
            body = expressionGenerator.generateExpression(returnType, st);
        } while (!body.validForFunctionBody());

        f.setBody(body);
        methodGenerator.enableMethods();

        functionDepth--;

        symbolTable.addClassFunction(f);
        dClass.addFunction(f);
        return f;
    }

    public Function generateBaseFunction(Type returnType, SymbolTable symbolTable, List<Type> argTypes) {

        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();
        RandomMethodGenerator methodGenerator = new RandomMethodGenerator();

        String functionName = VariableNameGenerator.generateFunctionName();

        List<Variable> args = argTypes.stream()
            .map(t -> new Variable(VariableNameGenerator.generateArgumentName(functionName), t))
            .collect(Collectors.toList());

        functionDepth++;
        Function f;
        SymbolTable st;
        Expression body;
        do {
            st = new SymbolTable();
            f = new Function(functionName, returnType, args, symbolTable);
            for (Variable arg : args) {
                arg.setConstant();
                for (Variable tableArg : arg.getSymbolTableArgs()) {
                    st.addVariable(tableArg);
                    tableArg.setDeclared();
                }
            }

            methodGenerator.disableMethods();
            body = expressionGenerator.generateExpression(returnType, st);
        } while (!body.validForFunctionBody());

        f.setBody(body);
        methodGenerator.enableMethods();

        functionDepth--;

        symbolTable.addFunction(f);
        return f;
    }
}
