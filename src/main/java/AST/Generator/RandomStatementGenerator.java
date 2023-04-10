package AST.Generator;

import AST.Statements.AssignmentStatement;
import AST.Statements.BlockStatement;
import AST.Statements.Expressions.CallExpression;
import AST.Statements.Expressions.Expression;
import AST.Statements.IfElseStatement;
import AST.Statements.PrintStatement;
import AST.Statements.ReturnStatement;
import AST.Statements.Statement;
import AST.Statements.util.PrintAll;
import AST.Statements.util.ReturnStatus;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RandomStatementGenerator {

    public static final double PROB_RETURN_STAT = 0.4;
    public static final double PROB_ASSIGN_STAT = PROB_RETURN_STAT + 0.2;
    public static final double PROB_PRINT_STAT = PROB_ASSIGN_STAT + 0.2;
    public static final double PROB_IF_ELSE_STAT = PROB_PRINT_STAT + 0.2;

    public static final double PROB_METHOD_ASSIGN = 0.05;
    public static final double PROB_ELSE_STAT = 0.5;

    public static final int MAX_STATEMENT_DEPTH = 5;
    public static final double PROB_NEXT_STAT = 0.85;
    public static final double PROB_FORCE_RETURN = 0.02;

    private static int statementDepth = 0;


    public Statement generateBody(Method method) {
        BlockStatement body = new BlockStatement(method.getSymbolTable());

        double probContinue = GeneratorConfig.getRandom().nextDouble();
        boolean hasReturn = method.hasReturn();
        Statement statement = null;
        while (probContinue < PROB_NEXT_STAT || hasReturn) {
            statement = generateStatement(method, body.getSymbolTable());
            body.addStatement(statement.expand());
            if (statement.isReturn()) {
                break;
            }
            double probForceReturn = GeneratorConfig.getRandom().nextDouble();
            if (probForceReturn < PROB_FORCE_RETURN) {
                if (hasReturn) {
                    statement = generateReturnStatement(method, body.getSymbolTable());
                    body.addStatement(statement);
                }
                break;
            }
            probContinue = GeneratorConfig.getRandom().nextDouble();
        }

        if (statement != null && !statement.isReturn()) {
            PrintAll printAll = new PrintAll(body.getSymbolTable());
            body.addStatement(printAll);
        }

        return body;
    }

    private Statement generateStatement(Method method, SymbolTable symbolTable) {
        statementDepth++;
        Statement ret = null;
        while (ret == null) {
            double probTypeOfStatement =
                GeneratorConfig.getRandom().nextDouble() * Math.pow(GeneratorConfig.DECAY_FACTOR,
                    statementDepth - 1);
            if ((statementDepth > MAX_STATEMENT_DEPTH || probTypeOfStatement < PROB_RETURN_STAT)
                && method.hasReturn()) {
                //return
                ret = generateReturnStatement(method, symbolTable);

            } else if (statementDepth > MAX_STATEMENT_DEPTH
                || probTypeOfStatement < PROB_ASSIGN_STAT) {
                //Assign
                ret = generateAssignmentStatement(symbolTable);

            } else if (probTypeOfStatement < PROB_PRINT_STAT) {
                //Print
//                ret = generatePrintStatement(symbolTable);
            } else if (probTypeOfStatement < PROB_IF_ELSE_STAT) {
                //IfElse
                ret = generateIfElseStatement(method, symbolTable);

            }
        }
        statementDepth--;
        return ret;
    }

    private ReturnStatement generateReturnStatement(Method method, SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();


        List<Type> types = method.getReturnTypes();

        List<Expression> values = new ArrayList<>();
        for (Type type : types) {
            Type concrete = type.concrete(symbolTable);
            Expression expression = expressionGenerator.generateExpression(concrete, symbolTable);
            values.add(expression);
        }

        ReturnStatement statement = new ReturnStatement(symbolTable, values);
        return statement;
    }

    private PrintStatement generatePrintStatement(SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();
        RandomTypeGenerator typeGenerator = new RandomTypeGenerator();

        PrintStatement statement = new PrintStatement(symbolTable);

        int noOfValues = GeneratorConfig.getRandom().nextInt(10) + 1;
        List<Type> types = typeGenerator.generateTypes(noOfValues, symbolTable);

        for (Type type : types) {
            Expression expression = expressionGenerator.generateExpression(type, symbolTable);
            statement.addValue(expression);
        }

        return statement;
    }

    private IfElseStatement generateIfElseStatement(Method method, SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();

        IfElseStatement statement = new IfElseStatement(symbolTable);

        Expression test = expressionGenerator.generateExpression(new Bool(), symbolTable);
        statement.setTest(test);

        Statement ifStat = generateBody(method);
        statement.setIfStat(ifStat);

        if (GeneratorConfig.getRandom().nextDouble() < PROB_ELSE_STAT) {
            Statement elseStat = generateBody(method);
            statement.setElseStat(elseStat);
        }

        return statement;
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

        double probCallMethod = GeneratorConfig.getRandom().nextDouble() * Math.pow(GeneratorConfig.DECAY_FACTOR, statementDepth);
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
