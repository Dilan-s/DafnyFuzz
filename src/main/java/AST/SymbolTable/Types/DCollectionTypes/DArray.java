package AST.SymbolTable.Types.DCollectionTypes;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.ArrayLiteral;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;

public class DArray implements DCollection {

    public static final int MAX_SIZE_OF_ARRAY = 10;
    private Type type;

    public int getLength() {
        return length;
    }

    private int length;

    public DArray(Type type) {
        this.type = type;
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

        length = GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_ARRAY) + 1;
        ArrayLiteral expression = new ArrayLiteral(symbolTable, this);
        for (int i = 0; i < length; i++) {
            expression.addValue(expressionGenerator.generateExpression(type, symbolTable));
        }
        return expression;
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
        return this;
    }

    @Override
    public boolean operatorExists() {
        return false;
    }

    @Override
    public boolean isPrintable() {
        return false;
    }
}
