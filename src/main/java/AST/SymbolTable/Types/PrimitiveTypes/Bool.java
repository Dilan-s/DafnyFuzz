package AST.SymbolTable.Types.PrimitiveTypes;

import AST.Expressions.BoolLiteral;
import AST.Expressions.Expression;
import AST.Generator.GeneratorConfig;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.Objects;

public class Bool implements BaseType {

  @Override
  public String getName() {
    return "bool";
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Type)) {
      return false;
    }
    Type other = (Type) obj;
    return other instanceof Bool;
  }

  @Override
  public Expression generateLiteral(SymbolTable symbolTable) {
    return new BoolLiteral(this, symbolTable, GeneratorConfig.getRandom().nextBoolean());
  }

  @Override
  public Expression generateExpressionFromValue(SymbolTable symbolTable, Object value) {
    Boolean b = (Boolean) value;
    return new BoolLiteral(this, symbolTable, b);
  }

  @Override
  public boolean operatorExists() {
    return true;
  }

  @Override
  public Boolean lessThan(Object lhsV, Object rhsV) {
    return false;
  }

  @Override
  public Boolean equal(Object lhsV, Object rhsV) {
    Boolean l = (Boolean) lhsV;
    Boolean r = (Boolean) rhsV;
    return Objects.equals(l, r);
  }

  @Override
  public String formatPrint(Object object) {
    return object.toString();
  }

  @Override
  public String formatEnsures(String variableName, Object object) {
    if (object == null) {
      return null;
    }
    Boolean v = Boolean.valueOf(object.toString());

    return String.format("(%s == %b)", variableName, v);
  }

  @Override
  public boolean isOrdered() {
    return false;
  }
}
