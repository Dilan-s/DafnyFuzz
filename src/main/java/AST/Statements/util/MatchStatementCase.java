package AST.Statements.util;

import AST.Generator.GeneratorConfig;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.BaseStatement;
import AST.Statements.Expressions.Expression;
import AST.Statements.Statement;
import AST.StringUtils;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MatchStatementCase extends BaseStatement {

    private SymbolTable symbolTable;
    private Expression test;
    private Statement body;

    private List<List<Statement>> expanded;
    private AssignmentStatement testAssign;
    private Variable testVar;

    public MatchStatementCase(SymbolTable symbolTable, Expression test, Statement body) {
        this.symbolTable = symbolTable;
        this.test = test;
        this.body = body;

        this.expanded = new ArrayList<>();
        if (test != null) {
            Type type = test.getTypes().get(0);
            testVar = new Variable(VariableNameGenerator.generateVariableValueName(type, symbolTable), type);
            testAssign = new AssignmentStatement(symbolTable, List.of(testVar), test);
            expanded.add(testAssign.expand());
        }
    }

    public MatchStatementCase(SymbolTable symbolTable, Statement body) {
        this(symbolTable, null, body);
    }

    @Override
    public List<Object> execute(Map<Variable, Variable> paramMap, StringBuilder s) {
        super.incrementUse();
        List<Object> execute = body.execute(paramMap, s);
        return execute;
    }

    public Expression getTest() {
        return test;
    }

    public Statement getBody() {
        return body;
    }

    @Override
    public List<Statement> expand() {
        if (test != null && testAssign.requireUpdate()) {
            expanded.set(0, testAssign.expand());
        }
        return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public boolean isReturn() {
        return body.isReturn();
    }

    @Override
    public boolean requireUpdate() {
        return test != null && testAssign.requireUpdate();
    }

    @Override
    public String toString() {

        String res = String.format("case %s => {\n", test == null ? "_" : testVar.getName());
        res = res + StringUtils.indent(body.toString());
        res = res + "\n}\n";

        return res;
    }

    @Override
    public String minimizedTestCase() {

        String res = String.format("case %s => {\n", test == null ? "_" : testVar.getName());
        res = res + StringUtils.indent(body.minimizedTestCase());
        res = res + "\n}\n";

        return res;
    }

    @Override
    public List<String> toOutput() {
        Set<String> res = new HashSet<>();
        List<String> temp = new ArrayList<>();

        String testName;
        if (test != null) {
            testName = testVar.getName();
        } else {
            testName = "_";
        }
        res.add(String.format("case %s => {\n", testName));

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

        res = new HashSet(temp);

        List<String> r = new ArrayList<>(res);
        Collections.shuffle(r, GeneratorConfig.getRandom());
        res = new HashSet<>(r.subList(0, Math.min(5, r.size())));

        temp = new ArrayList<>();
        for (String f : res) {
            temp.add(f + "\n}");
        }

        res = new HashSet(temp);

        r = new ArrayList<>(res);
        Collections.shuffle(r, GeneratorConfig.getRandom());
        return r.subList(0, Math.min(5, r.size()));
    }
}
