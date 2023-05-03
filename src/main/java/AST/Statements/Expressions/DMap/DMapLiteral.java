package AST.Statements.Expressions.DMap;

import AST.Errors.SemanticException;
import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.Expression;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.DMap.DMapEntry;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DMapLiteral implements Expression {

    private final SymbolTable symbolTable;
    private final Type type;
    private List<DMapEntry> entries;
    private final List<DMapEntry> entriesInMap;

    private List<List<Statement>> expanded;

    public DMapLiteral(SymbolTable symbolTable, Type type, List<DMapEntry> entries) {
        this.symbolTable = symbolTable;
        this.type = type;
        this.entries = entries;
        this.entriesInMap = new ArrayList<>(entries);

        this.expanded = new ArrayList<>();
        entries.forEach(e -> {
            expanded.add(e.getKey().expand());
            expanded.add(e.getValue().expand());
        });
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s) {
        List<Object> r = new ArrayList<>();
        Map<Object, Object> m = new HashMap<>();

        List<DMapEntry> entries = new ArrayList<>(this.entriesInMap);

        for (int i = 0; i < entries.size(); i++) {
            DMapEntry entry = entries.get(i);
            List<Object> keyValues = entry.getKey().getValue(paramsMap, s);
            List<Object> valueValues = entry.getValue().getValue(paramsMap, s);

            for (int j = 0; j < Math.min(keyValues.size(), valueValues.size()); j++) {
                Object key = keyValues.get(j);
                Object value = valueValues.get(j);

                if (key == null || value == null) {
                    r.add(null);
                    return r;
                }

                if (m.containsKey(key)) {
                    this.entriesInMap.remove(entry);
                } else {
                    m.put(key, value);
                }
            }
        }
        r.add(m);
        return r;
    }

    @Override
    public List<Statement> expand() {
        for (int i = 0; i < entries.size(); i++) {
            DMapEntry entry = entries.get(i);

            if (entry.getKey().requireUpdate()) {
                expanded.set(2 * i, entry.getKey().expand());
            }

            if (entry.getValue().requireUpdate()) {
                expanded.set(2 * i + 1, entry.getValue().expand());
            }
        }
        return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public boolean requireUpdate() {
        return entries.stream().anyMatch(e -> e.getKey().requireUpdate() || e.getValue().requireUpdate());
    }

    @Override
    public String toString() {
        return String.format("map[%s]", entriesInMap.stream()
            .map(x -> x.getKey() + " := " + x.getValue())
            .collect(Collectors.joining(", ")));
    }

    @Override
    public List<String> toOutput() {
        Set<String> res = new HashSet<>();
        List<String> temp = new ArrayList<>();

        res.add("map[");

        boolean first = true;
        for (DMapEntry entry : entriesInMap) {
            List<String> keyOptions = entry.getKey().toOutput();
            List<String> valueOptions = entry.getValue().toOutput();

            temp = new ArrayList<>();
            for (String f : res) {
                for (String keyOption : keyOptions) {
                    for (String valueOption : valueOptions) {
                        String curr = f;
                        if (!first) {
                            curr = curr + ", ";
                        }
                        curr = curr + keyOption + " := " + valueOption;
                        temp.add(curr);
                    }
                }
            }
            if (keyOptions.isEmpty()) {
                temp.addAll(res);
            }
            first = false;
            Collections.shuffle(temp, GeneratorConfig.getRandom());
            temp = temp.subList(0, Math.min(5, temp.size()));
            res = new HashSet<>(temp);
        }

        temp = new ArrayList<>();
        for (String f : res) {
            temp.add(f + "]");
        }
        res = new HashSet(temp);

        List<String> r = new ArrayList<>(res);
        Collections.shuffle(r, GeneratorConfig.getRandom());
        return r.subList(0, Math.min(5, res.size()));
    }
}
