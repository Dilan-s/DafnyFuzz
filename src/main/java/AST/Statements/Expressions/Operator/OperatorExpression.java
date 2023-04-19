package AST.Statements.Expressions.Operator;

import AST.Errors.SemanticException;
import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.CallExpression;
import AST.Statements.Expressions.Expression;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class OperatorExpression implements Expression {

    private final Operator operator;
    private final List<Expression> args;
    private Optional<Expression> replacementExpression;

    private Type type;
    private boolean convertToCall;
    private SymbolTable symbolTable;

    public OperatorExpression(SymbolTable symbolTable, Type type, Operator operator, List<Expression> args, boolean convertToCall) {
        this.symbolTable = symbolTable;
        this.replacementExpression = Optional.empty();
        this.operator = operator;
        this.convertToCall = convertToCall;
        this.type = type;
        this.args = args;
        generateMethodCallReplacement();
    }

    public OperatorExpression(SymbolTable symbolTable, Type type, Operator operator, List<Expression> args) {
        this(symbolTable, type, operator, args, true);
    }

    @Override
    public List<Type> getTypes() {
        if (type == null) {
            return operator.getType();
        }
        return List.of(type);
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {
        operator.semanticCheck(method, args);
    }

    @Override
    public String toString() {
        if (replacementExpression.isPresent()) {
            return replacementExpression.get().toString();
        }
        return operator.formExpression(args);
    }

    @Override
    public List<String> toOutput() {
        if (replacementExpression.isPresent()) {
            return replacementExpression.get().toOutput();
        }
        List<String> res = operator.formOutput(args);
        List<String> r = new ArrayList<>(res);
        Collections.shuffle(r, GeneratorConfig.getRandom());
        return r.subList(0, Math.min(5, res.size()));
    }

    @Override
    public List<Statement> expand() {
        if (replacementExpression.isPresent()) {
            return replacementExpression.get().expand();
        }
        List<Statement> list = new ArrayList<>();
        for (Expression arg : args) {
            List<Statement> expand = arg.expand();
            for (Statement statement : expand) {
                list.add(statement);
            }
        }
        return list;
    }

    private void generateMethodCallReplacement() {
        if (convertToCall && operator.equals(BinaryOperator.Divide)) {
            CallExpression safe_division = new CallExpression(symbolTable, symbolTable.getMethod("safe_division"), args);
            replacementExpression = Optional.of(safe_division);
        } else if (convertToCall && operator.equals(BinaryOperator.Modulus)) {
            CallExpression safe_modulus = new CallExpression(symbolTable, symbolTable.getMethod("safe_modulus"), args);
            replacementExpression = Optional.of(safe_modulus);
        }
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s) {
        List<Object> r = new ArrayList<>();

        if (replacementExpression.isPresent()) {
            return replacementExpression.get().getValue(paramsMap, s);
        }

        for (Expression e : args) {
            List<Object> value = e.getValue(paramsMap, s);
            for (Object v : value) {
                if (v == null) {
                    r.add(null);
                    return r;
                }
            }
        }

        r.add(operator.apply(args, paramsMap));
        return r;
    }
}
