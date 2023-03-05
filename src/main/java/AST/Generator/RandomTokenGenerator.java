package AST.Generator;

import AST.Errors.InvalidArgumentException;
import AST.Statements.AssignmentStatement;
import AST.Statements.BlockStatement;
import AST.Statements.Expressions.CallExpression;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.IfElseExpression;
import AST.Statements.Expressions.Operator.BinaryOperator;
import AST.Statements.Expressions.Operator.Operator;
import AST.Statements.Expressions.Operator.UnaryOperator;
import AST.Statements.Expressions.OperatorExpression;
import AST.Statements.Expressions.VariableExpression;
import AST.Statements.IfElseStatement;
import AST.Statements.PrintStatement;
import AST.Statements.ReturnStatement;
import AST.Statements.Statement;
import AST.Statements.util.PrintAll;
import AST.SymbolTable.Method;
import AST.SymbolTable.PrimitiveTypes.Bool;
import AST.SymbolTable.PrimitiveTypes.Char;
import AST.SymbolTable.PrimitiveTypes.DSet;
import AST.SymbolTable.PrimitiveTypes.Int;
import AST.SymbolTable.PrimitiveTypes.Real;
import AST.SymbolTable.PrimitiveTypes.Seq;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomTokenGenerator {

    public static final double PROB_RETURN_STAT = 0.2;
    public static final double PROB_ASSIGN_STAT = PROB_RETURN_STAT + 0.4;
    public static final double PROB_PRINT_STAT = PROB_ASSIGN_STAT + 0.3;
    public static final double PROB_IF_ELSE_STAT = PROB_PRINT_STAT + 0.1;

    public static final double PROB_METHOD_ASSIGN = 0.05;
    public static final double PROB_ELSE_STAT = 0.5;

    public static final double PROB_LITERAL_EXPRESSION = 0.3;
    public static final double PROB_OPERATOR_EXPRESSION = PROB_LITERAL_EXPRESSION + 0.45;
    public static final double PROB_VARIABLE_EXPRESSION = PROB_OPERATOR_EXPRESSION + 0.15;
    public static final double PROB_IF_ELSE_EXPRESSION = PROB_VARIABLE_EXPRESSION + 0.05;
    public static final double PROB_CALL_EXPRESSION = PROB_IF_ELSE_EXPRESSION + 0.05;

    public static final double PROB_REUSE_METHOD = 0.75;
    public static final int MAX_METHOD_DEPTH = 5;
    public static final int MAX_STATEMENT_DEPTH = 7;
    public static final int MAX_EXPRESSION_DEPTH = 7;
    public static final int MAX_TYPE_DEPTH = 2;
    public static final double DECAY_FACTOR = 0.95;

    private final Random random;

    private static int statementDepth = 0;
    private static int expressionDepth = 0;
    private static int methodDepth = 0;
    private static int typeDepth = 0;

    public RandomTokenGenerator(Random random) {
        this.random = random;
    }

    public Statement generateBody(Method method) {
        BlockStatement body = new BlockStatement(method.getSymbolTable());

        double probContinue = random.nextDouble();
        boolean hasReturn = method.hasReturn();
        Statement statement = null;
        while (probContinue < 0.9 || hasReturn) {
            statement = generateStatement(method, body.getSymbolTable());
            body.addStatement(statement);
            if (statement.isReturn()) {
                break;
            }
            probContinue = random.nextDouble();
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
                random.nextDouble() * Math.pow(DECAY_FACTOR, statementDepth - 1);
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
        ReturnStatement statement = new ReturnStatement(symbolTable);
        List<Type> types = method.getReturnTypes();
        for (Type type : types) {
            Expression expression = generateExpression(type, symbolTable);
            statement.addValue(expression);
        }

        return statement;
    }

    private PrintStatement generatePrintStatement(SymbolTable symbolTable) {
        PrintStatement statement = new PrintStatement(symbolTable);
        int noOfValues = random.nextInt(5) + 1;
        List<Type> types = generateTypes(noOfValues, symbolTable);

        for (Type type : types) {
            Expression expression = generateExpression(type, symbolTable);
            statement.addValue(expression);
        }

        return statement;
    }

    private IfElseStatement generateIfElseStatement(Method method, SymbolTable symbolTable) {
        IfElseStatement statement = new IfElseStatement(symbolTable);

        Expression test = generateExpression(new Bool(), symbolTable);
        statement.setTest(test);
        Statement ifStat = generateBody(method);
        statement.setIfStat(ifStat);
        if (random.nextDouble() < PROB_ELSE_STAT) {
            Statement elseStat = generateBody(method);
            statement.setElseStat(elseStat);
        }

        return statement;
    }

    private AssignmentStatement generateAssignmentStatement(SymbolTable symbolTable) {
        AssignmentStatement statement = new AssignmentStatement(symbolTable);
        int noOfReturns = random.nextInt(5) + 1;
        List<Type> returnTypes = generateTypes(noOfReturns, symbolTable);

        double probCallMethod = random.nextDouble() * Math.pow(DECAY_FACTOR, methodDepth - 1);
        if (probCallMethod < PROB_METHOD_ASSIGN && methodDepth < MAX_METHOD_DEPTH
            && statementDepth < MAX_METHOD_DEPTH) {
            //Create method
            CallExpression expression = generateCallExpression(symbolTable, returnTypes);
            statement.addAssignment(expression);
        } else {
            //Explicit values
            for (Type t : returnTypes) {
                Expression expression = generateExpression(t, symbolTable);
                statement.addAssignment(expression);
            }
        }

        statement.addAssignmentsToSymbolTable();

        return statement;
    }

    private CallExpression generateCallExpression(SymbolTable symbolTable, List<Type> returnTypes) {
        methodDepth++;
        Method m = generateMethod(returnTypes, symbolTable);
        methodDepth--;
        CallExpression expression = new CallExpression(m);
        expression.setSymbolTable(symbolTable);
        List<Type> argTypes = m.getArgTypes();
        int i = 0;
        while (i < argTypes.size()) {
            Type t = argTypes.get(i);
            try {
                Expression exp = generateExpression(t, symbolTable);
                expression.addArg(exp);
                i++;
            } catch (InvalidArgumentException e) {
                System.err.println("Could not generate argument");
            }
        }
        return expression;
    }

    private Method generateMethod(List<Type> returnTypes, SymbolTable symbolTable) {
        List<Method> methodWithSameType = symbolTable.getMethodWithTypes(returnTypes);

        if (!methodWithSameType.isEmpty() && random.nextDouble() < PROB_REUSE_METHOD) {
            int i = random.nextInt(methodWithSameType.size());
            return methodWithSameType.get(i);
        }

        String methodName = VariableNameGenerator.generateMethodName();
        Method m = new Method(returnTypes, methodName);

        RandomTokenGenerator mStatGen = new RandomTokenGenerator(random);

        int noOfArgs = random.nextInt(5) + 1;
        List<Type> args = generateTypes(noOfArgs, symbolTable);
        List<Variable> vars = new ArrayList<>();
        for (Type t : args) {
            Variable var = new Variable(VariableNameGenerator.generateArgumentName(m), t);
            vars.add(var);
            m.addArgument(var);
        }

        Statement statement = mStatGen.generateBody(m);
        m.setBody(statement);

        System.out.println(m);

        Method msimple = m.getSimpleMethod();
        symbolTable.addMethod(msimple);

        return msimple;
    }

    public Type generateNonCollectionType(int noOfTypes, SymbolTable symbolTable) {
        typeDepth += MAX_TYPE_DEPTH;
        List<Type> types = generateTypes(noOfTypes, symbolTable);
        typeDepth -= MAX_TYPE_DEPTH;
        return types.get(0);
    }

    public List<Type> generateTypes(int noOfTypes, SymbolTable symbolTable) {
        typeDepth++;
        List<Type> types = new ArrayList<>();
        List<Type> option = new ArrayList<>(List.of(new Int(), new Bool(), new Char(), new Real()));

        if (typeDepth < MAX_TYPE_DEPTH) {
            List<Type> collections = new ArrayList<>();
            collections.addAll(option.stream().map(DSet::new).collect(Collectors.toList()));
            collections.addAll(option.stream().map(Seq::new).collect(Collectors.toList()));
            option.addAll(collections);
        }
        for (int i = 0; i < noOfTypes; i++) {
            int randType = random.nextInt(option.size());
            types.add(option.get(randType));
        }

        typeDepth--;
        return types;
    }

    public Expression generateExpression(Type type, SymbolTable symbolTable) {
        Expression ret = null;
        expressionDepth++;
        while (ret == null) {
            double probTypeOfExpression =
                random.nextDouble() * Math.pow(DECAY_FACTOR, expressionDepth - 1);
            if (expressionDepth > MAX_EXPRESSION_DEPTH
                || probTypeOfExpression < PROB_LITERAL_EXPRESSION) {
                //literal
                ret = generateLiteral(type, symbolTable);

            } else if (probTypeOfExpression < PROB_OPERATOR_EXPRESSION && type.operatorExists()) {
                //Operator
                OperatorExpression expression = generateOperatorExpression(type, symbolTable);
                if (expression != null) {
                    ret = expression;
                }
            } else if (probTypeOfExpression < PROB_VARIABLE_EXPRESSION) {
                //variable
                VariableExpression expression = generateVariableExpression(type, symbolTable);
                if (expression != null) {
                    ret = expression;
                }
            } else if (probTypeOfExpression < PROB_IF_ELSE_EXPRESSION) {
                //ifElse
                ret = generateIfElseExpression(type, symbolTable);

            } else if (probTypeOfExpression < PROB_CALL_EXPRESSION
                && methodDepth < MAX_METHOD_DEPTH) {
                //call
                ret = generateCallExpression(symbolTable, List.of(type));
            }
        }
        expressionDepth--;
        return ret;
    }

    private VariableExpression generateVariableExpression(Type type, SymbolTable symbolTable) {
        List<Variable> variables = symbolTable.getAllVariables(type);
        if (!variables.isEmpty()) {
            int index = random.nextInt(variables.size());
            VariableExpression expression = new VariableExpression(variables.get(index));
            expression.setSymbolTable(symbolTable);
            return expression;
        }
        return null;
    }

    private OperatorExpression generateOperatorExpression(Type type, SymbolTable symbolTable) {
        Operator operator = generateOperator(type);
        if (operator == null) {
            return null;
        }
        OperatorExpression expression = new OperatorExpression(operator);

        List<List<Type>> typeArgs = operator.getTypeArgs();
        int randType = random.nextInt(typeArgs.size());
        List<Type> types = typeArgs.get(randType);

        types = operator.concreteType(random, types, symbolTable, type);

        for (Type t : types) {
            Expression arg = generateExpression(t, symbolTable);
            expression.addArgument(arg);
        }

        expression.setType(type);
        expression.setSymbolTable(symbolTable);

        return expression;
    }

    private IfElseExpression generateIfElseExpression(Type type, SymbolTable symbolTable) {
        Expression test = generateExpression(new Bool(), symbolTable);
        Expression ifExp = generateExpression(type, symbolTable);
        Expression elseExp = generateExpression(type, symbolTable);
        IfElseExpression expression = new IfElseExpression(test, ifExp, elseExp);
        expression.setSymbolTable(symbolTable);
        return expression;
    }

    private Expression generateLiteral(Type type, SymbolTable symbolTable) {
        Expression expression = type.generateLiteral(random, symbolTable);
        expression.setSymbolTable(symbolTable);
        return expression;
    }

    private Operator generateOperator(Type type) {
        List<Operator> ops = Arrays.stream(BinaryOperator.values()).collect(Collectors.toList());
        ops.addAll(Arrays.stream(UnaryOperator.values()).collect(Collectors.toList()));

        List<Operator> validOperators = new ArrayList<>();
        for (Operator x : ops) {
            if (x.returnType(type)) {
                validOperators.add(x);
            }
        }

        if (validOperators.size() > 0) {
            int randOp = random.nextInt(validOperators.size());
            return validOperators.get(randOp);
        }
        return null;
    }

}
