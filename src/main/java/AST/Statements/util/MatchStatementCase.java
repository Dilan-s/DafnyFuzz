package AST.Statements.util;

import AST.Expressions.Expression;
import AST.Generator.GeneratorConfig;
import AST.Statements.AssignmentStatement;
import AST.Statements.BaseStatement;
import AST.Statements.Statement;
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

public class MatchStatementCase extends BaseStatement {

  private final SymbolTable symbolTable;
  private final Expression test;
  private final Statement body;

  private final List<List<Statement>> expanded;
  private AssignmentStatement testAssign;
  private Variable testVar;

  public MatchStatementCase(SymbolTable symbolTable, Expression test, Statement body) {
    this.symbolTable = symbolTable;
    this.test = test;
    this.body = body;

    this.expanded = new ArrayList<>();
    expanded.add(null != test ? test.expand() : new ArrayList<>());
  }

  public MatchStatementCase(SymbolTable symbolTable, Statement body) {
    this(symbolTable, null, body);
  }

  @Override
  protected ReturnStatus execute(Map<Variable, Variable> paramMap, StringBuilder s,
    boolean unused) {
    ReturnStatus execute = body.execute(paramMap, s);
    return execute;
  }

  @Override
  public Set<Variable> getModifies() {
    return body.getModifies();
  }

  public Expression getTest() {
    return test;
  }

  public Statement getBody() {
    return body;
  }

  @Override
  public List<Statement> expand() {
    expanded.set(0, null != test ? test.expand() : new ArrayList<>());
    return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
  }

  @Override
  public boolean isReturn() {
    return body.isReturn();
  }

  @Override
  public String toString() {

    String res = String.format("case %s => {\n", test == null ? "_" : test.toString());
    res = res + StringUtils.indent(body.toString());
    res = res + "\n}\n";

    return res;
  }

  @Override
  public String minimizedTestCase() {

    String res = String.format("case %s => {\n", test == null ? "_" : test.toString());
    res = res + StringUtils.indent(body.minimizedTestCase());
    res = res + "\n}\n";

    return res;
  }

  @Override
  public Map<String, String> invalidValidationTests() {
    Map<String, String> m = body.invalidValidationTests();
    return m;
  }

  @Override
  public List<String> toOutput() {
    Set<String> res = new HashSet<>();
    List<String> temp = new ArrayList<>();

    List<String> testName;
    if (test != null) {
      testName = test.toOutput();
    } else {
      testName = List.of("_");
    }
    for (String s : testName) {
      res.add(String.format("case %s => {\n", s));
    }
    temp = new ArrayList<>();
    List<String> bodyOptions = body.toOutput();
    for (String f : res) {
      for (String bodyOption : bodyOptions) {
        String curr = StringUtils.indent(bodyOption);
        temp.add(f + curr);
      }
    }
    if (bodyOptions.isEmpty()) {
      temp.addAll(res);
    }

    res = new HashSet<>(temp);

    List<String> r = new ArrayList<>(res);
    Collections.shuffle(r, GeneratorConfig.getRandom());
    res = new HashSet<>(r.subList(0, Math.min(5, r.size())));

    temp = new ArrayList<>();
    for (String f : res) {
      temp.add(f + "\n}");
    }

    res = new HashSet<>(temp);

    r = new ArrayList<>(res);
    Collections.shuffle(r, GeneratorConfig.getRandom());
    return r.subList(0, Math.min(5, r.size()));
  }
}
