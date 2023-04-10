package AST.Statements;

import AST.Errors.SemanticException;
import AST.Statements.Expressions.Expression;
import AST.Statements.util.PrintAll;
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
    public String toString() {
        List<String> code = new ArrayList<>();

        String returnValues = values.stream()
            .map(Expression::toString)
            .collect(Collectors.joining(", "));

        code.add(String.format("return %s;", returnValues));
        return StringUtils.intersperse("\n", code);
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
        List<Object> list = new ArrayList<>();
        for (Expression x : values) {
            List<Object> value = x.getValue(paramMap);
            for (Object object : value) {
                list.add(object);
            }
        }
        return list;
    }

    @Override
    public List<Statement> expand() {
        List<Statement> r = new ArrayList<>();
        List<Statement> list = new ArrayList<>();
        for (Expression value : values) {
            List<Statement> expand = value.expand();
            for (Statement statement : expand) {
                list.add(statement);
            }
        }
        r.addAll(list);
        if (printAll) {
            r.add(new PrintAll(symbolTable));
        }
        r.add(this);
        return r;
    }
}
