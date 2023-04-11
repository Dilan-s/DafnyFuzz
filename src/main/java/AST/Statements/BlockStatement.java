package AST.Statements;

import AST.Errors.SemanticException;
import AST.Statements.Expressions.Expression;
import AST.Statements.util.ReturnStatus;
import AST.StringUtils;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BlockStatement implements Statement {

    private final SymbolTable symbolTable;
    private final List<Statement> body;

    public BlockStatement(SymbolTable symbolTable) {
        this.symbolTable = new SymbolTable(symbolTable);
        this.body = new ArrayList<>();
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void addStatement(Statement statement) {
        body.add(statement);
    }

    public void addStatement(List<Statement> statement) {
        body.addAll(statement);
    }

    public void semanticCheck(Method method) throws SemanticException {
        for (Statement statement : body) {
            statement.semanticCheck(method);
        }
    }

    @Override
    public boolean isReturn() {
        return body.stream().anyMatch(Statement::isReturn);
    }

    @Override
    public boolean couldReturn() {
        return body.stream().anyMatch(Statement::couldReturn);
    }

    @Override
    public ReturnStatus assignReturnIfPossible(Method method, ReturnStatus currStatus, List<Expression> dependencies) {
        ReturnStatus curr = currStatus;
        for (int i = 0; curr != ReturnStatus.ASSIGNED && curr != ReturnStatus.UNABLE && i < body.size(); i++) {
            Statement s = body.get(i);
            curr = s.assignReturnIfPossible(method, curr, dependencies);
        }
        return curr;
    }

    @Override
    public List<Object> execute(Map<Variable, Variable> paramMap, StringBuilder s) {
        for (int i = 0, bodySize = body.size(); i < bodySize; i++) {
            Statement statement = body.get(i);
            List<Object> retValues = statement.execute(paramMap, s);
            if (retValues != null) {
                return retValues;
            }
        }
        return null;
    }

    @Override
    public List<Statement> expand() {
        return body.stream()
            .map(Statement::expand)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        List<String> code = new ArrayList<>();
        for (Statement s : body) {
            String val = s.toString();
            code.add(val);
        }
        return StringUtils.intersperse("\n", code);
    }


}
