package AST.SymbolTable.Types.DCollectionTypes;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.Array.DArrayLiteralByElements;
import AST.Statements.Expressions.Array.DArrayLiteralInline;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.List;

public class DArray implements DCollection {

    public static final int MAX_SIZE_OF_ARRAY = 10;
    public static final double PROB_EXPAND = 0.8;
    private Type type;

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

        int length = GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_ARRAY) + 1;

        List<Expression> values = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Type t = type.concrete(symbolTable);
            Expression exp = expressionGenerator.generateExpression(t, symbolTable);

            values.add(exp);
        }

        double probInlineInit = GeneratorConfig.getRandom().nextDouble();
        if (probInlineInit < PROB_EXPAND) {
            DArrayLiteralInline expression = new DArrayLiteralInline(symbolTable, this, values);
            return expression;
        } else {
            DArrayLiteralByElements expression = new DArrayLiteralByElements(symbolTable, this, values);
            return expression;
        }
    }

    @Override
    public Boolean lessThan(Object lhsV, Object rhsV) {
        return null;
    }

    @Override
    public Boolean equal(Object lhsV, Object rhsV) {
        return null;
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
    public Boolean disjoint(Object lhsV, Object rhsV) {
        return null;
    }

    @Override
    public Object union(Object lhsV, Object rhsV) {
        return null;
    }

    @Override
    public Object difference(Object lhsV, Object rhsV) {
        return null;
    }

    @Override
    public Object intersection(Object lhsV, Object rhsV) {
        return null;
    }

    @Override
    public Boolean contains(Object lhsV, Object rhsV) {
        return null;
    }

    @Override
    public boolean isPrintable() {
        return false;
    }

    @Override
    public String formatPrint(Object object) {
        return "";
    }
}
