package AST.SymbolTable;

import AST.SymbolTable.Types.Type;

public class Variable implements Identifier {

    private final String name;
    private final Type type;
    private boolean isDeclared;

    public Variable(String name, Type type) {
        this.name = name;
        this.type = type;
        this.isDeclared = false;
    }

    @Override
    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public void setDeclared() {
        isDeclared = true;
    }

    @Override
    public String toString() {
        return String.format("%s%s", name, getType().getTypeIndicatorString());
    }

    public boolean isDeclared() {
        return isDeclared;
    }
}
