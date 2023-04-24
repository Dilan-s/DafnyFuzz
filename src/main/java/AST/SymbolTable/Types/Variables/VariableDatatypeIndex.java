package AST.SymbolTable.Types.Variables;

import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VariableDatatypeIndex extends Variable {

    private final int index;
    private final Variable variable;

    public VariableDatatypeIndex(Variable variable, Type type, int index) {
        super(variable.getName(), type);
        this.variable = variable;
        this.index = index;
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap) {
        List<Object> l = new ArrayList<>();
        List<Object> value = (List<Object>) variable.getValue(paramsMap).get(0);
        if (value != null) {
            l.add(value.get(index));
        } else {
            l.add(null);
        }
        return l;
    }

    @Override
    public String getName() {
        return super.getName() + "." + index;
    }
}
