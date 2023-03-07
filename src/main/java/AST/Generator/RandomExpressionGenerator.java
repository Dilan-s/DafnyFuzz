package AST.Generator;

import AST.Errors.InvalidArgumentException;
import AST.Statements.Expressions.CallExpression;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.IfElseExpression;
import AST.Statements.Expressions.IntLiteral;
import AST.Statements.Expressions.Operator.BinaryOperator;
import AST.Statements.Expressions.Operator.Operator;
import AST.Statements.Expressions.Operator.UnaryOperator;
import AST.Statements.Expressions.OperatorExpression;
import AST.Statements.Expressions.SeqIndexExpression;
import AST.Statements.Expressions.SubsequenceExpression;
import AST.Statements.Expressions.VariableExpression;
import AST.SymbolTable.Method;
import AST.SymbolTable.PrimitiveTypes.Bool;
import AST.SymbolTable.PrimitiveTypes.Seq;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RandomExpressionGenerator {

    public static final double PROB_LITERAL_EXPRESSION = 0.3;
    public static final double PROB_OPERATOR_EXPRESSION = PROB_LITERAL_EXPRESSION + 0.4;
    public static final double PROB_VARIABLE_EXPRESSION = PROB_OPERATOR_EXPRESSION + 0.15;
    public static final double PROB_SEQ_INDEX_EXPRESSION = PROB_VARIABLE_EXPRESSION + 0.025;
    public static final double PROB_SUBSEQUENCE_EXPRESSION = PROB_SEQ_INDEX_EXPRESSION + 0.025;
    public static final double PROB_IF_ELSE_EXPRESSION = PROB_SUBSEQUENCE_EXPRESSION + 0.05;
    public static final double PROB_CALL_EXPRESSION = PROB_IF_ELSE_EXPRESSION + 0.05;

    public static final double PROB_HI_AND_LO_SUBSEQUENCE = 0.5;
    public static final int MAX_EXPRESSION_DEPTH = 7;

    private static int expressionDepth = 0;


    public Expression generateExpression(Type type, SymbolTable symbolTable) {
        Expression ret = null;
        expressionDepth++;
        while (ret == null) {
            double probTypeOfExpression = GeneratorConfig.getRandom().nextDouble() * Math.pow(GeneratorConfig.DECAY_FACTOR, expressionDepth - 1);
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
            } else if (probTypeOfExpression < PROB_SEQ_INDEX_EXPRESSION && !type.isCollection()) {
                ret = generateSeqIndexExpression(type, symbolTable);
            } else if (probTypeOfExpression < PROB_SUBSEQUENCE_EXPRESSION && type.isSameType(new Seq())) {
                ret = generateSubsequenceExpression(type, symbolTable);
            } else if (probTypeOfExpression < PROB_IF_ELSE_EXPRESSION) {
                //ifElse
                ret = generateIfElseExpression(type, symbolTable);

            } else if (probTypeOfExpression < PROB_CALL_EXPRESSION) {
                //call
                ret = generateCallExpression(symbolTable, List.of(type));
            }
        }
        expressionDepth--;
        return ret;
    }

    private Expression generateSubsequenceExpression(Type type, SymbolTable symbolTable) {
        Seq t = (Seq) type;

        Expression seq = t.generateLiteral(symbolTable);
        seq.setSymbolTable(symbolTable);

        IntLiteral i = new IntLiteral(GeneratorConfig.getRandom().nextInt(t.getLength()));

        SubsequenceExpression expression = new SubsequenceExpression(seq);
        expression.setSymbolTable(symbolTable);
        if (GeneratorConfig.getRandom().nextDouble() < PROB_HI_AND_LO_SUBSEQUENCE) {
            IntLiteral j = new IntLiteral(GeneratorConfig.getRandom().nextInt(t.getLength()));
            expression.addIndexes(i, j);
        } else {
            expression.addIndexes(i);
        }

        return expression;
    }


    private SeqIndexExpression generateSeqIndexExpression(Type type, SymbolTable symbolTable) {
        Seq t = new Seq(type);
        Expression seq = t.generateLiteral(symbolTable);
        seq.setSymbolTable(symbolTable);

        IntLiteral ind = new IntLiteral(GeneratorConfig.getRandom().nextInt(t.getLength()));
        ind.setSymbolTable(symbolTable);

        SeqIndexExpression expression = new SeqIndexExpression();
        expression.setSymbolTable(symbolTable);
        expression.setSeqAndInd(seq, ind);
        return expression;
    }

    private VariableExpression generateVariableExpression(Type type, SymbolTable symbolTable) {
        List<Variable> variables = symbolTable.getAllVariables(type);
        if (!variables.isEmpty()) {
            int index = GeneratorConfig.getRandom().nextInt(variables.size());
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
        int randType = GeneratorConfig.getRandom().nextInt(typeArgs.size());
        List<Type> types = typeArgs.get(randType);

        types = operator.concreteType(types, symbolTable, type);

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
        Expression expression = type.generateLiteral(symbolTable);
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
            int randOp = GeneratorConfig.getRandom().nextInt(validOperators.size());
            return validOperators.get(randOp);
        }
        return null;
    }

    public CallExpression generateCallExpression(SymbolTable symbolTable, List<Type> returnTypes) {
        RandomMethodGenerator methodGenerator = new RandomMethodGenerator();
        Method m = methodGenerator.generateMethod(returnTypes, symbolTable);

        if (m == null) {
            return null;
        }

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


}
