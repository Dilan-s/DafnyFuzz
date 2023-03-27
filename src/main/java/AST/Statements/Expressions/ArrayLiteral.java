package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayLiteral implements Expression {

    private final Type type;
    private Variable variable;
    private final List<Expression> values;
    private SymbolTable symbolTable;
    private AssignmentStatement statement;

    public ArrayLiteral(SymbolTable symbolTable, Type type) {
        this.symbolTable = symbolTable;
        this.type = type;
        this.variable = new Variable(VariableNameGenerator.generateVariableValueName(type), type);
        this.values = new ArrayList<>();
        ArrayInitValues arrayInitValues = new ArrayInitValues(values);
        statement = new AssignmentStatement(symbolTable);
        statement.addAssignment(List.of(variable), arrayInitValues);
        statement.addAssignmentsToSymbolTable();
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
