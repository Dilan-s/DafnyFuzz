package AST.Statements.Expressions.Match;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.Expression;
import AST.Statements.Statement;
import AST.Statements.util.MatchStatementCase;
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

public class MatchExpression implements Expression {

    private SymbolTable symbolTable;
    private Type type;
    private Expression test;
    private List<MatchExpressionCase> cases;
    private MatchExpressionCase defaultCase;

    private List<List<Statement>> expanded;

    public MatchExpression(SymbolTable symbolTable, Type type, Expression test, List<MatchExpressionCase> cases, MatchExpressionCase defaultCase) {
        this.symbolTable = symbolTable;
        this.type = type;
        this.test = test;
        this.cases = cases;
        this.defaultCase = defaultCase;

        this.expanded = new ArrayList<>();
        expanded.add(test.expand());
        cases.forEach(c -> expanded.add(c.expand()));
        expanded.add(defaultCase.expand());
    }

    @Override
    public List<Statement> expand() {
        int i = 0;
        if (test.requireUpdate()) {
            expanded.set(i, test.expand());
        }
        i++;

        for (int j = 0; j < cases.size(); j++) {
            MatchExpressionCase mECase = cases.get(j);
            if (mECase.requireUpdate()) {
                expanded.set(i, mECase.expand());
            }
            i++;
        }
        if (defaultCase.requireUpdate()) {
            expanded.set(i, defaultCase.expand());
        }

        return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    @Override
    public boolean isValidReturn() {
        return cases.stream().allMatch(Expression::isValidReturn) && defaultCase.isValidReturn();
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s) {
        List<Object> r = new ArrayList<>();
        Object testValue = test.getValue(paramsMap, s).get(0);

        if (testValue == null) {

            r.add(null);
            return r;
        }
        for (MatchExpressionCase mECase : cases) {
            Object caseTestValue = mECase.getTest().getValue(paramsMap, s).get(0);
            if (caseTestValue == null) {
                r.add(null);
                return r;
            }
            if (testValue.equals(caseTestValue)) {
                return mECase.getValue(paramsMap, s);
            }
        }

        return defaultCase.getValue(paramsMap, s);
    }

    @Override
    public boolean requireUpdate() {
        return test.requireUpdate() || cases.stream().anyMatch(Expression::requireUpdate) || defaultCase.requireUpdate();
    }

    @Override
    public List<String> toOutput() {
        Set<String> res = new HashSet<>();
        List<String> temp = new ArrayList<>();

        for (String s : test.toOutput()) {
            res.add(String.format("match %s {\n", s));
        }

        for (MatchExpressionCase mECase : cases) {
            temp = new ArrayList<>();
            List<String> caseOutputs = mECase.toOutput();
            for (String f : res) {
                for (String caseOption : caseOutputs) {
                    String curr = f + StringUtils.indent(caseOption) + "\n";
                    temp.add(curr);
                }
            }
            if (caseOutputs.isEmpty()) {
                temp.addAll(res);
            }

            Collections.shuffle(temp, GeneratorConfig.getRandom());
            res = new HashSet<>(temp.subList(0, Math.min(5, temp.size())));
        }

        List<String> caseOptions = defaultCase.toOutput();
        for (String f : res) {
            for (String caseOption : caseOptions) {
                String curr = f + StringUtils.indent(caseOption) + "\n";
                temp.add(curr);
            }
        }
        if (caseOptions.isEmpty()) {
            temp.addAll(res);
        }

        res = new HashSet<>(temp);

        temp = new ArrayList<>();
        for (String f : res) {
            temp.add(f + "}");
        }

        res = new HashSet<>(temp);

        List<String> r = new ArrayList<>(res);
        Collections.shuffle(r, GeneratorConfig.getRandom());
        return r.subList(0, Math.min(5, r.size()));
    }

    @Override
    public String toString() {
        String res = String.format("match %s {\n", test.toString());
        for (MatchExpressionCase c : cases) {
            res = res + StringUtils.indent(c.toString()) + "\n";
        }
        res = res + StringUtils.indent(defaultCase.toString()) + "\n";
        res = res + "}\n";
        return res;
    }
}
