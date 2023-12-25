package AST.Expressions.DClass;

import AST.SymbolTable.Types.Variables.Variable;
import java.util.List;
import java.util.Objects;

public class DClassValue {

  private static int no = 0;
  private final int num;
  private final String name;
  private final Variable variable;
  private final List<Object> contents;

  public DClassValue(Variable variable, List<Object> contents) {
    this.variable = variable;
    this.name = variable.getName();
    this.contents = contents;
    this.num = DClassValue.no;
    DClassValue.no++;
  }

  public int getNum() {
    return num;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, contents, num);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof DClassValue)) {
      return false;
    }

    DClassValue other = (DClassValue) obj;
    return other.variable.equals(variable) && other.contents.equals(contents) && other.num == num;
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