package AST.SymbolTable.Types.UserDefinedTypes;

import AST.Expressions.DClass.DClassLiteral;
import AST.Expressions.DClass.DClassValue;
import AST.Expressions.Expression;
import AST.Expressions.VariableExpression;
import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Generator.VariableNameGenerator;
import AST.StringUtils;
import AST.SymbolTable.Function.ClassFunction;
import AST.SymbolTable.Function.Function;
import AST.SymbolTable.Method.ClassMethod;
import AST.SymbolTable.Method.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import AST.SymbolTable.Types.Variables.VariableThis;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DClass implements UserDefinedType {

  private static final int MAX_NO_FIELDS = 3;
  private static final int MIN_NO_FIELDS = 1;
  private String name;
  private List<Type> typeList;
  private List<String> fieldNames;
  private List<Boolean> isConst;
  private List<Method> methods;
  private List<Function> functions;

  public DClass() {
    this(null, null, null, null);
  }

  public DClass(String name, List<Type> typeList, List<String> fieldNames, List<Boolean> isConst) {
    this.name = name;
    this.typeList = typeList;
    this.fieldNames = fieldNames;
    this.isConst = isConst;
    this.methods = new ArrayList<>();
    this.functions = new ArrayList<>();
  }

  @Override
  public Type concrete(SymbolTable symbolTable) {
    if (name == null) {
      name = VariableNameGenerator.generateDClassName();
    }
    if (typeList == null) {
      int noVarTypes = GeneratorConfig.getRandom().nextInt(MAX_NO_FIELDS) + MIN_NO_FIELDS;
      RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
      List<Type> varTypes = typeGenerator.generateTypesWithoutCurrent(noVarTypes, symbolTable,
        this);
      this.typeList = varTypes;
      fieldNames = varTypes.stream()
        .map((Type type) -> VariableNameGenerator.generateDClassFieldName(name, type))
        .collect(Collectors.toList());
      isConst = IntStream.range(0, noVarTypes)
        .mapToObj(x -> false)
        .collect(Collectors.toList());

      int noConstTypes = GeneratorConfig.getRandom().nextInt(MAX_NO_FIELDS) + MIN_NO_FIELDS;
      List<Type> constTypes = typeGenerator.generateTypesWithoutCurrent(noConstTypes, symbolTable,
        this);
      this.typeList.addAll(constTypes);
      fieldNames.addAll(constTypes.stream()
        .map((Type type) -> VariableNameGenerator.generateDClassFieldName(name, type))
        .collect(Collectors.toList()));
      isConst.addAll(IntStream.range(0, noConstTypes)
        .mapToObj(x -> true)
        .collect(Collectors.toList()));
    }
    return this;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Expression generateLiteral(SymbolTable symbolTable) {
    RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();
    List<Expression> values = new ArrayList<>();
    for (int i = 0; i < typeList.size(); i++) {
      Type concrete = typeList.get(i).concrete(symbolTable);
      values.add(expressionGenerator.generateExpression(concrete, symbolTable));
    }
    return new DClassLiteral(symbolTable, this, values);
  }

  @Override
  public Expression generateExpressionFromValue(SymbolTable symbolTable, Object value) {
    DClassValue vs = (DClassValue) value;
    Variable v = vs.getVariable();
    if (symbolTable.variableInScope(v)) {
      return new VariableExpression(symbolTable, v, this);
    }
    return null;
  }

  @Override
  public String formatPrint(Object object) {
    DClassValue value = (DClassValue) object;
    return value.getName();
  }

  @Override
  public boolean isPrintable() {
    return false;
  }

  @Override
  public String formatEnsures(String variableName, Object object) {
    if (typeList == null) {
      return null;
    }

    List<String> res = new ArrayList<>();
    DClassValue value = (DClassValue) object;

    for (int i = 0; i < typeList.size(); i++) {
      Type t = typeList.get(i);

      String field = fieldNames.get(i);
      Object v = t.of(value.get(i));

      String element = t.formatEnsures(String.format("(%s).%s", variableName, field), v);

      if (element == null) {
        return null;
      }
      res.add(element);

    }
    return String.join(" && ", res);
  }

  public Type getType(int i) {
    return typeList.get(i);
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
    if (!(other instanceof DClass)) {
      return false;
    }

    DClass dClass = other.asDClass();

    if (typeList == null || dClass.typeList == null || fieldNames == null
      || dClass.fieldNames == null) {
      return true;
    }

    if (typeList.size() != dClass.typeList.size()
      || fieldNames.size() != dClass.fieldNames.size()) {
      return false;
    }

    if (!name.equals(dClass.name)) {
      return false;
    }

    for (int i = 0; i < typeList.size(); i++) {
      Type type = typeList.get(i);
      Type otype = dClass.typeList.get(i);

      if (type != null && otype != null && !type.equals(otype)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean validMethodType() {
    return false;
  }

  @Override
  public boolean validFunctionType() {
    return false;
  }

  @Override
  public Boolean equal(Object lhsV, Object rhsV) {
    DClassValue lhsDV = (DClassValue) lhsV;
    DClassValue rhsDV = (DClassValue) rhsV;

    return lhsDV.getName().equals(rhsDV.getName()) &&
      Objects.equals(lhsDV.getContents(), rhsDV.getContents())
      && lhsDV.getNum() == rhsDV.getNum();

  }

  public String declaration() {
    StringBuilder res = new StringBuilder();
    res.append(String.format("class %s {\n", name));
    List<String> fieldDeclarations =
      IntStream.range(0, fieldNames.size())
        .mapToObj(i -> {
          String field = fieldNames.get(i);
          Type t = typeList.get(i);
          if (isConst.get(i)) {
            return String.format("const %s: %s", field, t.getVariableType());
          } else {
            return String.format("var %s: %s", field, t.getVariableType());
          }
        }).collect(Collectors.toList());

    res.append(StringUtils.intersperse("\n", StringUtils.indent(fieldDeclarations)) + "\n");

    res.append(StringUtils.indent(constructor()) + "\n");

    methods.stream()
      .map(m -> m.toCode(false))
      .forEach(s -> res.append(StringUtils.indent(s) + "\n"));

    functions.stream()
      .map(Function::toCode)
      .forEach(s -> res.append(StringUtils.indent(s) + "\n"));

    res.append("\n}");
    return res.toString();
  }

  private String constructor() {
    StringBuilder res = new StringBuilder();
    res.append(String.format("constructor (%s) {\n", IntStream.range(0, fieldNames.size())
      .mapToObj(i -> {
        String field = fieldNames.get(i);
        Type t = typeList.get(i);

        return String.format("%s: %s", field, t.getVariableType());
      }).collect(Collectors.joining(", "))));
    res.append(StringUtils.intersperse("\n", StringUtils.indent(fieldNames.stream()
      .map(f -> String.format("this.%s := %s;", f, f))
      .collect(Collectors.toList()))));
    res.append("\n}");
    return res.toString();
  }

  public List<String> getFieldNames() {
    return fieldNames;
  }

  public List<Type> getFieldTypes() {
    return typeList;
  }

  public VariableThis getThis() {
    return new VariableThis(this);
  }

  public void addMethod(ClassMethod method) {
    methods.add(method);
  }

  public void addFunction(ClassFunction function) {
    functions.add(function);
  }

  public List<Boolean> getIsConst() {
    return isConst;
  }
}
