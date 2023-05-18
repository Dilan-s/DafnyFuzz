package AST.SymbolTable.Types.GenericType;

import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.Objects;

public class GenericType implements Type {

    private String representation;
    private Type type;

    public Type getType() {
        return type;
    }

    public GenericType(String representation) {
        this.representation = representation;
        this.type = null;
    }

    public GenericType(String representation, Type type) {
        this.representation = representation;
        this.type = type;
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        return type.generateLiteral(symbolTable);
    }

    @Override
    public Expression generateExpressionFromValue(SymbolTable symbolTable, Object value) {
        return type.generateExpressionFromValue(symbolTable, value);
    }

    @Override
    public String getVariableType() {
        return representation;
    }

    @Override
    public boolean operatorExists() {
        return type.operatorExists();
    }

    @Override
    public boolean isPrintable() {
        return type.isPrintable();
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
        Type type = typeGenerator.generateTypes(1, symbolTable).get(0);
        return type;
    }

    @Override
    public boolean isCollection() {
        return type.isCollection();
    }

    @Override
    public Boolean lessThan(Object lhsV, Object rhsV) {
        return type.lessThan(lhsV, rhsV);
    }

    @Override
    public Boolean equal(Object lhsV, Object rhsV) {
        return type.equal(lhsV, rhsV);
    }

    @Override
    public Boolean lessThanOrEqual(Object lhsV, Object rhsV) {
        return type.lessThanOrEqual(lhsV, rhsV);
    }

    @Override
    public Boolean greaterThan(Object lhsV, Object rhsV) {
        return type.greaterThan(lhsV, rhsV);
    }

    @Override
    public Boolean greaterThanOrEqual(Object lhsV, Object rhsV) {
        return type.greaterThanOrEqual(lhsV, rhsV);
    }

    @Override
    public String formatPrint(Object object) {
        return type.formatPrint(object);
    }

    @Override
    public String formatEnsures(String variableName, Object object) {
        return type.formatEnsures(variableName, object);
    }

    @Override
    public boolean validMethodType() {
        return type.validMethodType();
    }

    @Override
    public Object of(Object value) {
        return type.of(value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(representation, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GenericType)) {
            return false;
        }
        GenericType other = (GenericType) obj;
        return other.representation.equals(representation) && (other.type == null || type == null || other.type.equals(type));
    }

    @Override
    public String toString() {
        return type.toString();
    }

    @Override
    public String getName() {
        return type.getName();
    }

    public String getRepresentation() {
        return representation;
    }
}
