package AST.SymbolTable.Types.DCollectionTypes;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.ArrayLiteral;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.List;

public class DArray implements DCollection {

    public static final int MAX_SIZE_OF_ARRAY = 10;
    private List<Expression> sequence;
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

        List<Expression> values = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            values.add(expressionGenerator.generateExpression(type.concrete(symbolTable), symbolTable));
        }
        ArrayLiteral expression = new ArrayLiteral(symbolTable, this, values);
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
        return new DArray(type.concrete(symbolTable));
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
