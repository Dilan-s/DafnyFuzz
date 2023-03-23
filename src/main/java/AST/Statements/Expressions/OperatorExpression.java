package AST.Statements.Expressions;

import AST.Errors.InvalidArgumentException;
import AST.Errors.SemanticException;
import AST.Statements.Expressions.Operator.BinaryOperator;
import AST.Statements.Expressions.Operator.Operator;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OperatorExpression implements Expression {

    private final Operator operator;
    private final List<Expression> args;

    private Type type;
    private boolean convertToCall;

    private Optional<Expression> replacementExpression;
    private SymbolTable symbolTable;

    public OperatorExpression(SymbolTable symbolTable, Operator operator) {
        this.symbolTable = symbolTable;
        this.replacementExpression = Optional.empty();
        this.operator = operator;
        this.convertToCall = true;
        this.type = null;
        this.args = new ArrayList<>();
    }

    public OperatorExpression(SymbolTable symbolTable, Operator operator, boolean convertToCall) {
        this(symbolTable, operator);
        this.convertToCall = convertToCall;
    }

    public void addArgument(Expression expression) {
        args.add(expression);
    }

    public void setType(Type type) {
        this.type = type;
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
    public List<String> toCode() {
        List<String> code = new ArrayList<>();

        if (convertToCall && operator.equals(BinaryOperator.Divide)) {
            CallExpression safe_division = new CallExpression(symbolTable, symbolTable.getMethod("safe_division"));
            try {
                safe_division.addArg(args);
            } catch (InvalidArgumentException e) {
                System.err.println("ADDED DODGY LHS");
            }
            replacementExpression = Optional.of(safe_division);
            return safe_division.toCode();
        } else if (convertToCall && operator.equals(BinaryOperator.Modulus)) {
            CallExpression safe_modulus = new CallExpression(symbolTable, symbolTable.getMethod("safe_modulus"));
            try {
                safe_modulus.addArg(args);
            } catch (InvalidArgumentException e) {
                System.err.println("ADDED DODGY LHS");
            }
            replacementExpression = Optional.of(safe_modulus);
            return safe_modulus.toCode();
        }

        code.addAll(args.stream()
            .map(Expression::toCode)
            .flatMap(Collection::stream)
            .collect(Collectors.toList()));

        return code;
    }
}
