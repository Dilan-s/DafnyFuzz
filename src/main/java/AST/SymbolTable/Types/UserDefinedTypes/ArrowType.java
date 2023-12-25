package AST.SymbolTable.Types.UserDefinedTypes;

import AST.Expressions.Expression;
import AST.Expressions.Variable.Function.FunctionValue;
import AST.Generator.GeneratorConfig;
import AST.Generator.RandomFunctionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.SymbolTable.Function.Function;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArrowType implements UserDefinedType {

  public static final int MAX_SIZE_TYPES = 2;
  public static final int MIN_SIZE_TYPES = 2;

  private List<Type> fromTypes;
  private Type toType;

  public ArrowType() {
    this(null, null);
  }

  public ArrowType(Type toType) {
    this(null, toType);
  }

  public ArrowType(List<Type> fromTypes, Type toType) {
    this.fromTypes = fromTypes;
    this.toType = toType;
  }

  @Override
  public boolean validMethodType() {
    return true;
  }

  @Override
  public boolean validForFunctionBody() {
    return fromTypes.stream().allMatch(Type::validForFunctionBody) && toType.validForFunctionBody();
  }

  @Override
  public String getName() {
    return String.format("%s_TO_%s", fromTypes.stream()
      .map(Type::getName)
      .collect(Collectors.joining("_")), toType.getName());
  }

  @Override
  public String getVariableType() {
    return String.format("(%s)->(%s)", fromTypes.stream()
      .map(Type::getVariableType)
      .collect(Collectors.joining(", ")), toType.getVariableType());
  }

  @Override
  public Type concrete(SymbolTable symbolTable) {
    RandomTypeGenerator typeGenerator = new RandomTypeGenerator();

    if (fromTypes == null) {
      int noOfArgs = GeneratorConfig.getRandom().nextInt(MAX_SIZE_TYPES) + MIN_SIZE_TYPES;
      fromTypes = typeGenerator.generateFunctionTypes(noOfArgs, symbolTable);
    }
    if (toType == null) {
      toType = typeGenerator.generateFunctionTypes(1, symbolTable).get(0);
    }

    return new ArrowType(fromTypes, toType);
  }

  @Override
  public Expression generateLiteral(SymbolTable symbolTable) {
    RandomFunctionGenerator functionGenerator = new RandomFunctionGenerator();
    Function function = functionGenerator.generateFunction(toType, symbolTable, fromTypes);
    Expression expression = function.generateFunctionVariable(symbolTable, this);
    return expression;
  }

  @Override
  public Expression generateExpressionFromValue(SymbolTable symbolTable, Object value) {
    return null;
  }

  @Override
  public String formatPrint(Object object) {
    FunctionValue fValue = (FunctionValue) object;
    return fValue.getFunctionName();
  }

  @Override
  public String formatEnsures(String variableName, Object object) {
    return "true";
  }

  @Override
  public boolean isPrintable() {
    return false;
  }

  @Override
  public Boolean equal(Object lhsV, Object rhsV) {
    FunctionValue fValueLhs = (FunctionValue) lhsV;
    FunctionValue fValueRhs = (FunctionValue) rhsV;

    return Objects.equals(fValueLhs.getFunctionName(), fValueRhs.getFunctionName());
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Type)) {
      return false;
    }
    Type other = (Type) obj;
    if (!(other instanceof ArrowType)) {
      return false;
    }

    ArrowType arrowTypeOther = other.asArrowType();

    if (fromTypes == null || toType == null || arrowTypeOther.fromTypes == null
      || arrowTypeOther.toType == null) {
      return true;
    }

    if (arrowTypeOther.fromTypes.size() != fromTypes.size()) {
      return false;
    }

    for (int i = 0; i < fromTypes.size(); i++) {
      Type type = fromTypes.get(i);
      Type otype = arrowTypeOther.fromTypes.get(i);

      if (type != null && otype != null && !type.equals(otype)) {
        return false;
      }
    }

    return arrowTypeOther.toType.equals(toType);
  }

  public List<Type> getFromTypes() {
    return fromTypes;
  }

  public Type getToType() {
    return toType;
  }
}
