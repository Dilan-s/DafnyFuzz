package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class MultisetLiteral implements Expression {

    private final Type type;
    private SymbolTable symbolTable;
    private List<Expression> values;
    private Optional<Expression> collection;

    public MultisetLiteral(SymbolTable symbolTable, Type type) {
        this.symbolTable = symbolTable;
        this.type = type;
        this.values = new ArrayList<>();
        this.collection = Optional.empty();
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    public void addValue(Expression expression) {
        values.add(expression);
    }

    public void setCollection(Expression expression) {
        this.collection = Optional.of(expression);
    }


    @Override
    public List<String> toCode() {
        List<String> code = values.stream()
            .map(Expression::toCode)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
        code.addAll(collection.isEmpty() ? new ArrayList<>() : collection.get().toCode());
        return code;
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {
    }

    @Override
    public String toString() {
        if (collection.isEmpty()) {
            String value = values.stream()
                            .map(Expression::toString)
                            .collect(Collectors.joining(", "));

            return String.format("multiset{%s}", value);
        } else {
            return String.format("multiset(%s)", collection.get());
        }
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
}
