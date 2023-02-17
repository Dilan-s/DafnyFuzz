package AST.Statements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BranchStatementNode implements Statement {

    private Statement left;
    private Statement right;

    public BranchStatementNode(Statement left, Statement right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public List<String> generateCode() {
        List<String> statements = new ArrayList<>();
        statements.addAll(left.generateCode());
        statements.addAll(right.generateCode());
        return statements;
    }

    @Override
    public List<Statement> generateValue(Map<String, Statement> symbolTable) {
        return null;
    }
}
