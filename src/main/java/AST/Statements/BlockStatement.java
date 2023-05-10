package AST.Statements;

import AST.Generator.GeneratorConfig;
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
import java.util.stream.Stream;

public class BlockStatement extends BaseStatement {

    private final SymbolTable symbolTable;
    private final List<Statement> body;

    private List<List<Statement>> expanded;

    public BlockStatement(SymbolTable symbolTable) {
        super();
        this.symbolTable = new SymbolTable(symbolTable);
        this.body = new ArrayList<>();
        this.expanded = new ArrayList<>();
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void addStatementFirst(Statement statement) {
        body.add(0, statement);
        expanded.add(0, statement.expand());
    }

    public void addStatement(Statement statement) {
        body.add(statement);
        expanded.add(statement.expand());
    }

    public void addStatement(List<Statement> statement) {
        for (Statement s : statement) {
            addStatement(s);
        }
    }

    @Override
    public boolean isReturn() {
        return body.stream().anyMatch(Statement::isReturn);
    }

    @Override
    public boolean minimizedReturn() {
        return body.stream().anyMatch(Statement::minimizedReturn);
    }

    @Override
    protected ReturnStatus execute(Map<Variable, Variable> paramMap, StringBuilder s, boolean unused) {
        for (int i = 0, bodySize = body.size(); i < bodySize; i++) {
            Statement statement = body.get(i);
            List<Statement> ss = statement.expand();
            for (Statement stat : ss) {
                ReturnStatus retValues = stat.execute(paramMap, s);
                if (retValues != ReturnStatus.UNKNOWN) {
                    return retValues;
                }
            }
        }
        return ReturnStatus.UNKNOWN;
    }

    @Override
    public Set<Variable> getModifies() {
        return body.stream()
            .map(Statement::expand)
            .flatMap(Collection::stream)
            .map(Statement::getModifies)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }

    @Override
    public boolean requireUpdate() {
        return body.stream().anyMatch(Statement::requireUpdate);
    }

    @Override
    public List<Statement> expand() {
        for (int i = 0, bodySize = body.size(); i < bodySize; i++) {
            Statement statement = body.get(i);
            if (statement.requireUpdate()) {
                expanded.set(i, statement.expand());
            }
        }
        return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        List<String> code = new ArrayList<>();
        for (Statement b : body) {
            for (Statement s : b.expand()) {
                String val = s.toString();
                code.add(val);
            }
        }
        return StringUtils.intersperse("\n", code);
    }

    @Override
    public String minimizedTestCase() {
        List<String> code = new ArrayList<>();
        boolean goToNext = true;
        for (int i = 0; goToNext && i < body.size(); i++) {
            Statement b = body.get(i);
            if (b.getNoOfUses() > 0) {
                List<Statement> expand = b.expand();
                for (int j = 0; j < expand.size(); j++) {
                    Statement s = expand.get(j);
                    String val = s.minimizedTestCase();
                    if (!val.isEmpty()) {
                        code.add(val);
                    }

                    if (s.minimizedReturn()) {
                        goToNext = false;
                        break;
                    }
                }
            }
        }
        return StringUtils.intersperse("\n", code);
    }

    @Override
    public List<String> toOutput() {
        Set<String> res = new HashSet<>();
        List<String> temp = new ArrayList<>();

        res.add("");

        boolean first = true;
        for (Statement b : body) {
            List<Statement> expand = b.expand();
            for (Statement stat : expand) {
                List<String> statOptions = stat.toOutput();
                temp = new ArrayList<>();
                for (String f : res) {
                    for (String statOption : statOptions) {
                        if (!first) {
                            statOption = "\n" + statOption;
                        }
                        String curr = f + statOption;
                        temp.add(curr);
                    }

                }
                if (statOptions.isEmpty()) {
                    temp.addAll(res);
                }
                first = false;
                res = new HashSet(temp);

                List<String> r = new ArrayList<>(res);
                Collections.shuffle(r, GeneratorConfig.getRandom());
                res = new HashSet<>(r.subList(0, Math.min(5, r.size())));
            }
        }

        List<String> r = new ArrayList<>(res);
        Collections.shuffle(r, GeneratorConfig.getRandom());
        return r.subList(0, Math.min(5, res.size()));
    }
}
