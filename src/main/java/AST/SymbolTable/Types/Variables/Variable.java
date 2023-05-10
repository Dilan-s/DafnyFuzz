package AST.SymbolTable.Types.Variables;

import AST.SymbolTable.Identifier;
import AST.SymbolTable.Types.DCollectionTypes.DArray;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.Tuple;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Variable implements Identifier {

    private final String name;
    private final Type type;
    private boolean isConstant;
    private boolean isDeclared;
    private Object value;


    public Variable(String name, Type type) {
        this.name = name;
        this.type = type;
        this.isConstant = false;
        this.isDeclared = false;
        this.value = null;
    }

    public void setConstant() {
        isConstant = true;
    }

    public boolean isConstant() {
        return isConstant;
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
        return String.format("%s: %s", getName(), getType().getVariableType());
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

    public void setValue(Map<Variable, Variable> paramMap, Object value) {
        if (paramMap.containsKey(this)) {
            paramMap.get(this).setValue(paramMap, value);
        }
        this.value = value;
    }

    public List<Variable> getSymbolTableArgs() {
        List<Variable> vars = new ArrayList<>();
        if (type.equals(new DArray())) {
            DArray dArray = (DArray) this.type;
            for (int i = 0; i < DArray.MIN_SIZE_OF_ARRAY; i++) {
                vars.addAll(new VariableArrayIndex(this, dArray.getInnerType(), i).getSymbolTableArgs());
            }
        } else if (type.equals(new Tuple())) {
            Tuple tuple = (Tuple) this.type;
            for (int i = 0; i < tuple.getNoOfType(); i++) {
                vars.addAll(new VariableDatatypeIndex(this, tuple.getType(i), i).getSymbolTableArgs());
            }

        }
        vars.add(this);
        return vars;
    }

    public boolean modified(Variable x) {
        return false;
    }
}
