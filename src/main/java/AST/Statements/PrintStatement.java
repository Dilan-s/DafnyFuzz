package AST.Statements;

import AST.Expressions.Expression;
import AST.Expressions.Operator.BinaryOperator;
import AST.Expressions.Operator.OperatorExpression;
import AST.Expressions.StringLiteral;
import AST.Expressions.VariableExpression;
import AST.Statements.util.ReturnStatus;
import AST.StringUtils;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.Types.PrimitiveTypes.DString;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PrintStatement extends BaseStatement {

    private final SymbolTable symbolTable;
    private final List<Variable> variables;
    private List<Expression> values;
    private List<List<Statement>> expanded;
    private boolean update;

    public PrintStatement(SymbolTable symbolTable) {
        super();
        this.update = false;
        this.symbolTable = symbolTable;
        this.values = new ArrayList<>();
        this.variables = symbolTable.getAllVariablesInCurrentScope();
        this.expanded = new ArrayList<>();
        expanded.add(List.of(this));
    }

    @Override
    public String toString() {
        List<String> code = new ArrayList<>();

        String printValues = values.stream()
            .filter(x -> x.getTypes().stream().allMatch(Type::isPrintable))
            .map(Expression::toString)
            .collect(Collectors.joining(", \" \", "));
        if (!printValues.isEmpty()) {
            code.add(String.format("print %s, \"\\n\";", printValues));
        }
        return StringUtils.intersperse("\n", code);
    }

    @Override
    public boolean minimizedReturn() {
        return super.minimizedReturn();
    }

    @Override
    public String minimizedTestCase() {
        List<String> code = new ArrayList<>();

        String printValues = values.stream()
            .filter(x -> x.getTypes().stream().allMatch(Type::isPrintable))
            .map(Expression::minimizedTestCase)
            .collect(Collectors.joining(", \" \", "));
        if (!printValues.isEmpty()) {
            code.add(String.format("print %s, \"\\n\";", printValues));
        }
        return StringUtils.intersperse("\n", code);
    }

    @Override
    public List<String> toOutput() {
        return List.of(toString());
    }

    @Override
    protected ReturnStatus execute(Map<Variable, Variable> paramMap, StringBuilder s, boolean unused) {
        if (values.isEmpty()) {
            values = new ArrayList<>();
            variables.stream()
                .filter(symbolTable::variableInScope)
                .filter(v -> v.getValue(paramMap).stream().allMatch(Objects::nonNull))
                .forEach(v -> {
                    StringLiteral stringLiteral = new StringLiteral(new DString(), symbolTable, v.getName());
                    VariableExpression expression = new VariableExpression(symbolTable, v, v.getType());
                    values.add(stringLiteral);
                    expanded.add(expanded.size() - 1, stringLiteral.expand());
                    values.add(expression);
                    expanded.add(expanded.size() - 1, expression.expand());
                });
        }

        List<String> joiner = new ArrayList<>();

        List<Expression> values = new ArrayList<>(this.values);
        for (int i = 0; i < values.size(); i++) {
            Expression exp = values.get(i);
            List<Type> types = exp.getTypes();
            List<Object> value = exp.getValue(paramMap, s);
            if (types.stream().allMatch(Type::isPrintable)) {
                for (int j = 0; j < value.size(); j++) {
                    Object object = value.get(j);
                    Type t = types.get(j);
                    String str = t.formatPrint(object);
                    joiner.add(str);
                }
            } else {
                for (int j = 0; j < value.size(); j++) {
                    Object object = value.get(j);
                    Type t = types.get(j);
                    Expression newE = t.generateExpressionFromValue(symbolTable, object);
                    if (newE != null) {
                        OperatorExpression op = new OperatorExpression(symbolTable, new Bool(), BinaryOperator.Equals, List.of(exp, newE));
                        this.values.set(i, op);
                        this.expanded.set(i, op.expand());
                        for (Statement st : op.expand()) {
                            st.execute(paramMap, s);
                        }

                        this.update = true;
                        String str = "true";
                        joiner.add(str);
                    }
                }
            }
        }
        String printValues = String.join(" ", joiner);

        if (!joiner.isEmpty()) {
            s.append(printValues);
            s.append("\n");
        }
        return ReturnStatus.UNKNOWN;
    }

    @Override
    public boolean requireUpdate() {
        return update || (values != null && values.stream().anyMatch(Expression::requireUpdate));
    }

    @Override
    public List<Statement> expand() {
        for (int i = 0; i < values.size(); i++) {
            Expression exp = values.get(i);
            if (exp.requireUpdate()) {
                expanded.set(i, exp.expand());
            }
        }
        update = false;
        return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }
}
