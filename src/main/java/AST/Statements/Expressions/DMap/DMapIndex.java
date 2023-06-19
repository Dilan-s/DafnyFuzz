package AST.Statements.Expressions.DMap;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.BaseExpression;
import AST.Statements.Expressions.Expression;
import AST.Statements.Statement;
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

public class DMapIndex extends BaseExpression {

    private final SymbolTable symbolTable;
    private final Type type;
    private final Expression map;
    private final Expression index;

    private List<List<Statement>> expanded;

    public DMapIndex(SymbolTable symbolTable, Type type, Expression map, Expression index) {
        super();
        this.symbolTable = symbolTable;
        this.type = type;
        this.map = map;
        this.index = index;

        this.expanded = new ArrayList<>();
        expanded.add(map.expand());
        expanded.add(index.expand());
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    @Override
    protected List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s, boolean unused) {
        List<Object> r = new ArrayList<>();

        Object mapVarValue = map.getValue(paramsMap).get(0);
        Object indexValue = index.getValue(paramsMap).get(0);

        if (mapVarValue != null && indexValue != null) {
            Map<Object, Object> map = (Map<Object, Object>) mapVarValue;

            if (map.containsKey(indexValue)) {
                r.add(map.get(indexValue));
                return r;
            }
        }

        r.add(null);
        return r;
    }

    @Override
    public List<Statement> expand() {
        if (map.requireUpdate()) {
            expanded.set(0, map.expand());
        }

        if (index.requireUpdate()) {
            expanded.set(1, index.expand());
        }

        return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public boolean validForFunction() {
        return map.validForFunction() || index.validForFunction();
    }

    @Override
    public boolean requireUpdate() {
        return map.requireUpdate() || index.requireUpdate();
    }


    @Override
    public List<String> toOutput() {
        Set<String> res = new HashSet<>();

        for (String m : map.toOutput()) {
            for (String i : index.toOutput()) {
                res.add(m + "[" + i + "]");
            }
        }

        List<String> r = new ArrayList<>(res);
        Collections.shuffle(r, GeneratorConfig.getRandom());
        return r.subList(0, Math.min(5, res.size()));
    }

    @Override
    public String toString() {
        return map.toString() + "[" + index.toString() + "]";
    }

    @Override
    public String minimizedTestCase() {
        return map.minimizedTestCase() + "[" + index.minimizedTestCase() + "]";
    }
}
