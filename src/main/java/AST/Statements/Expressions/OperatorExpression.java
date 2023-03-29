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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class OperatorExpression implements Expression {

    private final Operator operator;
    private final List<Expression> args;
    private Optional<Expression> replacementExpression;

    private Type type;
    private boolean convertToCall;
    private SymbolTable symbolTable;

    public OperatorExpression(SymbolTable symbolTable, Operator operator, List<Expression> args, boolean convertToCall) {
        this.symbolTable = symbolTable;
        this.replacementExpression = Optional.empty();
        this.operator = operator;
        this.convertToCall = convertToCall;
        this.type = null;
        this.args = args;
        generateMethodCallReplacement();
    }

    public OperatorExpression(SymbolTable symbolTable, Operator operator, List<Expression> args) {
        this(symbolTable, operator, args, true);
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

        if (replacementExpression.isPresent()) {
            return replacementExpression.get().toCode();
        }

        code.addAll(args.stream()
            .map(Expression::toCode)
            .flatMap(Collection::stream)
            .collect(Collectors.toList()));

        return code;
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
    public int hashCode() {
        if (replacementExpression.isPresent()) {
            return replacementExpression.hashCode();
        } else {
            return Objects.hash(operator, args);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OperatorExpression)) {
            return false;
        }
        OperatorExpression other = (OperatorExpression) obj;

        if (replacementExpression.isPresent() && other.replacementExpression.isPresent()) {
            return replacementExpression.get().equals(other.replacementExpression.get());
        } else if (replacementExpression.isEmpty() && other.replacementExpression.isEmpty()) {
            if (!operator.equals(other.operator) || args.size() != other.args.size()) {
                return false;
            }

            for (int i = 0; i < args.size(); i++) {
                if (!args.get(i).equals(other.args.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
