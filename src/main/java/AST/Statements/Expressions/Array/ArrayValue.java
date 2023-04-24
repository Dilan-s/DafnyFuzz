package AST.Statements.Expressions.Array;

import java.util.List;
import java.util.Objects;

public class ArrayValue {
    private String name;
    private List<Object> contents;

    public ArrayValue(String name, List<Object> contents) {
        this.name = name;
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
}