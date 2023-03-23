package AST.Statements;

import AST.Errors.SemanticException;
import AST.Statements.Expressions.Expression;
import AST.Statements.util.PrintAll;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ReturnStatement implements Statement {

    private final SymbolTable symbolTable;
    private final List<Expression> values;

    public ReturnStatement(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.values = new ArrayList<>();
    }

    public void addValue(Expression expression) {
        values.add(expression);
    }

    @Override
    public boolean isReturn() {
        return true;
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {
        List<Type> returnTypes = method.getReturnTypes();
        List<Type> valueTypes = values.stream()
            .map(Expression::getTypes)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        int noValues = valueTypes.size();
        int noReturnTypes = returnTypes.size();
        if (noValues != noReturnTypes) {
            throw new SemanticException(String.format(
                "Expected %d arguments but actually got %d arguments in return statement",
                noReturnTypes, noValues));
        }

        for (int i = 0; i < noValues; i++) {
            Type expressionType = valueTypes.get(i);
            Type returnType = returnTypes.get(i);

            if (!returnType.isSameType(expressionType)) {
                throw new SemanticException(
                    String.format("Expected %dth argument to be %s but actually go %s", i,
                        returnType.getName(), expressionType.getName()));
            }
        }

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

        String returnValues = values.stream()
            .map(Expression::toString)
            .collect(Collectors.joining(", "));

        PrintAll printAll = new PrintAll(symbolTable);
        code.addAll(printAll.toCode());

        code.add(String.format("return %s;\n", returnValues));
        return code;
    }

}
