package AST.SymbolTable.Types.UserDefinedTypes;

import AST.Expressions.Expression;
import AST.Generator.RandomTypeGenerator;
import AST.Generator.VariableNameGenerator;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.DCollectionTypes.DArray;
import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Types.DCollectionTypes.DSet;
import AST.SymbolTable.Types.DCollectionTypes.Multiset;
import AST.SymbolTable.Types.DCollectionTypes.Seq;
import AST.SymbolTable.Types.DMap.DMap;
import AST.SymbolTable.Types.GenericType.GenericType;
import AST.SymbolTable.Types.PrimitiveTypes.BaseType;
import AST.SymbolTable.Types.PrimitiveTypes.BitVector;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.Types.PrimitiveTypes.Char;
import AST.SymbolTable.Types.PrimitiveTypes.DString;
import AST.SymbolTable.Types.PrimitiveTypes.Int.Int;
import AST.SymbolTable.Types.PrimitiveTypes.Real;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.DataType.DataType;
import AST.SymbolTable.Types.UserDefinedTypes.DataType.DataTypeRule;
import java.math.BigInteger;
import java.util.Objects;

public class TypeAlias implements Type {

  private Type type;
  private String name;

  public TypeAlias() {
    this(null, null);
  }

  public TypeAlias(Type type, String name) {
    this.type = type;
    this.name = name;
  }

  @Override
  public Type concrete(SymbolTable symbolTable) {
    if (type == null) {
      if (name == null) {
        name = VariableNameGenerator.generateTypeAliasName();
      }

      RandomTypeGenerator randomTypeGenerator = new RandomTypeGenerator();
      this.type = randomTypeGenerator.generateTypeAliasType(symbolTable)
        .concrete(symbolTable);
    }

    return new TypeAlias(type, name);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Type)) {
      return false;
    }
    Type other = (Type) obj;

    if (other instanceof TypeAlias) {
      TypeAlias o = other.asTypeAlias();
      return this.name == null || o.name == null || this.name.equals(o.name)
        || this.type == null || o.type == null;
    }

    if (this.type == null) {
      return true;
    }

    return other.equals(this.type);
  }

  @Override
  public Expression generateLiteral(SymbolTable symbolTable) {
    return type.generateLiteral(symbolTable);
  }

  @Override
  public Expression generateExpressionFromValue(SymbolTable symbolTable, Object value) {
    return type.generateExpressionFromValue(symbolTable, value);
  }

  @Override
  public boolean operatorExists() {
    return type.operatorExists();
  }

  @Override
  public boolean isPrintable() {
    return type.isPrintable();
  }

  @Override
  public boolean isCollection() {
    return type.isCollection();
  }

  @Override
  public Boolean lessThan(Object lhsV, Object rhsV) {
    return type.lessThan(lhsV, rhsV);
  }

  @Override
  public Boolean equal(Object lhsV, Object rhsV) {
    return type.equal(lhsV, rhsV);
  }

  @Override
  public Boolean lessThanOrEqual(Object lhsV, Object rhsV) {
    return type.lessThanOrEqual(lhsV, rhsV);
  }

  @Override
  public Boolean greaterThan(Object lhsV, Object rhsV) {
    return type.greaterThan(lhsV, rhsV);
  }

  @Override
  public Boolean greaterThanOrEqual(Object lhsV, Object rhsV) {
    return type.greaterThanOrEqual(lhsV, rhsV);
  }

  @Override
  public BigInteger cardinality(Object value) {
    return type.cardinality(value);
  }

  @Override
  public String concatenate(Object lhsV, Object rhsV) {
    return type.concatenate(lhsV, rhsV);
  }

  @Override
  public String formatPrint(Object object) {
    return type.formatPrint(object);
  }

  @Override
  public String formatEnsures(String variableName, Object object) {
    return type.formatEnsures(variableName, object);
  }

  @Override
  public boolean validMethodType() {
    return type.validMethodType();
  }


  @Override
  public Object of(Object value) {
    return type.of(value);
  }

  @Override
  public boolean isOrdered() {
    return type.isOrdered();
  }

  public String declaration() {
    return String.format("type %s = %s", name, type.getVariableType());
  }

  @Override
  public UserDefinedType asUserDefinedType() {
    return this.type.asUserDefinedType();
  }

  @Override
  public Tuple asTuple() {
    return this.type.asTuple();
  }

  @Override
  public DataType asDataType() {
    return this.type.asDataType();
  }

  @Override
  public DataTypeRule asDataTypeRule() {
    return this.type.asDataTypeRule();
  }

  @Override
  public BaseType asBaseType() {
    return this.type.asBaseType();
  }

  @Override
  public Real asReal() {
    return this.type.asReal();
  }

  @Override
  public Int asInt() {
    return this.type.asInt();
  }

  @Override
  public DString asDString() {
    return this.type.asDString();
  }

  @Override
  public Char asChar() {
    return this.type.asChar();
  }

  @Override
  public Bool asBool() {
    return this.type.asBool();
  }

  @Override
  public GenericType asGenericType() {
    return this.type.asGenericType();
  }

  @Override
  public DMap asDMap() {
    return this.type.asDMap();
  }

  @Override
  public DCollection asDCollection() {
    return this.type.asDCollection();
  }

  @Override
  public DArray asDArray() {
    return this.type.asDArray();
  }

  @Override
  public DSet asDSet() {
    return this.type.asDSet();
  }

  @Override
  public Multiset asMultiset() {
    return this.type.asMultiset();
  }

  @Override
  public Seq asSeq() {
    return this.type.asSeq();
  }

  @Override
  public DClass asDClass() {
    return this.type.asDClass();
  }

  @Override
  public String formatEnsures(Object object) {
    return type.formatEnsures(object);
  }

  @Override
  public ArrowType asArrowType() {
    return type.asArrowType();
  }

  @Override
  public BitVector asBitVector() {
    return type.asBitVector();
  }

  @Override
  public boolean validForFunctionBody() {
    return type.validForFunctionBody();
  }

  @Override
  public boolean isTypeAlias() {
    return type == null || type.isTypeAlias();
  }
}
