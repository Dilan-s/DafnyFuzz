package AST.Statements;

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
    public List<Object> execute(Map<Variable, Variable> paramMap, StringBuilder s) {
        incrementUse();
        return execute(paramMap, s, true);
    }

    protected abstract List<Object> execute(Map<Variable, Variable> paramMap, StringBuilder s, boolean unused);
}
