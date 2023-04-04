package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArrayLiteral implements Expression {

    private final Type type;
    private SymbolTable symbolTable;

    private Variable variable;
    private List<Expression> values;
    private AssignmentStatement statement;

    public ArrayLiteral(SymbolTable symbolTable, Type type, List<Expression> values, boolean toAssign) {
        this.symbolTable = symbolTable;
        this.type = type;
        this.values = values;

        if (toAssign) {
            this.variable = new Variable(VariableNameGenerator.generateVariableValueName(type, symbolTable), type);
            statement = new AssignmentStatement(symbolTable);
            statement.addAssignment(List.of(variable), new ArrayInitValues(values));
            statement.addAssignmentsToSymbolTable();
        }
    }

    public ArrayLiteral(SymbolTable symbolTable, Type type, List<Expression> values) {
        this(symbolTable, type, values, true);
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    public void addValue(Expression expression) {
        values.add(expression);
    }

    @Override
    public List<String> toCode() {
        List<String> code = values.stream()
            .map(Expression::toCode)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());


        code.addAll(statement.toCode());
        return code;
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {
    }

    @Override
    public String toString() {
        return variable.getName();
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, values);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ArrayLiteral)) {
            return false;
        }
        ArrayLiteral other = (ArrayLiteral) obj;

        if (other.values.size() != values.size()) {
            return false;
        }

        for (int i = 0; i < values.size(); i++) {
            if (!other.values.get(i).equals(values.get(i))) {
                return false;
            }
        }
        return true;
    }

    private class ArrayInitValues implements Expression {

        private final List<Expression> values;

        public ArrayInitValues(List<Expression> values) {
            this.values = values;
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
            String value = values.stream()
                .map(Expression::toString)
                .collect(Collectors.joining(", "));
            DCollection t = (DCollection) type;
            return String.format("new %s[] [%s]", t.getInnerType().getName(), value);
        }
    }
}
