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
import AST.SymbolTable.Method;
import AST.SymbolTable.PrimitiveTypes.Bool;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import java.util.List;
import java.util.spi.AbstractResourceBundleProvider;

public class RandomStatementGenerator {

    public static final double PROB_RETURN_STAT = 0.35;
    public static final double PROB_ASSIGN_STAT = PROB_RETURN_STAT + 0.3;
    public static final double PROB_PRINT_STAT = PROB_ASSIGN_STAT + 0.2;
    public static final double PROB_IF_ELSE_STAT = PROB_PRINT_STAT + 0.15;
    public static final double PROB_METHOD_ASSIGN = 0.05;
    public static final double PROB_ELSE_STAT = 0.5;

    public static final int MAX_STATEMENT_DEPTH = 7;

    private static int statementDepth = 0;


    public Statement generateBody(Method method) {
        BlockStatement body = new BlockStatement(method.getSymbolTable());

        double probContinue = GeneratorConfig.getRandom().nextDouble();
        boolean hasReturn = method.hasReturn();
        Statement statement = null;
        while (probContinue < 0.9 || hasReturn) {
            statement = generateStatement(method, body.getSymbolTable());
            body.addStatement(statement);
            if (statement.isReturn()) {
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
                ret = generatePrintStatement(symbolTable);
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

        ReturnStatement statement = new ReturnStatement(symbolTable);

        List<Type> types = method.getReturnTypes();
        for (Type type : types) {
            Expression expression = expressionGenerator.generateExpression(type, symbolTable);
            statement.addValue(expression);
        }

        return statement;
    }

    private PrintStatement generatePrintStatement(SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();
        RandomTypeGenerator typeGenerator = new RandomTypeGenerator();

        PrintStatement statement = new PrintStatement(symbolTable);

        int noOfValues = GeneratorConfig.getRandom().nextInt(5) + 1;
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

        AssignmentStatement statement = new AssignmentStatement(symbolTable);
        int noOfReturns = GeneratorConfig.getRandom().nextInt(5) + 1;
        List<Type> returnTypes = typeGenerator.generateTypes(noOfReturns, symbolTable);

        double probCallMethod = GeneratorConfig.getRandom().nextDouble() * Math.pow(GeneratorConfig.DECAY_FACTOR, statementDepth);
        if (probCallMethod < PROB_METHOD_ASSIGN) {
            //Create method
            CallExpression expression = expressionGenerator.generateCallExpression(symbolTable, returnTypes);
            if (expression != null) {
                statement.addAssignment(expression);
                statement.addAssignmentsToSymbolTable();
                return statement;
            }
        }
        for (Type t : returnTypes) {
            Expression expression = expressionGenerator.generateExpression(t, symbolTable);
            statement.addAssignment(expression);
        }

        statement.addAssignmentsToSymbolTable();

        return statement;
    }

}
