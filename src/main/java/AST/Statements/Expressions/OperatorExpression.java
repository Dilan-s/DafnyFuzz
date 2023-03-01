package AST.Statements.Expressions;

import AST.Errors.InvalidArgumentException;
import AST.Errors.SemanticException;
import AST.Statements.Expressions.Operator.NumericOperator;
import AST.Statements.Expressions.Operator.Operator;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OperatorExpression implements Expression {

    private final Operator operator;
    private final Expression lhs;
    private final Expression rhs;
    private Type type;
    private boolean convertToCall;

    private Optional<Expression> replacementExpression;
    private SymbolTable symbolTable;

    public OperatorExpression(Operator operator, Expression lhs, Expression rhs) {
        this.replacementExpression = Optional.empty();
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
        this.convertToCall = true;
        this.type = null;
    }

    public OperatorExpression(Operator operator, Expression lhs, Expression rhs, boolean convertToCall) {
        this(operator, lhs, rhs);
        this.convertToCall = convertToCall;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public List<Type> getTypes() {
        if (type == null) {
            return List.of(operator.getType());
        }
        return List.of(type);
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {
        operator.semanticCheck(method, lhs, rhs);
    }

    @Override
    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public String toString() {
        if (replacementExpression.isPresent()) {
            return replacementExpression.get().toString();
        }
        return operator.formExpression(lhs, rhs);
    }

    @Override
    public List<String> toCode() {
        List<String> code = new ArrayList<>();

        if (convertToCall && operator.equals(NumericOperator.Divide)) {
            CallExpression safe_division = new CallExpression(symbolTable.getMethod("safe_division"));
            safe_division.setSymbolTable(symbolTable);
            try {
                safe_division.addArg(lhs);
                safe_division.addArg(rhs);
            } catch (InvalidArgumentException e) {
                System.err.println("ADDED DODGY LHS");
            }
            replacementExpression = Optional.of(safe_division);
            return safe_division.toCode();
        } else if (convertToCall && operator.equals(NumericOperator.Modulus)) {
            CallExpression safe_modulus = new CallExpression(symbolTable.getMethod("safe_modulus"));
            safe_modulus.setSymbolTable(symbolTable);
            try {
                safe_modulus.addArg(lhs);
                safe_modulus.addArg(rhs);
            } catch (InvalidArgumentException e) {
                System.err.println("ADDED DODGY LHS");
            }
            replacementExpression = Optional.of(safe_modulus);
            return safe_modulus.toCode();
        }

        code.addAll(lhs.toCode());
        code.addAll(rhs.toCode());

        return code;
    }
}
