package AST.SymbolTable.Types.PrimitiveTypes.Int;

import AST.Expressions.Expression;
import AST.Expressions.IntLiteral;
import AST.Generator.GeneratorConfig;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.PrimitiveTypes.BaseType;
import AST.SymbolTable.Types.Type;
import java.math.BigInteger;

public class Int implements BaseType {

  public static final double PROB_NEGATION = 0.1;
  private static final int MAX_INT = 30;
  protected int max;

  public Int(int max) {
    this.max = max;
  }

  public Int() {
    this(MAX_INT);
  }

  @Override
  public String getName() {
    return "int";
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
    return other instanceof Int;
  }

  @Override
  public Expression generateLiteral(SymbolTable symbolTable) {
    Integer intValue = GeneratorConfig.getRandom().nextInt(max);
    intValue *= GeneratorConfig.getRandom().nextDouble() < PROB_NEGATION ? -1 : 1;
    BigInteger value = BigInteger.valueOf(intValue);
    return new IntLiteral(this, symbolTable, value);
  }

  @Override
  public Expression generateExpressionFromValue(SymbolTable symbolTable, Object value) {
    BigInteger v = new BigInteger(value.toString());
    return new IntLiteral(this, symbolTable, v);
  }

  @Override
  public boolean operatorExists() {
    return true;
  }

  @Override
  public Boolean lessThan(Object lhsV, Object rhsV) {
    BigInteger lhs = (BigInteger) lhsV;
    BigInteger rhs = (BigInteger) rhsV;
    return lhs.compareTo(rhs) < 0;
  }

  @Override
  public Boolean equal(Object lhsV, Object rhsV) {
    BigInteger lhs = (BigInteger) lhsV;
    BigInteger rhs = (BigInteger) rhsV;
    return lhs.compareTo(rhs) == 0;
  }

  @Override
  public String formatPrint(Object object) {
    return String.valueOf(object);
  }

  @Override
  public String formatEnsures(String variableName, Object object) {
    if (object == null) {
      return null;
    }
    BigInteger v = new BigInteger(object.toString());

    return String.format("(%s == %d)", variableName, v);
  }
}
