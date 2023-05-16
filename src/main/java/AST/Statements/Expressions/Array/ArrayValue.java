package AST.Statements.Expressions.Array;

import AST.SymbolTable.Types.Variables.Variable;
import java.util.List;
import java.util.Objects;

public class ArrayValue {
    private String name;
    private Variable variable;
    private List<Object> contents;

    public ArrayValue(Variable variable, List<Object> contents) {
        this.variable = variable;
        this.name = variable.getName();
        this.contents = contents;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, contents);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ArrayValue)) {
            return false;
        }

        ArrayValue other = (ArrayValue) obj;
        return other.name.equals(name) && other.contents.equals(contents);
    }

    public int size() {
        return contents.size();
    }

    @Override
    public String toString() {
        return contents.toString();
    }

    public Object get(int index) {
        return contents.get(index);
    }

    public void set(int index, Object value) {
        contents.set(index, value);
    }

    public String getName() {
        return name;
    }

    public List<Object> getContents() {
        return contents;
    }

    public Variable getVariable() {
        return variable;
    }
}