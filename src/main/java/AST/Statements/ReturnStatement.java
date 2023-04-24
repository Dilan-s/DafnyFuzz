package AST.Statements;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.Expression;
import AST.Statements.util.PrintAll;
import AST.StringUtils;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReturnStatement implements Statement {

    private final SymbolTable symbolTable;
    private final List<Expression> values;
    private boolean printAll;

    public ReturnStatement(SymbolTable symbolTable, List<Expression> values) {
        this.symbolTable = symbolTable;
        this.values = values;
        this.printAll = true;

    }

    public void setPrintAll(boolean printAll) {
        this.printAll = printAll;
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
            res = new HashSet(temp);
        }

        temp = new ArrayList<>();
        for (String f : res) {
            temp.add(f + ";");
        }
        res = new HashSet(temp);

        List<String> r = new ArrayList<>(res);
        Collections.shuffle(r, GeneratorConfig.getRandom());
        return r.subList(0, Math.min(5, res.size()));
    }

    public List<Expression> getReturnValues() {
        return values;
    }

    @Override
    public List<Object> execute(Map<Variable, Variable> paramMap, StringBuilder s) {
        List<Object> list = new ArrayList<>();
        for (Expression x : values) {
            List<Object> value = x.getValue(paramMap, s);
            for (Object object : value) {
                list.add(object);
            }
        }
        return list;
    }

    @Override
    public List<Statement> expand() {
        List<Statement> r = new ArrayList<>();
        List<Statement> list = new ArrayList<>();
        for (Expression value : values) {
            List<Statement> expand = value.expand();
            for (Statement statement : expand) {
                list.add(statement);
            }
        }
        r.addAll(list);
        if (printAll) {
            r.addAll(new PrintAll(symbolTable).expand());
        }
        r.add(this);
        return r;
    }
}
