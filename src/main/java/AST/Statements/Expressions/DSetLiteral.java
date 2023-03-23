package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.DCollectionTypes.DSet;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DSetLiteral implements Expression {

    private final Type type;
    private final List<Expression> values;
    private SymbolTable symbolTable;

    public DSetLiteral(SymbolTable symbolTable, Type type) {
        this.symbolTable = symbolTable;
        this.type = type;
        this.values = new ArrayList<>();
    }

    @Override
    public List<Type> getTypes() {
        return List.of(new DSet(type));
    }

    public void addValue(Expression expression) {
        values.add(expression);
    }

    @Override
    public List<String> toCode() {
        return values.stream()
            .map(Expression::toCode)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {
    }

    @Override
    public String toString() {
        String value = values.stream()
            .map(Expression::toString)
            .collect(Collectors.joining(", "));
        return String.format("{%s}", value);
    }

}
