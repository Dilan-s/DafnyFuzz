package AST.SymbolTable.Types.UserDefinedTypes;

import AST.Expressions.Expression;
import AST.Expressions.Variable.FunctionVariableValue;
import AST.Expressions.Variable.VariableFunctionExpression;
import AST.Generator.GeneratorConfig;
import AST.Generator.RandomFunctionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.SymbolTable.Function.Function;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.List;
import java.util.stream.Collectors;

public class ArrowType implements UserDefinedType {

  public static final int MAX_SIZE_TYPES = 2;
  public static final int MIN_SIZE_TYPES = 2;

  private List<Type> fromTypes;
  private Type toType;

  public ArrowType() {
    this(null, null);
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
      .map(Type::getName)
      .collect(Collectors.joining(", ")), toType.getName());
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
    Function function = functionGenerator.generateBaseFunction(toType, symbolTable, fromTypes);
    Expression expression = new VariableFunctionExpression(symbolTable, function, this);
    return expression;
  }

  @Override
  public Expression generateExpressionFromValue(SymbolTable symbolTable, Object value) {
    return null;
  }

  @Override
  public String formatPrint(Object object) {
    FunctionVariableValue fValue = (FunctionVariableValue) object;
    return fValue.getFunction().getName();
  }

  @Override
  public String formatEnsures(String variableName, Object object) {
    FunctionVariableValue fValue = (FunctionVariableValue) object;
    return String.format("%s == %s", variableName, fValue.getFunction().getName());
  }

  @Override
  public boolean isPrintable() {
    return false;
  }

  @Override
  public Boolean equal(Object lhsV, Object rhsV) {
    FunctionVariableValue fValueLhs = (FunctionVariableValue) lhsV;
    FunctionVariableValue fValueRhs = (FunctionVariableValue) rhsV;

    return fValueLhs.getFunction() == fValueRhs.getFunction();
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

    if (fromTypes == null || toType == null || arrowTypeOther.fromTypes == null || arrowTypeOther.toType == null) {
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
}
