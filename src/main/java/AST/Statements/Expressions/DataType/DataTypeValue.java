package AST.Statements.Expressions.DataType;

import AST.Statements.Expressions.Array.ArrayValue;
import AST.SymbolTable.Types.Type;
import java.util.List;
import java.util.Objects;

public class DataTypeValue {

    private final Type type;
    private final List<Object> values;

    public DataTypeValue(Type type, List<Object> values) {
        this.type = type;
        this.values = values;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, values);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DataTypeValue)) {
            return false;
        }

        DataTypeValue other = (DataTypeValue) obj;
        return other.type.equals(type) && other.values.equals(values);
    }

    @Override
    public String toString() {
        return type.getName() + values.toString();
    }

    public Object get(int index) {
        return values.get(index);
    }

    public void set(int index, Object value) {
        values.set(index, value);
    }

    public int size() {
        return values.size();
    }

    public Type getType() {
        return type;
    }
}
