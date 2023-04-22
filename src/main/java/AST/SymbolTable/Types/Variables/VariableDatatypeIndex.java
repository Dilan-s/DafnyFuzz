package AST.SymbolTable.Types.Variables;

import AST.SymbolTable.Types.Type;

public class VariableDatatypeIndex extends Variable {

    private final int index;

    public VariableDatatypeIndex(String name, Type type, int index) {
        super(name, type);
        this.index = index;
    }

    @Override
    public String getName() {
        return super.getName() + "." + index;
    }
}
