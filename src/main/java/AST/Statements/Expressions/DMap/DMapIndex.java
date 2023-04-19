package AST.Statements.Expressions.DMap;

import AST.Errors.SemanticException;
import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.Expression;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DMapIndex implements Expression {

    private final SymbolTable symbolTable;
    private final Type type;
    private final Expression map;
    private final Expression index;

    public DMapIndex(SymbolTable symbolTable, Type type, Expression map, Expression index) {
        this.symbolTable = symbolTable;
        this.type = type;
        this.map = map;
        this.index = index;
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s) {
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
        List<Statement> s = new ArrayList<>();
        s.addAll(map.expand());
        s.addAll(index.expand());
        return s;
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
}
