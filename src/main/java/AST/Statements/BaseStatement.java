package AST.Statements;

import AST.Statements.util.ReturnStatus;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.List;
import java.util.Map;

public abstract class BaseStatement implements Statement {
    private int useFreq;

    public BaseStatement() {
        this.useFreq = 0;
    }

    @Override
    public void incrementUse() {
        this.useFreq++;
    }

    @Override
    public int getNoOfUses() {
        return useFreq;
    }

    @Override
    public ReturnStatus execute(Map<Variable, Variable> paramMap, StringBuilder s) {
        incrementUse();
        ReturnStatus execute = execute(paramMap, s, true);
        return execute;
    }

    protected abstract ReturnStatus execute(Map<Variable, Variable> paramMap, StringBuilder s, boolean unused);

    @Override
    public List<String> toOutput() {
        return List.of(toString());
    }
}
