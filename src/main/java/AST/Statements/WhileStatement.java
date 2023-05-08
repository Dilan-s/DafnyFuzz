package AST.Statements;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.Expression;
import AST.StringUtils;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WhileStatement extends BaseStatement {

    private SymbolTable symbolTable;
    private Variable loopVar;
    private Variable finalVar;
    private Statement initAssign;
    private Statement finalAssign;
    private Expression test;
    private Statement body;

    private List<List<Statement>> expanded;

    public WhileStatement(SymbolTable symbolTable, Variable loopVar, Variable finalVar, Statement initAssign, Statement finalAssign, Expression test, Statement body) {
        this.symbolTable = symbolTable;
        this.loopVar = loopVar;
        this.finalVar = finalVar;
        this.initAssign = initAssign;
        this.finalAssign = finalAssign;
        this.test = test;
        this.body = body;

        this.expanded = new ArrayList<>();
        expanded.add(initAssign.expand());
        expanded.add(finalAssign.expand());
        expanded.add(test.expand());
        expanded.add(List.of(this));
    }

    @Override
    protected List<Object> execute(Map<Variable, Variable> paramMap, StringBuilder s, boolean unused) {
        while (true) {
            Object testValue = test.getValue(paramMap, s).get(0);

            if (testValue != null) {
                Boolean testValueB = (Boolean) testValue;
                if (testValueB) {
                    List<Object> execute = body.execute(paramMap, s);
                    if (execute != null) {
                        return execute;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    @Override
    public List<Statement> expand() {
        if (initAssign.requireUpdate()) {
            expanded.set(0, initAssign.expand());
        }
        if (finalAssign.requireUpdate()) {
            expanded.set(1, finalAssign.expand());
        }
        if (test.requireUpdate()) {
            expanded.set(2, test.expand());
        }

        return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public List<String> toOutput() {
        Set<String> res = new HashSet<>();

        List<String> testOptions = test.toOutput();
        for (String testOption : testOptions) {
            res.add(String.format("while %s \n\tdecreases %s - %s;\n\tinvariant %s <= %s;\n{\n", testOption, finalVar.getName(), loopVar.getName(), loopVar.getName(), finalVar.getName()));
        }

        List<String> temp = new ArrayList<>();
        List<String> bodyOptions = body.toOutput();
        for (String f : res) {
            for (String bodyOption : bodyOptions) {
                String curr = f + StringUtils.indent(bodyOption);
                temp.add(curr);
            }
        }
        if (bodyOptions.isEmpty()) {
            temp.addAll(res);
        }

        res = new HashSet<>(temp);
        temp = new ArrayList<>();
        for (String f : res) {
            String curr = f + "\n}";
            temp.add(curr);
        }

        res = new HashSet<>(temp);

        List<String> r = new ArrayList<>(res);
        Collections.shuffle(r, GeneratorConfig.getRandom());
        return r.subList(0, Math.min(5, r.size()));
    }

    @Override
    public String minimizedTestCase() {
        if (body.getNoOfUses() > 0) {
            String res = String.format("while %s \n\tdecreases %s - %s;\n\tinvariant %s <= %s;\n{\n", test,
                finalVar.getName(), loopVar.getName(), loopVar.getName(), finalVar.getName());
            res = res + StringUtils.indent(body.minimizedTestCase());
            res = res + "\n}";

            return res;
        }
        return "";
    }

    @Override
    public boolean requireUpdate() {
        return initAssign.requireUpdate() || test.requireUpdate() || body.requireUpdate();
    }

    @Override
    public String toString() {
        String res = String.format("while %s \n\tdecreases %s - %s;\n\tinvariant %s <= %s;\n{\n", test, finalVar.getName(), loopVar.getName(), loopVar.getName(), finalVar.getName());
        res = res + StringUtils.indent(body.toString());
        res = res + "\n}";

        return res;
    }
}
