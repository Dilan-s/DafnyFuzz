package AST.SymbolTable.Types.Variables;

import AST.Expressions.Array.ArrayValue;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VariableArrayIndex extends Variable {

    private final int index;
    private final Variable variable;

    public VariableArrayIndex(Variable variable, Type type, int index) {
        super(variable.getName(), type);
        this.variable = variable;
        this.index = index;
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap) {
        List<Object> l = new ArrayList<>();
        ArrayValue value = (ArrayValue) variable.getValue(paramsMap).get(0);
        if (value != null) {
            Object o = value.get(index);
            Object v = type.of(o);
            l.add(v);
        } else {
            l.add(null);
        }
        return l;
    }

    @Override
    public void setValue(SymbolTable symbolTable, Map<Variable, Variable> paramMap, Object value) {
        ArrayValue v = (ArrayValue) variable.getValue(paramMap).get(0);
        v.set(index, value);
    }

    @Override
    public String getName() {
        return super.getName() + "[" + index + "]";
    }

    @Override
    public List<Variable> getRelatedAssignment() {
        return List.of(variable, this);
    }

    @Override
    public boolean modified(Variable x) {
        return variable == x;
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, index);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VariableArrayIndex)) {
            return false;
        }
        VariableArrayIndex other = (VariableArrayIndex) obj;
        return other.variable.equals(variable) && other.index == index;
    }
}
