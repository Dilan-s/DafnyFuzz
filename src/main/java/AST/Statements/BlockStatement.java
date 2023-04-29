package AST.Statements;

import AST.Generator.GeneratorConfig;
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

public class BlockStatement extends BaseStatement {

    private final SymbolTable symbolTable;
    private final List<Statement> body;

    public BlockStatement(SymbolTable symbolTable) {
        super();
        this.symbolTable = new SymbolTable(symbolTable);
        this.body = new ArrayList<>();
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void addStatement(Statement statement) {
        body.add(statement);
    }

    public void addStatement(List<Statement> statement) {
        body.addAll(statement);
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
    public List<Object> execute(Map<Variable, Variable> paramMap, StringBuilder s) {
        super.incrementUse();
        for (int i = 0, bodySize = body.size(); i < bodySize; i++) {
            Statement statement = body.get(i);
            List<Statement> ss = statement.expand();
            for (Statement stat : ss) {
                List<Object> retValues = stat.execute(paramMap, s);
                if (retValues != null) {
                    return retValues;
                }
            }
        }
        return null;
    }

    @Override
    public List<Statement> expand() {
        return body.stream()
            .map(Statement::expand)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
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
                res = new HashSet<>(r.subList(0, Math.min(5, temp.size())));
            }
        }

        List<String> r = new ArrayList<>(res);
        Collections.shuffle(r, GeneratorConfig.getRandom());
        return r.subList(0, Math.min(5, res.size()));
    }
}
