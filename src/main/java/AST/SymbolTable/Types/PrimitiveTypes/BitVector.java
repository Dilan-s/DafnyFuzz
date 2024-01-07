package AST.SymbolTable.Types.PrimitiveTypes;

import AST.Expressions.BitVectorLiteral;
import AST.Expressions.Expression;
import AST.Generator.GeneratorConfig;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.math.BigInteger;
import java.util.Objects;

public class BitVector implements BaseType {

  private Integer size;

  public BitVector(Integer size) {
    this.size = size;
  }

  public BitVector() {
    this(null);
  }

  @Override
  public String getName() {
    return String.format("bv%d", size);
  }

  @Override
  public Type concrete(SymbolTable symbolTable) {
    if (size == null) {
      return new BitVector(GeneratorConfig.getRandom().nextInt(32));
    }
    return new BitVector(size);
  }

  @Override
  public int hashCode() {
    return Objects.hash(size, "bv");
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Type)) {
      return false;
    }
    Type other = (Type) obj;

    if (!(other instanceof BitVector)) {
      return false;
    }

    BitVector bitVectorOther = other.asBitVector();

    return size == null || bitVectorOther.size == null || size.equals(bitVectorOther.size);
  }

  @Override
  public Expression generateLiteral(SymbolTable symbolTable) {
    Long longValue = GeneratorConfig.getRandom().nextLong(1L << size);
    BigInteger value = BigInteger.valueOf(longValue);
    return new BitVectorLiteral(this, symbolTable, value);
  }

  @Override
  public Expression generateExpressionFromValue(SymbolTable symbolTable, Object value) {
    BigInteger v = new BigInteger(value.toString());
    return new BitVectorLiteral(this, symbolTable, v);
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

  public Integer getSize() {
    return size;
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
