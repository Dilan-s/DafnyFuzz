package AST.Generator;

import AST.Statements.AssignmentStatement;
import AST.Statements.BlockStatement;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.IfElseExpression;
import AST.Statements.Expressions.Operator.BoolOperator;
import AST.Statements.Expressions.Operator.NumericOperator;
import AST.Statements.Expressions.Operator.Operator;
import AST.Statements.Expressions.OperatorExpression;
import AST.Statements.Expressions.VariableExpression;
import AST.Statements.IfElseStatement;
import AST.Statements.PrintStatement;
import AST.Statements.ReturnStatement;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.PrimitiveTypes.Bool;
import AST.SymbolTable.PrimitiveTypes.Char;
import AST.SymbolTable.PrimitiveTypes.Int;
import AST.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StatementGenerator {

    public static final double PROB_ASSIGN_STAT = 0.5;
    public static final double PROB_IF_ELSE_STAT = PROB_ASSIGN_STAT + 0.1;
    public static final double PROB_PRINT_STAT = PROB_IF_ELSE_STAT + 0.25;
    public static final double PROB_RETURN_STAT = PROB_PRINT_STAT + 0.15;

    public static final double PROB_METHOD_ASSIGN = 0.05;

    public static final double PROB_LITERAL_EXPRESSION = 0.4;
    public static final double PROB_CALL_EXPRESSION = PROB_LITERAL_EXPRESSION + 0.1;
    public static final double PROB_IF_ELSE_EXPRESSION = PROB_CALL_EXPRESSION + 0.05;
    public static final double PROB_OPERATOR_EXPRESSION = PROB_IF_ELSE_EXPRESSION + 0.3;
    private static final double PROB_VARIABLE_EXPRESSION = PROB_OPERATOR_EXPRESSION + 0.15;
    private final Random random;

    private int statementDepth;
    private int expressionDepth;

    public StatementGenerator(Random random) {
        this.random = random;
        this.statementDepth = 0;
        this.expressionDepth = 0;
    }

    public Statement generateBody(Method method) {
        BlockStatement body = new BlockStatement(method.getSymbolTable());

        double probContinue = random.nextDouble();
        boolean hasReturn = method.hasReturn();
        while (probContinue < 0.9 || hasReturn) {
            statementDepth++;
            Statement statement = generateStatement(method, body.getSymbolTable());
            statementDepth--;
            body.addStatement(statement);
            if (statement.isReturn()) {
                break;
            }
            probContinue = random.nextDouble();
        }

        return body;
    }

    private Statement generateStatement(Method method, SymbolTable symbolTable) {

        while (true) {
            double probTypeOfStatement = random.nextDouble();
            if (statementDepth > 10 || probTypeOfStatement < PROB_ASSIGN_STAT) {
                //Assign
                AssignmentStatement statement = new AssignmentStatement(symbolTable);
                int noOfReturns = random.nextInt(5) + 1;
                List<Type> returnTypes = generateTypes(noOfReturns);

//                double probCallMethod = random.nextDouble();
//                if (probCallMethod < PROB_METHOD_ASSIGN) {
//                    //Create method
//                    Method m = generateMethod(method, returnTypes);
//                    CallExpression expression = new CallExpression(m);
//                    List<Type> argTypes = m.getArgTypes();
//                    int i = 0;
//                    while (i < argTypes.size()) {
//                        Type t = argTypes.get(i);
//                        try {
//                            expression.addArg(generateExpression(t, symbolTable));
//                            i++;
//                        } catch (InvalidArgumentException e) {
//                            System.err.println("Could not generate argument");
//                        }
//                    }
//                    statement.addAssignment(expression);
//                } else {
                //Explicit values
                for (Type t : returnTypes) {
                    expressionDepth++;
                    Expression expression = generateExpression(t, symbolTable);
                    expressionDepth--;
                    statement.addAssignment(expression);
//                    }
                }

                statement.addAssignmentsToSymbolTable();

                return statement;

            } else if (probTypeOfStatement < PROB_IF_ELSE_STAT) {
                //IfElse
                IfElseStatement statement = new IfElseStatement(symbolTable);

                expressionDepth++;
                Expression test = generateExpression(new Bool(), symbolTable);
                statement.setTest(test);
                expressionDepth--;


                statementDepth++;
                Statement ifStat = generateBody(method);
                statement.setIfStat(ifStat);
                statementDepth--;

                if (random.nextDouble() < PROB_IF_ELSE_STAT) {
                    statementDepth++;
                    Statement elseStat = generateBody(method);
                    statement.setElseStat(elseStat);
                    statementDepth--;
                }

                return statement;

            } else if (probTypeOfStatement < PROB_PRINT_STAT) {
                //Print

                PrintStatement statement = new PrintStatement(symbolTable);
                int noOfValues = random.nextInt(5) + 1;
                List<Type> types = generateTypes(noOfValues);

                for (Type type : types) {
                    expressionDepth++;
                    Expression expression = generateExpression(type, symbolTable);
                    statement.addValue(expression);
                    expressionDepth--;
                }

                return statement;

            } else if (probTypeOfStatement < PROB_RETURN_STAT && method.hasReturn()) {
                //return
                ReturnStatement statement = new ReturnStatement(symbolTable);
                List<Type> types = method.getReturnTypes();
                for (Type type : types) {
                    expressionDepth++;
                    Expression expression = generateExpression(type, symbolTable);
                    statement.addValue(expression);
                    expressionDepth--;
                }

                return statement;
            }
        }


    }

    private Method generateMethod(Method method, List<Type> returnTypes) {
        Method m = new Method(returnTypes, VariableNameGenerator.generateMethodName());
        method.addMethod(m);

        StatementGenerator mStatGen = new StatementGenerator(random);

        int noOfArgs = random.nextInt(5) + 1;
        List<Type> args = generateTypes(noOfArgs);
        for (Type t : args) {
            m.addArgument(VariableNameGenerator.generateArgumentName(m), t);
        }

        Statement statement = mStatGen.generateBody(m);
        m.setBody(statement);
        return m;
    }

    private List<Type> generateTypes(int noOfTypes) {
        List<Type> types = new ArrayList<>();

        for (int i = 0; i < noOfTypes; i++) {
            double probType = random.nextDouble();
            if (probType < 0.33) {
                types.add(new Int());
            } else if (probType < 0.66) {
                types.add(new Bool());
            } else if (probType < PROB_RETURN_STAT) {
                types.add(new Char());
            }
        }

        return types;
    }

    private Expression generateExpression(Type type, SymbolTable symbolTable) {
        while (true) {
            double probTypeOfExpression = random.nextDouble();
            if (expressionDepth > 10 || probTypeOfExpression < PROB_LITERAL_EXPRESSION) {
                Expression expression = type.generateLiteral(random);
                expression.setSymbolTable(symbolTable);
                return expression;
            } else if (probTypeOfExpression < PROB_CALL_EXPRESSION) {
                //Call

            } else if (probTypeOfExpression < PROB_IF_ELSE_EXPRESSION) {
                //ifElse
                expressionDepth++;
                Expression test = generateExpression(new Bool(), symbolTable);
                expressionDepth--;

                expressionDepth++;
                Expression ifExp = generateExpression(type, symbolTable);
                expressionDepth--;

                expressionDepth++;
                Expression elseExp = generateExpression(type, symbolTable);
                expressionDepth--;

                IfElseExpression expression = new IfElseExpression(test, ifExp, elseExp);
                expression.setSymbolTable(symbolTable);
                return expression;

            } else if (probTypeOfExpression < PROB_OPERATOR_EXPRESSION && type.operatorExists()) {
                //Operator
                Operator operator = generateOperator(type);
                List<Type> typeArgs = operator.getTypeArgs();
                int randType = random.nextInt(typeArgs.size());
                Type t = typeArgs.get(randType);

                expressionDepth++;
                Expression lhs = generateExpression(t, symbolTable);
                expressionDepth--;

                expressionDepth++;
                Expression rhs = generateExpression(t, symbolTable);
                expressionDepth--;

                OperatorExpression expression = new OperatorExpression(operator, lhs, rhs);
                expression.setType(type);
                expression.setSymbolTable(symbolTable);
                return expression;
            } else if (probTypeOfExpression < PROB_VARIABLE_EXPRESSION) {
                List<Variable> variables = symbolTable.getAllVariables(type);
                if (!variables.isEmpty()) {
                    int index = random.nextInt(variables.size());
                    VariableExpression expression = new VariableExpression(variables.get(index));
                    expression.setSymbolTable(symbolTable);
                    return expression;
                }
            }

        }
    }

    private Operator generateOperator(Type type) {
        if (type.isSameType(new Bool())) {
            BoolOperator[] values = BoolOperator.values();
            int randOp = random.nextInt(values.length);
            return values[randOp];
        } else if (type.isSameType(new Int())) {
            NumericOperator[] values = NumericOperator.values();
            int randOp = random.nextInt(values.length);
            return values[randOp];
        } else if (type.isSameType(new Char())) {

        }
        return null;
    }

}
