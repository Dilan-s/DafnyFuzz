package AST.SymbolTable.Types.Variables;

import AST.SymbolTable.Identifier;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Variable implements Identifier {

    private final String name;
    private final Type type;
    private boolean isDeclared;
    private Object value;

    public Variable(String name, Type type) {
        this.name = name;
        this.type = type;
        this.isDeclared = false;
        this.value = null;
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
        return String.format("%s: %s", name, getType().getVariableType());
    }

    public boolean isDeclared() {
        return isDeclared;
    }

    public List<Object> getValue() {
        return getValue(new HashMap<>());
    }

    public List<Object> getValue(Map<Variable, Variable> paramsMap) {
        if (paramsMap.containsKey(this)) {
            return paramsMap.get(this).getValue(paramsMap);
        }
        List<Object> l = new ArrayList<>();
        l.add(value);
        return l;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
