package AST.Statements;

import AST.Errors.SemanticException;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import java.util.ArrayList;
import java.util.List;

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
    public List<String> toCode() {
        List<String> code = new ArrayList<>();
        for (Statement s : body) {
            code.addAll(s.toCode());
        }
        return code;
    }


}
