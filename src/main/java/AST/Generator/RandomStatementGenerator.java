package AST.Generator;

import AST.Statements.AssertStatement;
import AST.Statements.AssignmentStatement;
import AST.Statements.BlockStatement;
import AST.Statements.Expressions.CallExpression;
import AST.Statements.Expressions.Expression;
import AST.Statements.IfElseStatement;
import AST.Statements.ReturnStatement;
import AST.Statements.Statement;
import AST.Statements.util.PrintAll;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.PrimitiveTypes.Void;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RandomStatementGenerator {

    private static final int MAX_ASSERT_VALUES = 5;
    public static double PROB_RETURN_STAT = 30.0;
    public static double PROB_ASSIGN_STAT = 40.0;
    public static double PROB_IF_ELSE_STAT = 20.0;
    public static double PROB_ASSERT = 30.0;

    public static final double PROB_METHOD_ASSIGN = 0.05;
    public static final double PROB_ELSE_STAT = 0.5;

    public static final int MAX_STATEMENT_DEPTH = 4;
    public static final double PROB_NEXT_STAT = 0.85;
    public static final double PROB_FORCE_RETURN = 0.05;

    private static int statementDepth = 0;


    public Statement generateBody(Method method, SymbolTable symbolTable) {
        BlockStatement body = new BlockStatement(symbolTable);

        double probContinue = GeneratorConfig.getRandom().nextDouble();
        boolean hasReturn = method.hasReturn();
        Statement statement = null;
        while (probContinue < PROB_NEXT_STAT || hasReturn) {
            statement = generateStatement(method, symbolTable);
            body.addStatement(statement);
            if (statement.isReturn()) {
                break;
            }
            probContinue = GeneratorConfig.getRandom().nextDouble();
        }

        if (statement != null && !statement.isReturn()) {
            PrintAll printAll = new PrintAll(body.getSymbolTable());
            body.addStatement(printAll.expand());
        }

        return body;
    }

    private Statement generateStatement(Method method, SymbolTable symbolTable) {
        statementDepth++;
        Statement ret = null;
        while (ret == null) {
            double ratioSum = PROB_RETURN_STAT + PROB_ASSIGN_STAT +
                PROB_IF_ELSE_STAT + PROB_ASSERT;
            double probTypeOfStatement = GeneratorConfig.getRandom().nextDouble() * ratioSum;

            if ((statementDepth > MAX_STATEMENT_DEPTH
                || (probTypeOfStatement -= PROB_RETURN_STAT) < 0)) {
                //return
                ret = generateReturnStatement(method, symbolTable);

            } else if ((probTypeOfStatement -= PROB_ASSIGN_STAT) < 0) {
                //Assign
                PROB_ASSIGN_STAT *= GeneratorConfig.OPTION_DECAY_FACTOR;
                ret = generateAssignmentStatement(symbolTable);

            } else if ((probTypeOfStatement -= PROB_IF_ELSE_STAT) < 0) {
                //IfElse
                PROB_IF_ELSE_STAT *= GeneratorConfig.OPTION_DECAY_FACTOR;
                ret = generateIfElseStatement(method, symbolTable);

            } else if ((probTypeOfStatement -= PROB_ASSERT) < 0) {
                //IfElse
                PROB_ASSERT *= GeneratorConfig.OPTION_DECAY_FACTOR;
                ret = generateAssertStatement(method, symbolTable);

            }
        }
        statementDepth--;
        return ret;
    }

    private Statement generateAssertStatement(Method method, SymbolTable symbolTable) {
        int noOfValues = GeneratorConfig.getRandom().nextInt(MAX_ASSERT_VALUES) + 1;

        List<Variable> variablesInCurrentScope = symbolTable.getAllVariablesInCurrentScope()
            .stream()
            .filter(x -> x.getType().validMethodType())
            .collect(Collectors.toList());
        if (variablesInCurrentScope.isEmpty()) {
            return null;
        }

        Collections.shuffle(variablesInCurrentScope, GeneratorConfig.getRandom());

        List<Variable> vs = variablesInCurrentScope.subList(0, Math.min(noOfValues, variablesInCurrentScope.size()));

        AssertStatement statement = new AssertStatement(symbolTable, vs);
        return statement;
    }

    private ReturnStatement generateReturnStatement(Method method, SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();


        List<Type> types = method.getReturnTypes();

        List<Expression> values = new ArrayList<>();
        for (Type type : types.stream().filter(x -> !x.equals(new Void())).collect(Collectors.toList())) {
            Type concrete = type.concrete(symbolTable);
            Expression expression = expressionGenerator.generateExpression(concrete, symbolTable);
            values.add(expression);
        }

        ReturnStatement statement = new ReturnStatement(symbolTable, values);
        return statement;
    }

    private IfElseStatement generateIfElseStatement(Method method, SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();


        Expression test = expressionGenerator.generateExpression(new Bool(), symbolTable);

        Statement ifStat = generateBody(method, new SymbolTable(symbolTable));

        if (GeneratorConfig.getRandom().nextDouble() < PROB_ELSE_STAT) {
            Statement elseStat = generateBody(method, new SymbolTable(symbolTable));
            IfElseStatement statement = new IfElseStatement(symbolTable, test, ifStat, elseStat);
            return statement;
        } else {
            IfElseStatement statement = new IfElseStatement(symbolTable, test, ifStat);
            return statement;
        }
    }

    private AssignmentStatement generateAssignmentStatement(SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();
        RandomTypeGenerator typeGenerator = new RandomTypeGenerator();

        int noOfReturns = GeneratorConfig.getRandom().nextInt(5) + 1;
        List<Type> returnTypes = typeGenerator.generateTypes(noOfReturns, symbolTable);

        double probReassign = GeneratorConfig.getRandom().nextDouble();
        boolean canReassign = false;

        List<Variable> toReassign = new ArrayList<>();
/*      for (int i = 0, returnTypesSize = returnTypes.size(); canReassign && i < returnTypesSize; i++) {
            Type t = returnTypes.get(i);
            List<Variable> allVariables = symbolTable.getAllVariables(t, false);
            if (allVariables.isEmpty()) {
                canReassign = false;
                break;
            }
            boolean toAdd = true;
            for (int j = 0, allVariablesSize = allVariables.size(); toAdd && j < allVariablesSize; j++) {
                Variable v = allVariables.get(j);
                if (!toReassign.contains(v)) {
                    toReassign.add(v);
                    toAdd = false;
                }
            }

            if (toAdd) {
                canReassign = false;
                break;
            }
        }*/

        if (canReassign) {
            List<Variable> variables = new ArrayList<>();
            List<Expression> value = new ArrayList<>();
            for (Variable v : toReassign) {
                Type type = v.getType().concrete(symbolTable);
                variables.add(v);
                value.add(expressionGenerator.generateExpression(type, symbolTable));
            }
            AssignmentStatement statement = new AssignmentStatement(symbolTable, variables, value);
            return statement;
        }

        double probCallMethod = GeneratorConfig.getRandom().nextDouble() * Math.pow(GeneratorConfig.OPTION_DECAY_FACTOR, statementDepth);
        if (probCallMethod < PROB_METHOD_ASSIGN) {
            //Create method

            CallExpression expression = expressionGenerator.generateCallExpression(symbolTable, returnTypes);
            if (expression != null) {
                List<Variable> variables = returnTypes.stream()
                    .map(x -> new Variable(VariableNameGenerator.generateVariableValueName(x, symbolTable), x))
                    .collect(Collectors.toList());

                AssignmentStatement statement = new AssignmentStatement(symbolTable, variables, expression);
                return statement;
            }
        }
        List<Variable> variables = new ArrayList<>();
        List<Expression> value = new ArrayList<>();

        for (Type t : returnTypes) {
            Type concrete = t.concrete(symbolTable);
            Expression expression = expressionGenerator.generateExpression(concrete, symbolTable);

            variables.add(new Variable(VariableNameGenerator.generateVariableValueName(t, symbolTable), t));
            value.add(expression);
        }

        AssignmentStatement statement = new AssignmentStatement(symbolTable, variables, value);
        return statement;
    }

}
