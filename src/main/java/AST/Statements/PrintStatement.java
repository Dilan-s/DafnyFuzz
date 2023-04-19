package AST.Statements;

import AST.Errors.SemanticException;
import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.Expression;
import AST.Statements.util.ReturnStatus;
import AST.StringUtils;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PrintStatement implements Statement {

    private final SymbolTable symbolTable;
    private final List<Expression> values;

    public PrintStatement(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.values = new ArrayList<>();
    }

    public void addValue(Expression expression) {
        values.add(expression);
    }

    @Override
    public String toString() {
        List<String> code = new ArrayList<>();;

        String printValues = values.stream()
            .filter(x -> x.getTypes().stream().allMatch(Type::isPrintable))
            .map(Expression::toString)
            .collect(Collectors.joining(", \" \", "));
        if (!printValues.isEmpty()) {
            code.add(String.format("print %s, \"\\n\";", printValues));
        }
        return StringUtils.intersperse("\n", code);
    }

    @Override
    public List<String> toOutput() {
        Set<String> res = new HashSet<>();
        List<String> temp = new ArrayList<>();

        List<Expression> printValues = values.stream()
            .filter(x -> x.getTypes().stream().allMatch(Type::isPrintable))
            .collect(Collectors.toList());

        if (!printValues.isEmpty()) {
            res.add("print ");

            boolean first = true;
            for (Expression exp : printValues) {
                List<String> expOptions = exp.toOutput();
                temp = new ArrayList<>();
                for (String f : res) {
                    for (String expOption : expOptions) {
                        if (!first) {
                            expOption = ", \" \", " + expOption;
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
                temp.add(f + ", \"\\n\";");
            }
            res = new HashSet(temp);
        }
        List<String> r = new ArrayList<>(res);
        Collections.shuffle(r, GeneratorConfig.getRandom());
        return r.subList(0, Math.min(5, res.size()));
    }

    @Override
    public ReturnStatus assignReturnIfPossible(Method method, ReturnStatus currStatus, List<Expression> dependencies) {
        return currStatus;
    }

    @Override
    public List<Object> execute(Map<Variable, Variable> paramMap, StringBuilder s) {
        List<String> joiner = new ArrayList<>();

        for (Expression exp : values) {
            List<Type> types = exp.getTypes();
            if (types.stream().allMatch(Type::isPrintable)) {
                List<Object> value = exp.getValue(paramMap, s);
                for (int i = 0, valueSize = value.size(); i < valueSize; i++) {
                    Object object = value.get(i);
                    Type t = types.get(i);
                    String str = t.formatPrint(object);
                    joiner.add(str);
                }
            }
        }
        String printValues = String.join(" ", joiner);

        if (!joiner.isEmpty()) {
            s.append(printValues);
            s.append("\n");
        }
        return null;
    }

    @Override
    public List<Statement> expand() {
        List<Statement> r = new ArrayList<>();
        r.addAll(values.stream()
            .map(Expression::expand)
            .flatMap(Collection::stream)
            .collect(Collectors.toList()));
        r.add(this);
        return r;
    }
}
