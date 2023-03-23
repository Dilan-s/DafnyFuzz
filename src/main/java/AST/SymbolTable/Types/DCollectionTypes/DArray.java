package AST.SymbolTable.Types.DCollectionTypes;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.ArrayLiteral;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.DCollection;
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
    public String getTypeIndicatorString() {
        if (type == null) {
            return ": array";
        }
        return String.format(": array<%s>", type.getName());
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
    public boolean isSameType(Type other) {
        if (!(other instanceof DArray)) {
            return false;
        }

        DArray dsetOther = (DArray) other;

        if (type == null || dsetOther.type == null) {
            return true;
        }

        return dsetOther.type.isSameType(type);
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();

        length = GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_ARRAY) + 1;
        ArrayLiteral expression = new ArrayLiteral(symbolTable,this);
        for (int i = 0; i < length; i++) {
            expression.addValue(expressionGenerator.generateExpression(type, symbolTable));
        }
        return expression;
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        if (type == null) {
            RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
            Type t = typeGenerator.generateNonCollectionType(1, symbolTable);
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

    @Override
    public boolean isCollection() {
        return true;
    }
}
