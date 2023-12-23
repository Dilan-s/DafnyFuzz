package AST.Statements;

import AST.Statements.util.PrintAll;
import AST.Statements.util.ReturnStatus;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContinueStatement extends BaseStatement {

    private Statement statement;
    private SymbolTable symbolTable;

    public ContinueStatement(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.statement = new PrintAll(symbolTable);
    }

    @Override
    protected ReturnStatus execute(Map<Variable, Variable> paramMap, StringBuilder s, boolean unused) {
        return ReturnStatus.CONTINUE;
    }

    @Override
    public List<Statement> expand() {
        List<Statement> r = new ArrayList<>();
        r.addAll(statement.expand());
        r.add(this);
        return r;
    }

    @Override
    public boolean isReturn() {
        return true;
    }

    @Override
    public List<String> toOutput() {
        List<String> r = new ArrayList<>();
        String curr = "continue;";
        r.add(curr);
        return r;
    }

    @Override
    public String toString() {
        return "continue;";
    }

    @Override
    public String minimizedTestCase() {
        return toString();
    }

    @Override
    public boolean validForFunctionBody() {
        return false;
    }
}
