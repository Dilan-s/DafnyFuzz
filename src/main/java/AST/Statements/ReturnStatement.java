package AST.Statements;

import AST.Expressions.Expression;
import AST.Generator.GeneratorConfig;
import AST.Statements.util.PrintAll;
import AST.Statements.util.ReturnStatus;
import AST.StringUtils;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReturnStatement extends BaseStatement {

  private final SymbolTable symbolTable;
  private final List<Expression> values;
  private final boolean printAll;
  private final PrintAll printScope;
  private final List<List<Statement>> expanded;

  public ReturnStatement(SymbolTable symbolTable, List<Expression> values, boolean printAll) {
    super();
    this.symbolTable = symbolTable;
    this.values = values;
    this.printAll = printAll;
    this.printScope = new PrintAll(symbolTable);

    expanded = values.stream()
      .map(Expression::expand)
      .collect(Collectors.toList());
    expanded.add(printAll ? printScope.expand() : new ArrayList<>());
    expanded.add(List.of(this));
  }

  public ReturnStatement(SymbolTable symbolTable, List<Expression> values) {
    this(symbolTable, values, true);
  }

  @Override
  public boolean isReturn() {
    return true;
  }

  @Override
  public String toString() {
    List<String> code = new ArrayList<>();

    String returnValues = values.stream()
      .map(Expression::toString)
      .collect(Collectors.joining(", "));

    code.add(String.format("return %s;", returnValues));
    return StringUtils.intersperse("\n", code);
  }

  @Override
  public String minimizedTestCase() {
    List<String> code = new ArrayList<>();

    String returnValues = values.stream()
      .map(Expression::minimizedTestCase)
      .collect(Collectors.joining(", "));

    code.add(String.format("return %s;", returnValues));
    return StringUtils.intersperse("\n", code);
  }

  @Override
  public List<String> toOutput() {
    Set<String> res = new HashSet<>();
    List<String> temp = new ArrayList<>();

    res.add("return ");

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
      res = new HashSet<>(temp);
    }

    temp = new ArrayList<>();
    for (String f : res) {
      temp.add(f + ";");
    }
    res = new HashSet<>(temp);

    List<String> r = new ArrayList<>(res);
    Collections.shuffle(r, GeneratorConfig.getRandom());
    return r.subList(0, Math.min(5, res.size()));
  }

  public List<Expression> getReturnValues() {
    return values;
  }

  @Override
  protected ReturnStatus execute(Map<Variable, Variable> paramMap, StringBuilder s,
    boolean unused) {
    List<Object> list = new ArrayList<>();
    for (Expression x : values) {
      List<Object> value = x.getValue(paramMap, s);
      list.addAll(value);
    }
    ReturnStatus returnStatus = ReturnStatus.returnValues(list);
    return returnStatus;
  }

  @Override
  public List<Statement> expand() {
    int i;
    for (i = 0; i < values.size(); i++) {
      Expression exp = values.get(i);
      expanded.set(i, exp.expand());
    }
    expanded.set(i, printAll ? printScope.expand() : new ArrayList<>());
    return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
  }

  @Override
  public boolean validForFunctionBody() {
    return false;
  }
}
