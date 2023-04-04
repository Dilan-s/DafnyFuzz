package AST.Generator;

import AST.Statements.Expressions.CallExpression;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.IfElseExpression;
import AST.Statements.Expressions.IntLiteral;
import AST.Statements.Expressions.Operator.BinaryOperator;
import AST.Statements.Expressions.Operator.Operator;
import AST.Statements.Expressions.Operator.UnaryOperator;
import AST.Statements.Expressions.OperatorExpression;
import AST.Statements.Expressions.ReassignSeqExpression;
import AST.Statements.Expressions.IndexExpression;
import AST.Statements.Expressions.SubsequenceExpression;
import AST.Statements.Expressions.VariableExpression;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.Types.DCollectionTypes.Seq;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RandomExpressionGenerator {

    public static final double PROB_LITERAL_EXPRESSION = 0.2;
    public static final double PROB_OPERATOR_EXPRESSION = PROB_LITERAL_EXPRESSION + 0.3;
    public static final double PROB_VARIABLE_EXPRESSION = PROB_OPERATOR_EXPRESSION + 0.3;
    public static final double PROB_SEQ_INDEX_EXPRESSION = PROB_VARIABLE_EXPRESSION + 0.03;
    public static final double PROB_SUBSEQUENCE_EXPRESSION = PROB_SEQ_INDEX_EXPRESSION + 0.03;
    public static final double PROB_REASSIGN_SEQ_EXPRESSION = PROB_SUBSEQUENCE_EXPRESSION + 0.03;
    public static final double PROB_IF_ELSE_EXPRESSION = PROB_REASSIGN_SEQ_EXPRESSION + 0.05;
    public static final double PROB_CALL_EXPRESSION = PROB_IF_ELSE_EXPRESSION + 0.05;

    public static final double PROB_HI_AND_LO_SUBSEQUENCE = 0.7;
    public static final int MAX_EXPRESSION_DEPTH = 5;

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
            } else if (probTypeOfExpression < PROB_SUBSEQUENCE_EXPRESSION && type.equals(new Seq())) {
                ret = generateSubsequenceExpression(type, symbolTable);
            } else if (probTypeOfExpression < PROB_REASSIGN_SEQ_EXPRESSION && type.equals(new Seq())) {
                ret = generateReassignSeqExpression(type, symbolTable);
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

    private Expression generateReassignSeqExpression(Type type, SymbolTable symbolTable) {
        Seq seqT = (Seq) type.concrete(symbolTable);
        Expression seq = generateExpression(seqT, symbolTable);
        Object s = seqT.getValue();

        Int indT = new Int();
        Expression ind = generateLiteral(indT, symbolTable);
        Integer i = (Integer) indT.getValue();

        Type expT = seqT.getInnerType().concrete(symbolTable);
        Expression exp = generateExpression(expT, symbolTable);

        ReassignSeqExpression expression = new ReassignSeqExpression(symbolTable, seq, ind, exp);
        VariableExpression seqVar = expression.getSequenceVariableExpression();

        OperatorExpression size = new OperatorExpression(symbolTable, new Int(), UnaryOperator.Cardinality, List.of(seqVar));

        IntLiteral zero = new IntLiteral(new Int(), symbolTable, 0);
        OperatorExpression test = new OperatorExpression(symbolTable, new Bool(), BinaryOperator.Greater_Than, List.of(size, zero));

        IfElseExpression ifElseExpression = new IfElseExpression(symbolTable, type, test, expression, seqVar);

        if (i != null && expT.getValue() != null && seqT.getValue() != null) {
            int iIndex = i < seqT.getSize() && 0 <= i ? i : 0;
            Expression expLiteral = expT.generateLiteral(symbolTable, exp, expT.getValue());
            type.setValue(s == null ? null : seqT.getSize() > 0 ? seqT.reassignIndex(iIndex, expLiteral) : seqT.getValue());
        }
        return ifElseExpression;
    }

    private Expression generateSubsequenceExpression(Type type, SymbolTable symbolTable) {
        Seq seqT = (Seq) type.concrete(symbolTable);
        Expression seq = generateExpression(seqT, symbolTable);
        Object s = seqT.getValue();

        Int indIT = new Int();
        Expression indI = generateExpression(indIT, symbolTable);
        Integer i = (Integer) indIT.getValue();

        Expression indJ;
        Int indJT = new Int();
        if (GeneratorConfig.getRandom().nextDouble() < PROB_HI_AND_LO_SUBSEQUENCE) {
            indJ = generateExpression(indJT, symbolTable);
        } else {
            indJ = new IntLiteral(indJT, symbolTable, 0);
        }
        Integer j = (Integer) indJT.getValue();
        SubsequenceExpression expression = new SubsequenceExpression(symbolTable, seq, indI, indJ);
        VariableExpression seqVar = expression.getSequenceVariableExpression();

        OperatorExpression size = new OperatorExpression(symbolTable, new Int(), UnaryOperator.Cardinality, List.of(seqVar));

        IntLiteral zero = new IntLiteral(new Int(), symbolTable, 0);
        OperatorExpression test = new OperatorExpression(symbolTable, new Bool(), BinaryOperator.Greater_Than, List.of(size, zero));

        IfElseExpression ifElseExpression = new IfElseExpression(symbolTable, type, test, expression, seqVar);

        if (i != null && j != null && seqT.getValue() != null) {
            int iIndex = i < seqT.getSize() && 0 <= i ? i : 0;
            int jIndex = j < seqT.getSize() && 0 <= j ? j : 0;
            type.setValue(s == null ? null : seqT.getSize() > 0 ? seqT.subsequence(iIndex, jIndex) : seqT.getValue());
        }
        return ifElseExpression;
    }


    private Expression generateSeqIndexExpression(Type type, SymbolTable symbolTable) {
        Seq seqT = new Seq(type.concrete(symbolTable));
        Expression seq = seqT.generateLiteral(symbolTable);
        Object s = seqT.getValue();

        Int indT = new Int();
        Expression ind = generateExpression(indT, symbolTable);
        Integer i = (Integer) indT.getValue();

        IndexExpression expression = new IndexExpression(symbolTable, type, seq, ind);

        VariableExpression seqVar = expression.getSequenceVariableExpression();

        OperatorExpression size = new OperatorExpression(symbolTable, new Int(), UnaryOperator.Cardinality, List.of(seqVar));

        IntLiteral zero = new IntLiteral(new Int(), symbolTable, 0);
        OperatorExpression test = new OperatorExpression(symbolTable, new Bool(), BinaryOperator.Greater_Than, List.of(size, zero));

        Type defT = type.concrete(symbolTable);
        Expression def = generateExpression(defT, symbolTable);
        IfElseExpression ifElseExpression = new IfElseExpression(symbolTable, type, test, expression, def);

        if (i != null) {
            int iIndex = i < seqT.getSize() && 0 <= i ? i : 0;
            type.setValue(s == null ? null : seqT.getSize() > 0 ? seqT.get(iIndex) : defT.getValue());
        }
        return ifElseExpression;
    }

    private VariableExpression generateVariableExpression(Type type, SymbolTable symbolTable) {
        List<Variable> variables = symbolTable.getAllVariables(type);

        if (!variables.isEmpty()) {
            int index = GeneratorConfig.getRandom().nextInt(variables.size());
            Variable variable = variables.get(index);
            type.setValue(variable.getType().getValue());
            VariableExpression expression = new VariableExpression(symbolTable, variable, type);
            return expression;
        }
        return null;
    }

    private OperatorExpression generateOperatorExpression(Type type, SymbolTable symbolTable) {
        Operator operator = generateOperator(type);
        if (operator == null) {
            return null;
        }

        List<List<Type>> typeArgs = operator.getTypeArgs();
        int randType = GeneratorConfig.getRandom().nextInt(typeArgs.size());
        List<Type> types = typeArgs.get(randType);

        types = operator.concreteType(types, symbolTable, type);

        List<Expression> args = new ArrayList<>();
        for (Type t : types) {
            Expression arg = generateExpression(t, symbolTable);
            args.add(arg);
        }

        OperatorExpression expression = new OperatorExpression(symbolTable, type, operator, args);

        return expression;
    }

    private IfElseExpression generateIfElseExpression(Type type, SymbolTable symbolTable) {
        Bool testT = new Bool();
        Expression test = generateExpression(testT, symbolTable);

        Type ifT = type.concrete(symbolTable);
        Expression ifExp = generateExpression(ifT, symbolTable);

        Type elseT = type.concrete(symbolTable);
        Expression elseExp = generateExpression(elseT, symbolTable);
        IfElseExpression expression = new IfElseExpression(symbolTable, type, test, ifExp, elseExp);

        if (testT.getValue() != null) {
            boolean testValue = (boolean) testT.getValue();
            if (testValue) {
                type.setValue(ifT.getValue());
            } else {
                type.setValue(elseT.getValue());
            }
        }
        return expression;
    }

    private Expression generateLiteral(Type type, SymbolTable symbolTable) {
        Expression expression = type.generateLiteral(symbolTable);
        return expression;
    }

    private Operator generateOperator(Type type) {
        List<Operator> ops = Arrays.stream(BinaryOperator.values()).collect(Collectors.toList());
        ops.addAll(Arrays.stream(UnaryOperator.values()).collect(Collectors.toList()));

        List<Operator> validOperators = ops.stream()
            .filter(x -> x.returnType(type))
            .collect(Collectors.toList());

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

        List<Type> argTypes = m.getArgTypes();
        int i = 0;
        List<Expression> args = new ArrayList<>();
        while (i < argTypes.size()) {
            Type t = argTypes.get(i);
            Type concrete = t.concrete(symbolTable);
            Expression exp = generateExpression(concrete, symbolTable);
            args.add(exp);
            i++;
        }
        CallExpression expression = new CallExpression(symbolTable, m, args);


        return expression;
    }


}
