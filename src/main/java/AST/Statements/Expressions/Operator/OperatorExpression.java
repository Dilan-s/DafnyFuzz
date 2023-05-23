package AST.Statements.Expressions.Operator;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.BaseExpression;
import AST.Statements.Expressions.CallExpression;
import AST.Statements.Expressions.Expression;
import AST.Statements.Statement;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class OperatorExpression extends BaseExpression {

    private final Operator operator;
    private final List<Expression> args;
    private Optional<Expression> replacementExpression;

    private Type type;
    private boolean convertToCall;
    private SymbolTable symbolTable;

    private boolean update;

    private List<List<Statement>> expanded;

    public OperatorExpression(SymbolTable symbolTable, Type type, Operator operator, List<Expression> args, boolean convertToCall) {
        super();
        this.symbolTable = symbolTable;
        this.replacementExpression = Optional.empty();
        this.operator = operator;
        this.convertToCall = convertToCall;
        this.type = type;
        this.args = args;
        this.update = false;

        expanded = new ArrayList<>();
        args.forEach(a -> expanded.add(a.expand()));
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
    public String toString() {
        if (replacementExpression.isPresent()) {
            return replacementExpression.get().toString();
        }
        return operator.formExpression(args);
    }

    @Override
    public String minimizedTestCase() {
        if (replacementExpression.isPresent()) {
            return replacementExpression.get().minimizedTestCase();
        }
        return operator.formMinimizedExpression(args);
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
            if (replacementExpression.get().requireUpdate()) {
                expanded.set(0, replacementExpression.get().expand());
            }
        } else {
            for (int i = 0; i < args.size(); i++) {
                Expression arg = args.get(i);
                if (arg.requireUpdate()) {
                    expanded.set(i, arg.expand());
                }
            }
        }
        update = false;
        return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public boolean requireUpdate() {
        return this.update || args.stream().anyMatch(Expression::requireUpdate) || (replacementExpression.isPresent() && replacementExpression.get().requireUpdate());
    }

    private void generateMethodCallReplacement(Map<Variable, Variable> paramsMap, StringBuilder s) {
        if (convertToCall && operator.equals(BinaryOperator.Divide)) {
            CallExpression safe_division = new CallExpression(symbolTable, symbolTable.getMethod("safe_division"), args);
            for (Statement stat : safe_division.expand()) {
                stat.execute(paramsMap, s);
            }
            replacementExpression = Optional.of(safe_division);
            expanded = new ArrayList<>();
            expanded.add(safe_division.expand());
            update = true;

        } else if (convertToCall && operator.equals(BinaryOperator.Modulus)) {
            CallExpression safe_modulus = new CallExpression(symbolTable, symbolTable.getMethod("safe_modulus"), args);
            for (Statement stat : safe_modulus.expand()) {
                stat.execute(paramsMap, s);
            }
            replacementExpression = Optional.of(safe_modulus);
            expanded = new ArrayList<>();
            expanded.add(safe_modulus.expand());
            update = true;
        }
    }

    @Override
    protected List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s, boolean unused) {
        List<Object> r = new ArrayList<>();

        if (replacementExpression.isPresent()) {
            return replacementExpression.get().getValue(paramsMap, s);
        }

        List<Object> vals = new ArrayList<>();
        for (Expression e : args) {
            List<Object> value = e.getValue(paramsMap, s);
            for (Object v : value) {
                if (v == null) {
                    r.add(null);
                    return r;
                }
            }
            vals.addAll(value);
        }

        if (operator.requiresSafe(vals)) {
            generateMethodCallReplacement(paramsMap, s);
            if (replacementExpression.isPresent()) {
                return replacementExpression.get().getValue(paramsMap, s);
            }

        }

        r.add(operator.apply(args, paramsMap));
        return r;
    }

    public OperatorExpression mutateForInvalidValidation() {
        List<Type> argTypes = args.stream()
            .map(Expression::getTypes)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
        List<Operator> ops = operator.mutateForInvalidValidation(argTypes);

        int opInd = GeneratorConfig.getRandom().nextInt(ops.size());
        Operator op = ops.get(opInd);

        return new OperatorExpression(symbolTable, type, op, args);
    }
}
