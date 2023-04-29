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
}
