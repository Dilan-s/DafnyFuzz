package AST.Statements.Expressions.DMap;

import AST.Errors.SemanticException;
import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.Expression;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DMapUpdateExpression implements Expression {

    private final SymbolTable symbolTable;
    private final Type type;
    private final Expression map;
    private final Expression key;
    private final Expression value;

    public DMapUpdateExpression(SymbolTable symbolTable, Type type, Expression map, Expression key, Expression value) {
        this.symbolTable = symbolTable;
        this.type = type;
        this.map = map;
        this.key = key;
        this.value = value;
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {

    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s) {
        List<Object> res = new ArrayList<>();

        Object mapValue = map.getValue(paramsMap, s).get(0);
        Object keyValue = key.getValue(paramsMap, s).get(0);
        Object valueValue = value.getValue(paramsMap, s).get(0);

        if (mapValue != null && keyValue != null && valueValue != null) {
            Map<Object, Object> m = (Map<Object, Object>) mapValue;
            m.put(keyValue, valueValue);

            res.add(m);
            return res;
        }
        res.add(null);
        return res;
    }

    @Override
    public List<Statement> expand() {
        List<Statement> s = new ArrayList<>();

        s.addAll(map.expand());
        s.addAll(key.expand());
        s.addAll(value.expand());

        return s;
    }

    @Override
    public List<String> toOutput() {
        Set<String> res = new HashSet<>();
        List<String> temp = new ArrayList<>();
        res.add("");

        temp = new ArrayList<>();
        List<String> mapOptions = map.toOutput();
        for (String f : res) {
            for (String mapOption : mapOptions) {
                String curr = f + mapOption + "[";
                temp.add(curr);
            }
        }
        if (mapOptions.isEmpty()) {
            temp.addAll(res);
        }
        res = new HashSet(temp);

        temp = new ArrayList<>();
        List<String> keyOptions = key.toOutput();
        for (String f : res) {
            for (String keyOption : keyOptions) {
                String curr = f + keyOption;
                temp.add(curr);
            }
        }
        if (keyOptions.isEmpty()) {
            temp.addAll(res);
        }
        res = new HashSet(temp);

        temp = new ArrayList<>();
        List<String> valueOptions = value.toOutput();
        for (String f : res) {
            for (String valueOption : valueOptions) {
                String curr = f + " := " + valueOption + "]";
                temp.add(curr);
            }
        }
        if (valueOptions.isEmpty()) {
            temp.addAll(res);
        }
        res = new HashSet(temp);


        List<String> r = new ArrayList<>(res);
        Collections.shuffle(r, GeneratorConfig.getRandom());
        return r.subList(0, Math.min(5, res.size()));
    }

    @Override
    public String toString() {
        return String.format("%s[%s := %s]", map, key, value);
    }


}
