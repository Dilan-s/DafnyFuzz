package AST.Statements;

import AST.Errors.SemanticException;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
            .map(Expression::toString)
            .collect(Collectors.joining(", ' ', "));

        code.add(String.format("print %s, \"\\n\";\n", printValues));
        return code;
    }
}
