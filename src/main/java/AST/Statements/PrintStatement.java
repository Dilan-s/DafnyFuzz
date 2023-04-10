package AST.Statements;

import AST.Errors.SemanticException;
import AST.Statements.Expressions.Expression;
import AST.Statements.util.ReturnStatus;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
    public List<String> toCode() {
        List<String> code = new ArrayList<>();

        code.addAll(values.stream()
            .map(Expression::toCode)
            .flatMap(Collection::stream)
            .collect(Collectors.toList()));

        String printValues = values.stream()
            .filter(x -> x.getTypes().stream().allMatch(Type::isPrintable))
            .map(Expression::toString)
            .collect(Collectors.joining(", ' ', "));
        if (!printValues.isEmpty()) {
            code.add(String.format("print %s, \"\\n\";\n", printValues));
        }
        return code;
    }

    @Override
    public ReturnStatus assignReturnIfPossible(Method method, ReturnStatus currStatus, List<Expression> dependencies) {
        return currStatus;
    }

    @Override
    public List<Object> execute(Map<Variable, Variable> paramMap) {
        return null;
    }

    @Override
    public List<Statement> expand() {
        return values.stream()
            .map(Expression::expand)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }
}
