package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.DCollectionTypes.DSet;
import AST.SymbolTable.Types.DCollectionTypes.Seq;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MultisetLiteral implements Expression {

    private final Type type;
    private SymbolTable symbolTable;
    private List<Expression> values;
    private Optional<Expression> collection;

    public MultisetLiteral(SymbolTable symbolTable, Type type, List<Expression> values) {
        this.symbolTable = symbolTable;
        this.type = type;
        this.values = values;
        this.collection = Optional.empty();
    }

    public MultisetLiteral(SymbolTable symbolTable, Type type, Optional<Expression> collection) {
        this.symbolTable = symbolTable;
        this.type = type;
        this.values = new ArrayList<>();
        this.collection = collection;
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {
    }

    @Override
    public String toString() {
        if (collection.isPresent()) {
            return String.format("multiset(%s)", collection.get());
        } else {
            String value = values.stream()
                .map(Expression::toString)
                .collect(Collectors.joining(", "));

            return String.format("multiset{%s}", value);
        }
    }

    @Override
    public List<Statement> expand() {
        if (collection.isPresent()) {
            return collection.get().expand();
        }
        List<Statement> list = new ArrayList<>();
        for (Expression value : values) {
            List<Statement> expand = value.expand();
            for (Statement statement : expand) {
                list.add(statement);
            }
        }
        return list;
    }

    @Override
    public int hashCode() {
        if (collection.isPresent()) {
            return collection.hashCode();
        } else {
            return Objects.hash(values);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MultisetLiteral)) {
            return false;
        }
        MultisetLiteral other = (MultisetLiteral) obj;

        if (collection.isPresent() && other.collection.isPresent()) {
            return collection.get().equals(other.collection.get());
        } else if (collection.isEmpty() && other.collection.isEmpty()) {
            if (values.size() != other.values.size()) {
                return false;
            }

            for (int i = 0; i < values.size(); i++) {
                if (!values.get(i).equals(other.values.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap) {
        List<Object> r = new ArrayList<>();

        if (collection.isPresent()) {
            Expression col = collection.get();
            Type type = col.getTypes().get(0);
            Object colValue = col.getValue(paramsMap).get(0);

            if (colValue != null) {

                Map<Object, Integer> m = new HashMap<>();
                if (type.equals(new Seq())) {
                    List<Object> colVL = (List<Object>) colValue;

                    for (Object v : colVL) {
                        m.put(v, m.getOrDefault(v, 0) + 1);
                    }
                    r.add(m);
                    return r;
                } else if (type.equals(new DSet())) {
                    Set<Object> colVS = (Set<Object>) colValue;

                    for (Object v : colVS) {
                        m.put(v, 1);
                    }
                    r.add(m);
                    return r;
                }
            }
            r.add(null);

        } else {
            Map<Object, Integer> m = new HashMap<>();

            for (Expression e : values) {
                List<Object> value = e.getValue(paramsMap);
                for (Object v : value) {
                    if (v == null) {
                        r.add(null);
                        return r;
                    }
                    m.put(v, m.getOrDefault(v, 0) + 1);
                }
            }
            r.add(m);
        }
        return r;
    }
}
