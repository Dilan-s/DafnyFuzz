package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.SymbolTable.Method;
import AST.SymbolTable.PrimitiveTypes.DSet;
import AST.SymbolTable.PrimitiveTypes.Multiset;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

}
