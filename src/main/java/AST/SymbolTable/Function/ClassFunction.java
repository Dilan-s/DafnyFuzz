package AST.SymbolTable.Function;

import AST.Expressions.Expression;
import AST.Expressions.Function.CallBaseFunctionExpression;
import AST.Expressions.Function.CallClassFunctionExpression;
import AST.Expressions.Function.CallFunctionExpression;
import AST.Expressions.Variable.VariableClassFunctionExpression;
import AST.Expressions.Variable.VariableFunctionExpression;
import AST.Generator.RandomExpressionGenerator;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.DClass;
import AST.SymbolTable.Types.Variables.Variable;
import AST.SymbolTable.Types.Variables.VariableThis;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassFunction extends Function {

    private final DClass dClass;
    private VariableThis thisVariable;

    public ClassFunction(String name, Type returnType, List<Variable> args, SymbolTable symbolTable, DClass dClass) {
        super(name, returnType, args, symbolTable);
        this.dClass = dClass;
        addThisToMethod();
    }

    public void addThisToMethod() {
        this.thisVariable = dClass.getThis();
        for (Variable arg : thisVariable.getSymbolTableArgs()) {
            getSymbolTable().addVariable(arg);
            arg.setDeclared();
        }
    }

    @Override
    public void assignThis(Variable classVariable) {
        thisVariable.set(classVariable);
    }

    @Override
    protected List<String> getRequiresClauses(Map<Variable, Variable> requiresEnsures) {
        List<String> requiresClauses = super.getRequiresClauses(requiresEnsures);
        if (requiresClauses == null) {
            return null;
        }

        List<Variable> symbolTableArgs = thisVariable.getSymbolTableArgs().stream()
            .filter(v -> v != thisVariable)
            .collect(Collectors.toList());
        for (Variable variable : symbolTableArgs) {
            String name = variable.getName();
            String v = variable.getType().formatEnsures(name, variable.getValue().get(0));
            if (v == null) {
                return null;
            }
            requiresClauses.add(v);
        }
        return requiresClauses;
    }

    @Override
    public CallFunctionExpression generateCall(SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();

        List<Type> argTypes = getArgTypes();
        List<Expression> args = new ArrayList<>();

        for (Type t : argTypes) {
            Type concrete = t.concrete(symbolTable);
            Expression exp = expressionGenerator.generateExpression(concrete, symbolTable);
            args.add(exp);
        }

        Expression classExp = expressionGenerator.generateExpression(dClass, symbolTable);

        CallFunctionExpression expression = new CallClassFunctionExpression(symbolTable, this, classExp, args);
        return expression;
    }

    @Override
    public Expression generateFunctionVariable(SymbolTable symbolTable, Type type) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();
        Expression classExp = expressionGenerator.generateExpression(dClass, symbolTable);
        Expression expression = new VariableClassFunctionExpression(
          symbolTable, this, type, dClass, classExp);
        return expression;
    }
}
