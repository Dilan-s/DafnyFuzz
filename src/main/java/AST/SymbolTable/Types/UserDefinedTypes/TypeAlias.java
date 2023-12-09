package AST.SymbolTable.Types.UserDefinedTypes;

import AST.Expressions.Expression;
import AST.Generator.RandomTypeGenerator;
import AST.Generator.VariableNameGenerator;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.DCollectionTypes.DArray;
import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Types.DCollectionTypes.DSet;
import AST.SymbolTable.Types.DCollectionTypes.Multiset;
import AST.SymbolTable.Types.DCollectionTypes.Seq;
import AST.SymbolTable.Types.DMap.DMap;
import AST.SymbolTable.Types.GenericType.GenericType;
import AST.SymbolTable.Types.PrimitiveTypes.BaseType;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.Types.PrimitiveTypes.Char;
import AST.SymbolTable.Types.PrimitiveTypes.DString;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.Types.PrimitiveTypes.Real;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.DataType.DataType;
import AST.SymbolTable.Types.UserDefinedTypes.DataType.DataTypeRule;
import java.math.BigInteger;
import java.util.Objects;

public class TypeAlias implements Type {

    private Type type;
    private String name;

    public TypeAlias() {
        this(null, null);
    }

    public TypeAlias(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        if (type == null) {
            if (name == null) {
                name = VariableNameGenerator.generateTypeAliasName();
            }

            RandomTypeGenerator randomTypeGenerator = new RandomTypeGenerator();
            this.type = randomTypeGenerator.generateTypes(1, symbolTable).get(0)
                .concrete(symbolTable);
        }

        return new TypeAlias(type, name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Type)) {
            return false;
        }
        Type other = (Type) obj;

        if (other instanceof TypeAlias) {
            TypeAlias o = other.asTypeAlias();
            return this.name == null || o.name == null || this.name.equals(o.name)
                || this.type == null || o.type == null;
        }

        if (this.type == null) {
            return true;
        }

        return other.equals(this.type);
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
    public boolean operatorExists() {
        return type.operatorExists();
    }

    @Override
    public boolean isPrintable() {
        return type.isPrintable();
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
    public BigInteger cardinality(Object value) {
        return type.cardinality(value);
    }

    @Override
    public String concatenate(Object lhsV, Object rhsV) {
        return type.concatenate(lhsV, rhsV);
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
    public boolean validFunctionType() {
        return type.validFunctionType();
    }

    @Override
    public Object of(Object value) {
        return type.of(value);
    }

    @Override
    public boolean isOrdered() {
        return type.isOrdered();
    }

    public String declaration() {
        return String.format("type %s = %s", name, type.getVariableType());
    }

    @Override
    public UserDefinedType asUserDefinedType() {
        return (UserDefinedType) this.type;
    }

    @Override
    public Tuple asTuple() {
        return (Tuple) this.type;
    }

    @Override
    public DataType asDataType() {
        return (DataType) this.type;
    }

    @Override
    public DataTypeRule asDataTypeRule() {
        return (DataTypeRule) this.type;
    }

    @Override
    public BaseType asBaseType() {
        return (BaseType) this.type;
    }

    @Override
    public Real asReal() {
        return (Real) this.type;
    }

    @Override
    public Int asInt() {
        return (Int) this.type;
    }

    @Override
    public DString asDString() {
        return (DString) this.type;
    }

    @Override
    public Char asChar() {
        return (Char) this.type;
    }

    @Override
    public Bool asBool() {
        return (Bool) this.type;
    }

    @Override
    public GenericType asGenericType() {
        return (GenericType) this.type;
    }

    @Override
    public DMap asDMap() {
        return (DMap) this.type;
    }

    @Override
    public DCollection asDCollection() {
        return (DCollection) this.type;
    }

    @Override
    public DArray asDArray() {
        return (DArray) this.type;
    }

    @Override
    public DSet asDSet() {
        return (DSet) this.type;
    }

    @Override
    public Multiset asMultiset() {
        return (Multiset) this.type;
    }

    @Override
    public Seq asSeq() {
        return (Seq) this.type;
    }

    @Override
    public String formatEnsures(Object object) {
        return type.formatEnsures(object);
    }
}
