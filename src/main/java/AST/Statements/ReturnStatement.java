package AST.Statements;

import AST.Errors.SemanticException;
import AST.Statements.Expressions.Expression;
import AST.Statements.util.PrintAll;
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

public class ReturnStatement implements Statement {

    private final SymbolTable symbolTable;
    private final List<Expression> values;
    private boolean printAll;

    public ReturnStatement(SymbolTable symbolTable, List<Expression> values) {
        this.symbolTable = symbolTable;
        this.values = values;
        this.printAll = true;

    }

    public void setPrintAll(boolean printAll) {
        this.printAll = printAll;
    }

    @Override
    public boolean isReturn() {
        return true;
    }

    @Override
    public boolean couldReturn() {
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

            if (!returnType.equals(expressionType)) {
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

        if (printAll) {
            PrintAll printAll = new PrintAll(symbolTable);
            code.addAll(printAll.toCode());
        }

        code.add(String.format("return %s;\n", returnValues));
        return code;
    }

    public List<Expression> getReturnValues() {
        return values;
    }

    @Override
    public ReturnStatus assignReturnIfPossible(Method method, ReturnStatus currStatus, List<Expression> dependencies) {
        method.setReturnValues(values, dependencies);
        return ReturnStatus.ASSIGNED;
    }

    @Override
    public List<Object> execute(Map<Variable, Variable> paramMap) {
        return null;
    }

    @Override
    public List<Statement> expand() {
        List<Statement> r = new ArrayList<>();
        r.addAll(values.stream()
            .map(Expression::expand)
            .flatMap(Collection::stream)
            .collect(Collectors.toList()));
        if (printAll) {
            r.add(new PrintAll(symbolTable));
        }
        return r;
    }
}
