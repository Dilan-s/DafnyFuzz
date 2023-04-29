package AST.Statements;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.Expression;
import AST.StringUtils;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class IfElseStatement extends BaseStatement {

    private final SymbolTable symbolTable;
    private Expression test;
    private Statement ifStat;
    private Optional<Statement> elseStat;

    public IfElseStatement(SymbolTable symbolTable) {
        super();
        this.symbolTable = symbolTable;
        this.elseStat = Optional.empty();
    }

    public void setTest(Expression test) {
        this.test = test;
    }

    public void setIfStat(Statement ifStat) {
        this.ifStat = ifStat;
    }

    public void setElseStat(Statement elseStat) {
        this.elseStat = Optional.of(elseStat);
    }

    @Override
    public boolean isReturn() {
        return ifStat.isReturn() && elseStat.isPresent() && elseStat.get().isReturn();
    }

    @Override
    public boolean minimizedReturn() {
        if (elseStat.isEmpty()) {
            if (ifStat.getNoOfUses() > 0) {
                return ifStat.minimizedReturn();
            }
            return false;
        }

        if (ifStat.getNoOfUses() > 0 && elseStat.get().getNoOfUses() == 0) {
            return ifStat.minimizedReturn() ;
        } else if (ifStat.getNoOfUses() == 0 && elseStat.get().getNoOfUses() > 0) {
            return elseStat.get().minimizedReturn();
        } else {
            return ifStat.minimizedReturn() && elseStat.get().minimizedReturn();
        }
    }

    @Override
    public String toString() {
        List<String> code = new ArrayList<>();

        code.add(String.format("if %s {", test));
        code.add(StringUtils.indent(ifStat.toString()));

        if (elseStat.isPresent()) {
            code.add("} else {");
            code.add(StringUtils.indent(elseStat.get().toString()));
        }

        code.add("}");
        return StringUtils.intersperse("\n", code);
    }

    @Override
    public String minimizedTestCase() {
        if (elseStat.isEmpty()) {
            if (ifStat.getNoOfUses() > 0) {
                return ifStat.minimizedTestCase();
            }
            return "";
        }

        if (ifStat.getNoOfUses() > 0 && elseStat.get().getNoOfUses() == 0) {
            return ifStat.minimizedTestCase();
        } else if (ifStat.getNoOfUses() == 0 && elseStat.get().getNoOfUses() > 0) {
            return elseStat.get().minimizedTestCase();
        } else {

            List<String> code = new ArrayList<>();

            code.add(String.format("if %s {", test));
            code.add(StringUtils.indent(ifStat.minimizedTestCase()));

            if (elseStat.isPresent()) {
                code.add("} else {");
                code.add(StringUtils.indent(elseStat.get().minimizedTestCase()));
            }

            code.add("}");
            return StringUtils.intersperse("\n", code);
        }
    }

    @Override
    public List<String> toOutput() {
        Set<String> res = new HashSet<>();
        List<String> temp = new ArrayList<>();

        res.add("if ");

        List<String> testOptions = test.toOutput();
        temp = new ArrayList<>();
        for (String f : res) {
            for (String testOption : testOptions) {
                String curr = f + testOption;
                temp.add(curr);
            }
        }
        if (testOptions.isEmpty()) {
            temp.addAll(res);
        }
        res = new HashSet(temp);

        temp = new ArrayList<>();
        for (String f : res) {
            temp.add(f + " {\n");
        }
        res = new HashSet(temp);

        List<String> ifOptions = ifStat.toOutput();
        temp = new ArrayList<>();
        for (String f : res) {
            for (String ifOption : ifOptions) {
                String curr = StringUtils.indent(ifOption);
                curr = f + curr;
                temp.add(curr);
            }
        }
        if (ifOptions.isEmpty()) {
            temp.addAll(res);
        }
        res = new HashSet(temp);

        if (elseStat.isPresent()) {

            temp = new ArrayList<>();
            for (String f : res) {
                temp.add(f + "\n} else {\n");
            }
            res = new HashSet(temp);

            List<String> elseOptions = elseStat.get().toOutput();
            temp = new ArrayList<>();
            for (String f : res) {
                for (String elseOption : elseOptions) {
                    String curr = StringUtils.indent(elseOption);
                    curr = f + curr;
                    temp.add(curr);
                }
            }
            if (elseOptions.isEmpty()) {
                temp.addAll(res);
            }
            res = new HashSet(temp);
        }

        temp = new ArrayList<>();
        for (String f : res) {
            temp.add(f + "\n}");
        }
        res = new HashSet(temp);

        List<String> r = new ArrayList<>(res);
        Collections.shuffle(r, GeneratorConfig.getRandom());
        return r.subList(0, Math.min(5, res.size()));
    }

    @Override
    public List<Object> execute(Map<Variable, Variable> paramMap, StringBuilder s) {
        super.incrementUse();
        Object testValue = test.getValue(paramMap, s).get(0);
        Boolean testB = (Boolean) testValue;
        if (testB) {
            return ifStat.execute(paramMap, s);
        } else if (elseStat.isPresent()) {
            return elseStat.get().execute(paramMap, s);
        }
        return null;
    }

    @Override
    public List<Statement> expand() {
        List<Statement> r = new ArrayList<>();
        r.addAll(test.expand());
        r.add(this);
        return r;
    }
}
