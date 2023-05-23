package AST.SymbolTable.Types.DCollectionTypes;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.Array.ArrayValue;
import AST.Statements.Expressions.Array.DArrayLiteralByElements;
import AST.Statements.Expressions.Array.DArrayLiteralInline;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.VariableExpression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DArray implements DCollection {

    public static final int MAX_SIZE_OF_ARRAY = 3;
    public static final double PROB_EXPAND = 0.9;
    public static final int MIN_SIZE_OF_ARRAY = 3;
    private Type type;

    public DArray(Type type) {
        this.type = type;
    }

    public DArray() {
        this(null);
    }

    @Override
    public boolean validMethodType() {
        return type.validMethodType();
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

        DArray dArray = (DArray) other;

        if (type == null || dArray.type == null) {
            return true;
        }

        return dArray.type.equals(type);
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();

        int length = GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_ARRAY) + MIN_SIZE_OF_ARRAY;

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
    public Expression generateExpressionFromValue(SymbolTable symbolTable, Object value) {
        ArrayValue vs = (ArrayValue) value;
        Variable v = vs.getVariable();
        if (symbolTable.variableInScope(v)) {
            return new VariableExpression(symbolTable, v, this);
        }
        return null;
    }

    @Override
    public Boolean lessThan(Object lhsV, Object rhsV) {
        return null;
    }

    @Override
    public Boolean equal(Object lhsV, Object rhsV) {
        ArrayValue lhsAV = (ArrayValue) lhsV;
        ArrayValue rhsAV = (ArrayValue) rhsV;

        return lhsAV.getName().equals(rhsAV.getName()) &&
            Objects.equals(lhsAV.getContents(), rhsAV.getContents()) && lhsAV.getNum() == rhsAV.getNum();
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
            Type t = typeGenerator.generateTypes(1, symbolTable).get(0).concrete(symbolTable);
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
        ArrayValue value = (ArrayValue) object;
        return value.getName();
    }

    @Override
    public String formatEnsures(String variableName, Object object) {
        if (type == null) {
            return null;
        }
        ArrayValue value = (ArrayValue) object;
        List<String> res = new ArrayList<>();

        res.add(String.format("%s.Length == %d", variableName, value.size()));

        for (int i = 0; i < value.size(); i++) {
            String e = type.formatEnsures(String.format("%s[%d]", variableName, i), value.get(i));
            if (e == null) {
                return null;
            }
            res.add(e);
        }
        return String.join(" && ", res);
    }

    @Override
    public boolean isOrdered() {
        return false;
    }
}
