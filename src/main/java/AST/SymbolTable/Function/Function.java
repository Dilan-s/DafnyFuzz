package AST.SymbolTable.Function;

import AST.Expressions.Expression;
import AST.Expressions.Function.CallBaseFunctionExpression;
import AST.Expressions.Function.CallFunctionExpression;
import AST.Expressions.Variable.VariableFunctionExpression;
import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.VariableNameGenerator;
import AST.Statements.Statement;
import AST.StringUtils;
import AST.SymbolTable.Identifier;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.DCollectionTypes.DArray;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.DClass;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Function implements Identifier {

  private final String name;
  private final Type returnType;
  private final Variable returnArg;
  private final List<Variable> args;
  private final SymbolTable symbolTable;
  private final Set<String> requires;
  private final Set<String> ensures;
  private Expression body;
  private final Set<String> reads;

  private int noOfUses;

  public Function(String name, Type returnType, List<Variable> args, SymbolTable symbolTable) {
    this.name = name;
    this.returnType = returnType;
    this.args = args;
    this.symbolTable = symbolTable;

    this.requires = new HashSet<>();
    this.ensures = new HashSet<>();
    this.reads = args.stream()
      .filter(x -> x.getType().equals(new DArray()) || x.getType().equals(new DClass()))
      .map(Variable::getName)
      .collect(Collectors.toSet());

    this.noOfUses = 0;

    this.returnArg = new Variable(VariableNameGenerator.generateReturnVariableName(getName()),
      returnType);
  }

  public void setBody(Expression body) {
    this.body = body;
  }

  public int getNoOfUses() {
    return noOfUses;
  }

  public void incrementUse() {
    this.noOfUses++;
  }

  public SymbolTable getSymbolTable() {
    return symbolTable;
  }

  public List<Variable> getArgs() {
    return args;
  }

  public List<Type> getArgTypes() {
    return args.stream().map(Variable::getType).collect(Collectors.toList());
  }

  public Type getReturnType() {
    return returnType;
  }

  @Override
  public String getName() {
    return name;
  }

  public String minimizedTestCase() {
    List<String> code = new ArrayList<>();

    code.add(declarationLine());

    for (Statement statement : body.expand()) {
      code.add(StringUtils.indent(statement.minimizedTestCase()));
    }

    code.add(StringUtils.indent(body.minimizedTestCase()));
    code.add("}\n");
    return String.join("\n", code);
  }

  public String toCode() {
    List<String> code = new ArrayList<>();

    code.add(declarationLine());

    for (Statement statement : body.expand()) {
      code.add(StringUtils.indent(statement.toString()));
    }

    code.add(StringUtils.indent(body.toString()));
    code.add("}\n");
    return String.join("\n", code);
  }

  @Override
  public String toString() {
    return toCode();
  }

  public List<String> toOutput() {
    Set<String> res = new HashSet<>();
    List<String> temp = new ArrayList<>();

    res.add(declarationLine() + "\n");

    for (Statement statement : body.expand()) {
      temp = new ArrayList<>();
      for (String s : statement.toOutput()) {
        for (String f : res) {
          String curr = f;
          curr = curr + StringUtils.indent(s) + "\n";
          temp.add(curr);
        }
      }

      if (temp.isEmpty()) {
        temp.addAll(res);
      }

      Collections.shuffle(temp, GeneratorConfig.getRandom());
      res = new HashSet<>(temp.subList(0, Math.min(5, temp.size())));
    }

    temp = new ArrayList<>();
    for (String s : body.toOutput()) {
      for (String f : res) {
        String curr = f;
        curr = curr + StringUtils.indent(s) + "\n}\n\n";
        temp.add(curr);
      }
    }
    if (temp.isEmpty()) {
      temp.addAll(res);
    }

    Collections.shuffle(temp, GeneratorConfig.getRandom());
    res = new HashSet<>(temp.subList(0, Math.min(5, temp.size())));

    List<String> r = new ArrayList<>(res);
    return r;
  }


  public String declarationLine() {
    String arguments = getArgs().stream()
      .map(Variable::toString)
      .collect(Collectors.joining(", "));

    String res = String.format("function %s(%s): (%s: %s)\n", getName(), arguments,
      returnArg.getName(), returnType.getVariableType());

    if (!requires.isEmpty()) {
      res =
        res + StringUtils.indent("requires " + StringUtils.intersperse(" || ", requires)) + "\n";
    }
    if (!ensures.isEmpty()) {
      res = res + StringUtils.indent("ensures " + StringUtils.intersperse(" && ", ensures)) + "\n";
    }
    if (!reads.isEmpty()) {
      res = res + StringUtils.indent("reads " + StringUtils.intersperse(", ", reads)) + "\n";
    }

    res = res + "{";
    return res;
  }

  public void addFunction(Function function) {
    symbolTable.addFunction(function);
  }

  public List<Object> execute(List<Variable> params) {
    return execute(params, new StringBuilder());
  }

  public List<Object> execute(List<Variable> params, StringBuilder s) {
    incrementUse();
    Map<Variable, Variable> requiresEnsures = new HashMap<>();
    Map<Variable, Variable> paramMap = new HashMap<>();
    for (int i = 0, argsSize = args.size(); i < argsSize; i++) {
      Variable arg = args.get(i);
      Variable param = params.get(i);
      paramMap.put(arg, param);
      requiresEnsures.put(arg, param);
    }

    for (Statement st : body.expand()) {
      st.execute(paramMap, s);
    }

    List<Object> execute = body.getValue(paramMap, s);

    if (!getName().startsWith("safe") && !getName().equals("Main")) {
      addRequires(requiresEnsures);
      addEnsures(requiresEnsures, execute);
    }
    return execute;
  }

  private void addEnsures(Map<Variable, Variable> requiresEnsures, List<Object> execute) {
    List<String> clausesRequires = getRequiresClauses(requiresEnsures);
    if (clausesRequires == null) {
      return;
    }

    List<String> clausesEnsures = getEnsuresClauses(execute);

    String res = "";

    if (!clausesEnsures.isEmpty()) {

      if (!clausesRequires.isEmpty()) {
        res = res + "(" + String.join(" && ", clausesRequires) + ") ==> ";
      }

      res = res + "(" + String.join(" && ", clausesEnsures) + ")";
    }

    if (!res.isEmpty()) {
      ensures.add("(" + res + ")");
    }


  }

  private void addRequires(Map<Variable, Variable> requiresEnsures) {
    List<String> clausesRequires = getRequiresClauses(requiresEnsures);
    if (clausesRequires == null) {
      return;
    }

    if (!clausesRequires.isEmpty()) {
      String res = "(" + String.join(" && ", clausesRequires) + ")";
      requires.add(res);
    }
  }

  private List<String> getEnsuresClauses(List<Object> execute) {
    List<String> ensures = new ArrayList<>();

    Object o = execute.get(0);

    String variable = returnArg.getName();
    String v = returnArg.getType().formatEnsures(variable, o);

    ensures.add(v);

    return ensures;
  }

  protected List<String> getRequiresClauses(Map<Variable, Variable> requiresEnsures) {
    List<String> clauses = new ArrayList<>();

    for (Map.Entry<Variable, Variable> entry : requiresEnsures.entrySet()) {
      Variable key = entry.getKey();
      Variable value = entry.getValue();

      String variable = key.getName();
      String v = value.getType().formatEnsures(variable, value.getValue(requiresEnsures).get(0));

      if (v == null) {
        return null;
      }

      clauses.add(v);
    }
    return clauses;
  }

  public void assignThis(Variable classVariable) {
  }

  public CallFunctionExpression generateCall(SymbolTable symbolTable) {
    RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();

    List<Type> argTypes = getArgTypes();
    List<Expression> args = new ArrayList<>();

    for (Type t : argTypes) {
      Type concrete = t.concrete(symbolTable);
      Expression exp = expressionGenerator.generateExpression(concrete, symbolTable);
      args.add(exp);
    }

    CallFunctionExpression expression = new CallBaseFunctionExpression(symbolTable, this, args);

    return expression;
  }

  public Expression generateFunctionVariable(SymbolTable symbolTable, Type type) {
    return new VariableFunctionExpression(symbolTable, this, type);
  }
}