package AST.Generator;

import AST.Statements.AssertStatement;
import AST.Statements.AssignmentStatement;
import AST.Statements.BlockStatement;
import AST.Statements.BreakStatement;
import AST.Statements.ContinueStatement;
import AST.Expressions.CallMethodExpression;
import AST.Expressions.Expression;
import AST.Expressions.IntLiteral;
import AST.Expressions.Operator.BinaryOperator;
import AST.Expressions.Operator.OperatorExpression;
import AST.Expressions.VariableExpression;
import AST.Statements.ForStatement;
import AST.Statements.IfElseStatement;
import AST.Statements.MatchStatement;
import AST.Statements.ReturnStatement;
import AST.Statements.Statement;
import AST.Statements.WhileStatement;
import AST.Statements.util.MatchStatementCase;
import AST.Statements.util.PrintAll;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.Types.PrimitiveTypes.Void;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RandomStatementGenerator {

    private static final int MAX_ASSERT_VALUES = 5;
    private static final int MAX_MATCH_VALUES = 2;
    public static double PROB_RETURN_STAT = 60.0;
    public static double PROB_ASSIGN_STAT = 30.0;
    public static double PROB_IF_ELSE_STAT = 15.0;
    public static double PROB_MATCH_STAT = 5.0;
    public static double PROB_WHILE_STAT = 7.0;
    public static double PROB_FOR_STAT = 7.0;
    public static double PROB_ASSERT = 12.5;
    public static double PROB_BREAK_STAT = 5.0;
    public static double PROB_CONTINUE_STAT = 5.0;

    public static final double PROB_METHOD_ASSIGN = 0.1;
    public static final double PROB_ELSE_STAT = 0.5;

    public static final int MAX_STATEMENT_DEPTH = 4;
    public static final double PROB_NEXT_STAT = 0.85;
    public static final double PROB_FORCE_RETURN = 0.25;
    public static final double PROB_REASSIGN = 1.0;

    public static int loopDepth = 0;
    private static int statementDepth = 0;


    public BlockStatement generateBody(Method method, SymbolTable symbolTable) {
        return generateBody(method, symbolTable, method.hasReturn());
    }

    public BlockStatement generateBody(Method method, SymbolTable symbolTable, boolean requireReturn) {
        BlockStatement body = new BlockStatement(symbolTable);

        double probContinue = GeneratorConfig.getRandom().nextDouble();
        Statement statement = null;
        while (probContinue < PROB_NEXT_STAT || requireReturn) {
            statement = generateStatement(method, symbolTable, requireReturn);
            if (statementDepth == 0) {
                List<Statement> expand = statement.expand();
                for (Statement st : expand) {
                    st.execute(new StringBuilder());
                }
            }

            body.addStatement(statement);
            if (statement.isReturn()) {
                break;
            }
            probContinue = GeneratorConfig.getRandom().nextDouble();
        }

        if (statement != null && !statement.isReturn()) {
            PrintAll printAll = new PrintAll(body.getSymbolTable());
            List<Statement> expand = printAll.expand();
            if (statementDepth == 0) {
                for (Statement st : expand) {
                    st.execute(new StringBuilder());
                }
            }
            body.addStatement(expand);
        }

        return body;
    }

    private Statement generateStatement(Method method, SymbolTable symbolTable, boolean requireReturn) {
        statementDepth++;
        Statement ret = null;
        while (ret == null) {
            double ratioSum = PROB_RETURN_STAT + PROB_ASSIGN_STAT + PROB_IF_ELSE_STAT +
                PROB_ASSERT + PROB_WHILE_STAT + PROB_FOR_STAT + PROB_BREAK_STAT +
                PROB_MATCH_STAT +
                PROB_CONTINUE_STAT;
            double probTypeOfStatement = GeneratorConfig.getRandom().nextDouble() * ratioSum;

            if ((statementDepth > MAX_STATEMENT_DEPTH ||
                (probTypeOfStatement -= PROB_RETURN_STAT) < 0)) {
                //return
                double probReturnWhenNoReturnRequired = GeneratorConfig.getRandom().nextDouble();
                if (statementDepth > MAX_STATEMENT_DEPTH || requireReturn || probReturnWhenNoReturnRequired < PROB_FORCE_RETURN) {
                    ret = generateReturnStatement(method, symbolTable);
                }

            } else if ((probTypeOfStatement -= PROB_ASSIGN_STAT) < 0) {
                //Assign
                PROB_ASSIGN_STAT *= GeneratorConfig.OPTION_DECAY_FACTOR;
                ret = generateAssignmentStatement(symbolTable);

            } else if ((probTypeOfStatement -= PROB_IF_ELSE_STAT) < 0) {
                //IfElse
                PROB_IF_ELSE_STAT *= GeneratorConfig.OPTION_DECAY_FACTOR;
                ret = generateIfElseStatement(method, symbolTable);

            } else if ((probTypeOfStatement -= PROB_ASSERT) < 0) {
                //Assert
                PROB_ASSERT *= GeneratorConfig.OPTION_DECAY_FACTOR;
                ret = generateAssertStatement(method, symbolTable);

            }
            else if ((probTypeOfStatement -= PROB_WHILE_STAT) < 0) {
                //Match
                PROB_WHILE_STAT *= GeneratorConfig.OPTION_DECAY_FACTOR;
                ret = generateWhileStatement(method, symbolTable);

            } else if ((probTypeOfStatement -= PROB_FOR_STAT) < 0) {
                //Match
                PROB_FOR_STAT *= GeneratorConfig.OPTION_DECAY_FACTOR;
                ret = generateForStatement(method, symbolTable);

            } else if ((probTypeOfStatement -= PROB_BREAK_STAT) < 0) {
                if (loopDepth > 0) {
                    PROB_BREAK_STAT *= GeneratorConfig.OPTION_DECAY_FACTOR;
                    ret = generateBreakStatement(method, symbolTable);
                }
            } else if ((probTypeOfStatement -= PROB_CONTINUE_STAT) < 0) {
                if (loopDepth > 0) {
                    PROB_CONTINUE_STAT *= GeneratorConfig.OPTION_DECAY_FACTOR;
                    ret = generateContinueStatement(method, symbolTable);
                }
            }
            else if ((probTypeOfStatement -= PROB_MATCH_STAT) < 0) {
                //Match
                PROB_MATCH_STAT *= GeneratorConfig.OPTION_DECAY_FACTOR;
                ret = generateMatchStatement(method, symbolTable);

            }
        }
        statementDepth--;
        return ret;
    }

    private Statement generateContinueStatement(Method method, SymbolTable symbolTable) {
        ContinueStatement continueStatement = new ContinueStatement(symbolTable);
        return continueStatement;
    }

    private Statement generateBreakStatement(Method method, SymbolTable symbolTable) {
        BreakStatement breakStatement = new BreakStatement(symbolTable);
        return breakStatement;
    }

    private Statement generateForStatement(Method method, SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();
        Int intType = new Int();

        SymbolTable forSt = new SymbolTable(symbolTable);

        Variable loopVar = new Variable(VariableNameGenerator.generateVariableValueName(intType, symbolTable), intType);
        loopVar.setConstant();

        Expression initExp = expressionGenerator.generateExpression(intType, symbolTable);
        Expression finalExp = expressionGenerator.generateExpression(intType, symbolTable);

        forSt.addVariable(loopVar);

        loopDepth++;
        BlockStatement body = generateBody(method, forSt, false);
        loopDepth--;

        ForStatement forStatement = new ForStatement(symbolTable, initExp, finalExp, loopVar, body);
        return forStatement;
    }

    private Statement generateWhileStatement(Method method, SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();
        Int intType = new Int();

        Variable loopVar = new Variable(VariableNameGenerator.generateVariableValueName(intType, symbolTable), intType);
        loopVar.setConstant();
        VariableExpression loopVarExp = new VariableExpression(symbolTable, loopVar, intType);

        Variable finalVar = new Variable(VariableNameGenerator.generateVariableValueName(intType, symbolTable), intType);
        finalVar.setConstant();
        VariableExpression finalVarExp = new VariableExpression(symbolTable, finalVar, intType);

        Expression initExp = expressionGenerator.generateExpression(intType, symbolTable);
        AssignmentStatement initAssign = new AssignmentStatement(symbolTable, List.of(loopVar), initExp);

        Expression finalExp = expressionGenerator.generateExpression(intType, symbolTable);
        AssignmentStatement finalAssign = new AssignmentStatement(symbolTable, List.of(finalVar), finalExp);

        Bool boolType = new Bool();

        OperatorExpression test = new OperatorExpression(symbolTable, boolType, BinaryOperator.Less_Than, List.of(loopVarExp, finalVarExp));

        SymbolTable bodySt = new SymbolTable(symbolTable);

        loopDepth++;
        BlockStatement body = generateBody(method, bodySt, false);
        loopDepth--;


        OperatorExpression incr = new OperatorExpression(bodySt, intType, BinaryOperator.Plus, List.of(loopVarExp, new IntLiteral(intType, symbolTable, 1)));
        AssignmentStatement incrAssign = new AssignmentStatement(bodySt, List.of(loopVar), incr);
        body.addStatementFirst(incrAssign);

        WhileStatement whileStatement = new WhileStatement(symbolTable, loopVar, finalVar, initAssign, finalAssign, test, body);

        return whileStatement;
    }

    private Statement generateMatchStatement(Method method, SymbolTable symbolTable) {
        int noOfCases = GeneratorConfig.getRandom().nextInt(MAX_MATCH_VALUES) + 1;

        RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
        Type type = typeGenerator.generateMatchType(symbolTable).concrete(symbolTable);

        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();
        Expression testExpression = expressionGenerator.generateExpression(type, symbolTable);

        List<MatchStatementCase> cases = new ArrayList<>();
        for (int i = 0; i < noOfCases; i++) {
            SymbolTable caseSymbolTable = new SymbolTable(symbolTable);

            Expression exp = expressionGenerator.generateLiteral(type, caseSymbolTable);

            Statement b = generateBody(method, caseSymbolTable, false);

            MatchStatementCase c = new MatchStatementCase(caseSymbolTable, exp, b);
            cases.add(c);
        }

        SymbolTable defCaseSymbolTable = new SymbolTable(symbolTable);
        Statement defCaseBody = generateBody(method, defCaseSymbolTable, false);
        MatchStatementCase defCase = new MatchStatementCase(defCaseSymbolTable, defCaseBody);
        MatchStatement matchStatement = new MatchStatement(symbolTable, testExpression, cases, defCase);
        return matchStatement;
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

        Statement ifStat = generateBody(method, new SymbolTable(symbolTable), false);

        if (GeneratorConfig.getRandom().nextDouble() < PROB_ELSE_STAT) {
            Statement elseStat = generateBody(method, new SymbolTable(symbolTable), false);
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

        double probCallMethod = GeneratorConfig.getRandom().nextDouble() * Math.pow(GeneratorConfig.OPTION_DECAY_FACTOR, statementDepth);
        if (probCallMethod < PROB_METHOD_ASSIGN) {
            List<Type> returnTypes = typeGenerator.generateMethodTypes(noOfReturns, symbolTable);
            //Create method

            CallMethodExpression expression = expressionGenerator.generateCallMethodExpression(symbolTable, returnTypes);
            if (expression != null) {
                List<Variable> variables = generateAssignVariables(returnTypes, symbolTable);

                AssignmentStatement statement = new AssignmentStatement(symbolTable, variables, expression);
                return statement;
            }
        }
        List<Type> returnTypes = typeGenerator.generateTypes(noOfReturns, symbolTable);

        List<Type> concreteRetTypes = returnTypes.stream()
            .map(t -> t.concrete(symbolTable))
            .collect(Collectors.toList());

        List<Expression> value = concreteRetTypes.stream()
            .map(concrete -> expressionGenerator.generateExpression(concrete, symbolTable))
            .collect(Collectors.toList());

        List<Variable> variables = generateAssignVariables(concreteRetTypes, symbolTable);

        AssignmentStatement statement = new AssignmentStatement(symbolTable, variables, value);
        return statement;
    }

    private List<Variable> generateAssignVariables(List<Type> types, SymbolTable symbolTable) {
        if (types.stream().allMatch(Type::validMethodType)) {
            List<Variable> variables = new ArrayList<>();
            for (Type t : types) {
                List<Variable> varsInScope = symbolTable.getAllVariables(t).stream()
                    .filter(v -> !v.isConstant())
                    .collect(Collectors.toList());
                Collections.shuffle(varsInScope, GeneratorConfig.getRandom());

                boolean assigned = false;

                for (int i = 0; !assigned && i < varsInScope.size(); i++) {
                    Variable vToAssign = varsInScope.get(i);
                    List<Variable> vs = vToAssign.getRelatedAssignment();
                    if (vs.stream().noneMatch(vAttempt -> variables.stream()
                        .anyMatch(p -> p.getRelatedAssignment().contains(vAttempt)))) {

                        variables.add(vToAssign);
                        assigned = true;
                    }
                }
                if (!assigned) {
                    return generateNewAssignVariables(types, symbolTable);
                }
            }
            return variables;
        }
        return generateNewAssignVariables(types, symbolTable);
    }

    private List<Variable> generateNewAssignVariables(List<Type> types, SymbolTable symbolTable) {
        List<Variable> variables = types.stream()
            .map(t -> new Variable(VariableNameGenerator.generateVariableValueName(t, symbolTable), t))
            .collect(Collectors.toList());
        return variables;
    }



}
