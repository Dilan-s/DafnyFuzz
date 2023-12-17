package AST.SymbolTable.Method;

import AST.Expressions.Expression;
import AST.Expressions.Method.CallClassMethodExpression;
import AST.Expressions.Method.CallMethodExpression;
import AST.Expressions.VariableExpression;
import AST.Generator.RandomExpressionGenerator;
import AST.Statements.AssignmentStatement;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.DClass;
import AST.SymbolTable.Types.Variables.Variable;
import AST.SymbolTable.Types.Variables.VariableThis;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassMethod extends Method {

    private DClass dClass;
    private VariableThis thisVariable;

    public ClassMethod(List<Type> returnTypes, String name, SymbolTable symbolTable,
        List<Variable> args, DClass dClass) {
        super(returnTypes, name, symbolTable, args);
        this.dClass = dClass;
        addThisToMethod();
    }

    public ClassMethod(List<Type> returnTypes, String name, SymbolTable symbolTable,
        DClass dClass) {
        this(returnTypes, name, symbolTable, new ArrayList<>(), dClass);
    }

    public ClassMethod(Type returnTypes, String name, SymbolTable symbolTable, DClass dClass) {
        this(List.of(returnTypes), name, symbolTable, dClass);
    }

    public ClassMethod(Type returnTypes, String name, DClass dClass) {
        this(List.of(returnTypes), name, new SymbolTable(), dClass);
    }

    public ClassMethod(List<Type> returnTypes, String name, DClass dClass) {
        this(returnTypes, name, new SymbolTable(), dClass);
    }

    public void addThisToMethod() {
        this.thisVariable = dClass.getThis();
        for (Variable arg : thisVariable.getSymbolTableArgs()) {
            getSymbolTable().addVariable(arg);
            arg.setDeclared();
        }
    }

    @Override
    public CallMethodExpression generateCall(SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();

        List<Type> argTypes = getArgTypes();
        List<Expression> args = new ArrayList<>();
        for (Type t : argTypes) {
            Type concrete = t.concrete(symbolTable);
            Expression exp = expressionGenerator.generateExpression(concrete, symbolTable);
            args.add(exp);
        }

        Expression classExp = expressionGenerator.generateExpression(dClass, symbolTable);

        CallClassMethodExpression expression = new CallClassMethodExpression(symbolTable, this,
            classExp, args);
        return expression;
    }

    @Override
    public void assignThis(Variable classVariable, Map<Variable, Variable> paramMap, StringBuilder s) {
        VariableExpression varExp = new VariableExpression(getSymbolTable(), classVariable, classVariable.getType());
        thisVariable.set(classVariable);
//        new AssignmentStatement(getSymbolTable(), List.of(thisVariable), varExp).execute(paramMap, s);
    }
}
