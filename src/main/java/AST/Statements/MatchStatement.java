package AST.Statements;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.Expression;
import AST.Statements.util.MatchStatementCase;
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

public class MatchStatement extends BaseStatement {

    private final SymbolTable symbolTable;
    private Expression test;
    private List<MatchStatementCase> cases;
    private List<MatchStatementCase> distinctCases;
    private MatchStatementCase defaultCase;

    private List<List<Statement>> expanded;

    public MatchStatement(SymbolTable symbolTable, Expression test, List<MatchStatementCase> cases, MatchStatementCase defaultCase) {
        super();
        this.symbolTable = symbolTable;
        this.test = test;
        this.cases = cases;
        this.defaultCase = defaultCase;
        this.distinctCases = new ArrayList<>(cases);

        this.expanded = new ArrayList<>();

        this.expanded.add(test.expand());
        cases.forEach(c -> expanded.add(c.expand()));
        expanded.add(defaultCase.expand());
        expanded.add(List.of(this));
    }

    @Override
    public boolean isReturn() {
        return distinctCases.stream().allMatch(Statement::isReturn) && defaultCase.isReturn();
    }

    @Override
    public List<Object> execute(Map<Variable, Variable> paramMap, StringBuilder s) {
        super.incrementUse();
        Object testValue = test.getValue(paramMap).get(0);
        if (testValue == null) {
            return null;
        }

        List<MatchStatementCase> cases = new ArrayList<>(this.distinctCases);
        Set<Object> testValues = new HashSet<>();

        for (int i = 0; i < cases.size(); i++){
            MatchStatementCase matchStatementCase = cases.get(i);

            Object castTestValue = matchStatementCase.getTest().getValue(paramMap).get(0);
            if (castTestValue == null) {
                return null;
            }

            if (testValues.contains(castTestValue)) {
//                distinctCases.remove(matchStatementCase);
                continue;
            }
            testValues.add(castTestValue);

            if (castTestValue.equals(testValue)) {
                List<Object> execute = matchStatementCase.execute(paramMap, s);
                for (int j = i + 1; j < cases.size(); j++) {
                    matchStatementCase = cases.get(j);

                    castTestValue = matchStatementCase.getTest().getValue(paramMap).get(0);
                    if (castTestValue == null) {
                        return null;
                    }

                    if (testValues.contains(castTestValue)) {
//                        distinctCases.remove(matchStatementCase);
                    } else {
                        testValues.add(castTestValue);
                    }
                }
                return execute;
            }
        }

        return defaultCase.execute(paramMap, s);
    }

    @Override
    public List<Statement> expand() {
        if (test.requireUpdate()) {
            expanded.set(0, test.expand());
        }
        int j = 1;
        int i;
        for (i = 0; i < cases.size(); i++) {
            MatchStatementCase c = cases.get(i);
            if (c.requireUpdate()) {
                expanded.set(j, c.expand());
            }
            j++;
        }
        if (defaultCase.requireUpdate()) {
            expanded.set(j, defaultCase.expand());
        }
        return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public boolean requireUpdate() {
        return cases.stream().anyMatch(Statement::requireUpdate) || defaultCase.requireUpdate();
    }

    @Override
    public List<String> toOutput() {
        Set<String> res = new HashSet<>();
        List<String> temp = new ArrayList<>();

        List<String> testOutputs = test.toOutput();
        for (String testOutput : testOutputs) {
            res.add(String.format("match %s {\n", testOutput));
        }

        for (MatchStatementCase matchStatementCase : distinctCases) {
            List<String> caseOptions = matchStatementCase.toOutput();
            temp = new ArrayList<>();
            for (String f : res) {
                for (String caseOption : caseOptions) {
                    String curr = StringUtils.indent(caseOption);
                    curr = f + curr + "\n";
                    temp.add(curr);
                }
            }
            if (caseOptions.isEmpty()) {
                temp.addAll(res);
            }

            res = new HashSet(temp);

            List<String> r = new ArrayList<>(res);
            Collections.shuffle(r, GeneratorConfig.getRandom());
            res = new HashSet<>(r.subList(0, Math.min(5, r.size())));
        }

        temp = new ArrayList<>();
        List<String> defaultOptions = defaultCase.toOutput();
        for (String f : res) {
            for (String defaultOption : defaultOptions) {
                String curr = StringUtils.indent(defaultOption);
                curr = f + curr;
                temp.add(curr);
            }
        }
        if (defaultOptions.isEmpty()) {
            temp.addAll(res);
        }

        res = new HashSet(temp);


        temp = new ArrayList<>();
        for (String f : res) {
            temp.add(f + "}\n");
        }

        res = new HashSet(temp);

        List<String> r = new ArrayList<>(res);
        Collections.shuffle(r, GeneratorConfig.getRandom());
        return r.subList(0, Math.min(5, r.size()));
    }

    @Override
    public String minimizedTestCase() {
        List<MatchStatementCase> usedCases = new ArrayList<>();
        distinctCases.stream().filter(c -> c.getNoOfUses() > 0).forEach(usedCases::add);
        if (defaultCase.getNoOfUses() > 0) {
            usedCases.add(defaultCase);
        }

        if (usedCases.size() == 1) {
            Statement body = usedCases.get(0).getBody();
            String res = body.minimizedTestCase();
            return res;
        } else if (usedCases.size() > 1) {

            String res = String.format("match %s {\n", test.toString());
            for (MatchStatementCase c : usedCases) {
                res = res + StringUtils.indent(c.minimizedTestCase());
            }
            res = res + "\n}\n";
            return res;
        }

        return "";

    }

    @Override
    public String toString() {
        String res = String.format("match %s {\n", test.toString());
        for (MatchStatementCase c : distinctCases) {
            res = res + StringUtils.indent(c.toString());
        }
        res = res + "\n}\n";
        return res;
    }
}
