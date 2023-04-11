package AST.Statements;

import AST.Errors.SemanticException;
import AST.Statements.Expressions.Expression;
import AST.Statements.util.ReturnStatus;
import AST.StringUtils;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class PrintStatement implements Statement {

    private final SymbolTable symbolTable;
    private final List<Expression> values;

    public PrintStatement(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.values = new ArrayList<>();
    }

    public void addValue(Expression expression) {
        values.add(expression);
    }


    @Override
    public void semanticCheck(Method method) throws SemanticException {
        for (Expression e : values) {
            e.semanticCheck(method);
        }
    }

    @Override
    public String toString() {
        List<String> code = new ArrayList<>();;

        String printValues = values.stream()
            .filter(x -> x.getTypes().stream().allMatch(Type::isPrintable))
            .map(Expression::toString)
            .collect(Collectors.joining(", ' ', "));
        if (!printValues.isEmpty()) {
            code.add(String.format("print %s, \"\\n\";", printValues));
        }
        return StringUtils.intersperse("\n", code);
    }

    @Override
    public ReturnStatus assignReturnIfPossible(Method method, ReturnStatus currStatus, List<Expression> dependencies) {
        return currStatus;
    }

    @Override
    public List<Object> execute(Map<Variable, Variable> paramMap, StringBuilder s) {
        StringJoiner joiner = new StringJoiner(" ");
        for (Expression exp : values) {
            List<Type> types = exp.getTypes();
            if (types.stream().allMatch(Type::isPrintable)) {
                List<Object> value = exp.getValue(paramMap, s);
                for (int i = 0, valueSize = value.size(); i < valueSize; i++) {
                    Object object = value.get(i);
                    Type t = types.get(i);
                    String str = t.formatPrint(object);
                    joiner.add(str);
                }
            }
        }
        String printValues = joiner.toString();

        if (!printValues.isEmpty()) {
            s.append(printValues);
            s.append("\n");
        }
        return null;
    }

    @Override
    public List<Statement> expand() {
        List<Statement> r = new ArrayList<>();
        r.addAll(values.stream()
            .map(Expression::expand)
            .flatMap(Collection::stream)
            .collect(Collectors.toList()));
        r.add(this);
        return r;
    }
}
