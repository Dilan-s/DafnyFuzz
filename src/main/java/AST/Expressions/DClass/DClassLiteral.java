package AST.Expressions.DClass;

import AST.Expressions.BaseExpression;
import AST.Expressions.Expression;
import AST.Generator.GeneratorConfig;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.Statement;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.DClass;
import AST.SymbolTable.Types.Variables.Variable;
import AST.SymbolTable.Types.Variables.VariableClassIndex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DClassLiteral extends BaseExpression {

  private final Type type;
  private final SymbolTable symbolTable;

  private final Variable variable;
  private final List<Expression> values;
  private final Statement statement;

  private final List<List<Statement>> expanded;

  public DClassLiteral(SymbolTable symbolTable, Type type, List<Expression> values) {
    super();
    this.symbolTable = symbolTable;
    this.type = type;
    this.values = values;

    this.variable = new Variable(
      VariableNameGenerator.generateVariableValueName(type, symbolTable), type);
    this.statement = new AssignmentStatement(symbolTable, List.of(variable),
      new DClassInit(values));

    this.expanded = new ArrayList<>();
    values.forEach(v -> expanded.add(v.expand()));
    expanded.add(statement.expand());

    generateAssignments();
  }

  private void generateAssignments() {
    DClass t = this.type.asDClass();
    List<Type> fieldTypes = t.getFieldTypes();
    List<String> fieldNames = t.getFieldNames();
    List<Boolean> fieldIsConst = t.getIsConst();

    for (int i = 0; i < fieldTypes.size(); i++) {
      VariableClassIndex v = new VariableClassIndex(variable, fieldTypes.get(i), fieldNames.get(i),
        i);
      v.setDeclared();
      if (fieldIsConst.get(i)) {
        v.setConstant();
      }
      symbolTable.addVariable(v);
    }
  }

  @Override
  public List<Type> getTypes() {
    return List.of(type);
  }

  @Override
  public List<Statement> expand() {
    int i;
    for (i = 0; i < values.size(); i++) {
      Expression value = values.get(i);
      expanded.set(i, value.expand());
    }
    expanded.set(i, statement.expand());
    return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return variable.getName();
  }

  @Override
  protected List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s,
    boolean unused) {
    return variable.getValue(paramsMap);
  }

  @Override
  public boolean validForFunctionBody() {
    return false;
  }

  private class DClassInit extends BaseExpression {

    private final List<Expression> values;

    public DClassInit(List<Expression> values) {
      super();
      this.values = values;
    }

    @Override
    public List<Type> getTypes() {
      return List.of(type);
    }

    public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s,
      boolean unused) {
      List<Object> r = new ArrayList<>();

      List<Object> l = new ArrayList<>();
      for (Expression exp : values) {
        List<Object> value = exp.getValue(paramsMap, s);
        for (Object v : value) {
          if (v == null) {
            r.add(null);
            return r;
          }
          l.add(v);
        }
      }
      r.add(new DClassValue(variable, l));
      return r;
    }

    @Override
    public String toString() {
      String value = values.stream()
        .map(Expression::toString)
        .collect(Collectors.joining(", "));
      DClass t = type.asDClass();
      return String.format("new %s(%s)", t.getName(), value);
    }

    @Override
    public String minimizedTestCase() {
      String value = values.stream()
        .map(Expression::minimizedTestCase)
        .collect(Collectors.joining(", "));
      DClass t = type.asDClass();
      return String.format("new %s(%s)", t.getName(), value);
    }

    @Override
    public List<String> toOutput() {
      Set<String> res = new HashSet<>();
      List<String> temp = new ArrayList<>();

      DClass t = type.asDClass();
      res.add(String.format("new %s(", t.getName()));

      boolean first = true;
      for (Expression exp : values) {
        List<String> expOptions = exp.toOutput();
        temp = new ArrayList<>();
        for (String f : res) {
          for (String expOption : expOptions) {
            if (!first) {
              expOption = ", " + expOption;
            }
            String curr = f + expOption;
            temp.add(curr);
          }
        }
        if (expOptions.isEmpty()) {
          temp.addAll(res);
        }
        first = false;
        Collections.shuffle(temp, GeneratorConfig.getRandom());
        temp = temp.subList(0, Math.min(5, temp.size()));
        res = new HashSet<>(temp);
      }

      temp = new ArrayList<>();
      for (String f : res) {
        temp.add(f + ")");
      }
      res = new HashSet<>(temp);

      List<String> r = new ArrayList<>(res);
      Collections.shuffle(r, GeneratorConfig.getRandom());
      return r.subList(0, Math.min(5, res.size()));
    }

    @Override
    public List<Statement> expand() {
      return new ArrayList<>();
    }
  }
}