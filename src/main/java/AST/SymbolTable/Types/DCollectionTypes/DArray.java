package AST.SymbolTable.Types.DCollectionTypes;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.ArrayLiteral;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.AbstractType;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.List;

public class DArray extends AbstractType implements DCollection {

    public static final int MAX_SIZE_OF_ARRAY = 10;
    private List<Expression> array;
    private Type type;

    public int getLength() {
        return length;
    }

    private int length;

    public DArray(Type type) {
        this.type = type;
        this.array = null;
    }

    public DArray() {
        this(null);
    }

    @Override
    public String getName() {
        return "array";
    }

    @Override
    public Type setInnerType(Type type) {
        return new DArray(type);
    }

    @Override
    public Type getInnerType() {
        return type;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Type)) {
            return false;
        }
        Type other = (Type) obj;
        if (!(other instanceof DArray)) {
            return false;
        }

        DArray dsetOther = (DArray) other;

        if (type == null || dsetOther.type == null) {
            return true;
        }

        return dsetOther.type.equals(type);
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();

        this.array = new ArrayList<>();
        length = GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_ARRAY) + 1;

        List<Expression> values = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Type t = type.concrete(symbolTable);
            Expression exp = expressionGenerator.generateExpression(t, symbolTable);

            Expression expLiteral = t.generateLiteral(symbolTable, exp, t.getValue());

            values.add(exp);
            array.add(expLiteral);
        }
        ArrayLiteral expression = new ArrayLiteral(symbolTable, this, values);
        return expression;
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable, Object value) {
        Type t = this.concrete(symbolTable);
        t.setValue(value);
        return new ArrayLiteral(symbolTable, t, (List<Expression>) value, false);
    }

    @Override
    public String getVariableType() {
        if (type == null) {
            return "array";
        }
        return String.format("array<%s>", type.getVariableType());
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        if (type == null) {
            RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
            Type t = typeGenerator.generateTypes(1, symbolTable).get(0);
            return new DArray(t);
        }
        return new DArray(type.concrete(symbolTable));
    }

    @Override
    public void setValue(Object value) {
        this.array = (List<Expression>) value;
    }

    @Override
    public Object getValue() {
        return array;
    }

    @Override
    public boolean operatorExists() {
        return false;
    }

    @Override
    public int getSize() {
        return array.size();
    }

    @Override
    public boolean contains(Expression val) {
        return array.contains(val);
    }

    @Override
    public boolean disjoint(DCollection rhs) {
        DArray rhsSeq = (DArray) rhs;
        return rhsSeq.array.stream().noneMatch(this::contains);
    }

    @Override
    public Object union(DCollection rhs) {
        return null;
    }

    @Override
    public boolean isPrintable() {
        return false;
    }
}
