package AST.SymbolTable.Types.Variables;

import AST.SymbolTable.Types.Type;

public class VariableIndex extends Variable {

    private final int index;

    public VariableIndex(String name, Type type, int index) {
        super(name, type);
        this.index = index;
    }

    @Override
    public String getName() {
        return super.getName() + "[" + index + "]";
    }
}
