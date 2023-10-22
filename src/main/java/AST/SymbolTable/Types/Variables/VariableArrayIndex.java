package AST.SymbolTable.Types.Variables;

import AST.Expressions.Array.ArrayValue;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VariableArrayIndex extends Variable {

    private final Variable variable;
    private BigInteger indexInt;
    private Variable indexVar;

    private VariableArrayIndex(Variable variable, Type type, Variable indexVar, BigInteger indexInt) {
        super(variable.getName(), type);
        this.variable = variable;
        this.indexInt = indexInt;
        this.indexVar = indexVar;
    }

    public VariableArrayIndex(Variable variable, Type type, int indexInt) {
        this(variable, type, null, BigInteger.valueOf(indexInt));
    }

    public VariableArrayIndex(Variable variable, Type type, Variable indexVar) {
        this(variable, type, indexVar, null);
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap) {
        List<Object> l = new ArrayList<>();
        ArrayValue value = (ArrayValue) variable.getValue(paramsMap).get(0);
        if (value != null) {
            BigInteger index = indexInt;
            if (indexVar != null) {
                index = (BigInteger) indexVar.getValue(paramsMap).get(0);
            }
            Object o = value.get(index.intValue());
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
        BigInteger index = indexInt;
        if (indexVar != null) {
            index = (BigInteger) indexVar.getValue(paramMap).get(0);
        }
        v.set(index.intValue(), value);
    }

    @Override
    public String getName() {
        return super.getName() + "[" + (indexVar != null ? indexVar.getName() : indexInt.intValue()) + "]";
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
        return Objects.hash(variable, indexInt);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VariableArrayIndex)) {
            return false;
        }
        VariableArrayIndex other = (VariableArrayIndex) obj;
        return other.variable.equals(variable) && (Objects.equals(other.indexInt, indexInt) || Objects.equals(other.indexVar, indexVar));
    }
}
