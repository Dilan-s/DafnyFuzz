package AST.SymbolTable.Types.DCollectionTypes;

import AST.Expressions.DSeq.SeqFuncLiteral;
import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomFunctionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Expressions.Expression;
import AST.Expressions.DSeq.SeqLiteral;
import AST.SymbolTable.Function;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.PrimitiveTypes.Char;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Seq implements DCollection {

    public static final int MAX_SIZE_OF_SEQ = 5;
    public static int MIN_SIZE_OF_SEQ = 0;
    public static final double PROB_USE_FUNC = 0.1;
    private Type type;
    public static int printDepth = 0;

    public Seq(Type type) {
        this.type = type;
    }

    public Seq() {
        this(null);
    }

    @Override
    public boolean validMethodType() {
        return type.validMethodType();
    }

    @Override
    public String getName() {
        return "seq";
    }

    @Override
    public Type setInnerType(Type type) {
        return new Seq(type);
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
        if (!(other instanceof Seq)) {
            return false;
        }

        Seq dsetOther = (Seq) other;

        if (type == null || dsetOther.type == null) {
            return true;
        }

        return dsetOther.type.equals(type);
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();
        RandomFunctionGenerator functionGenerator = new RandomFunctionGenerator();

        int length = MIN_SIZE_OF_SEQ + GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_SEQ);
        if (type.equals(new DArray())) {
            while (length == 1) {
                length = MIN_SIZE_OF_SEQ + GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_SEQ);
            }
        }

        double probType = GeneratorConfig.getRandom().nextDouble();
        if (type.validFunctionType() && probType < PROB_USE_FUNC) {
            Function f = functionGenerator.generateFunction(type, symbolTable, List.of(new Int()));
            SeqFuncLiteral expression = new SeqFuncLiteral(symbolTable, this, length, f);
            return expression;
        }

        List<Expression> values = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Type concrete = type.concrete(symbolTable);
            values.add(expressionGenerator.generateExpression(concrete, symbolTable));
        }
        SeqLiteral expression = new SeqLiteral(symbolTable,this, values);
        return expression;
    }

    @Override
    public Expression generateExpressionFromValue(SymbolTable symbolTable, Object value) {
        List<Object> vs = (List<Object>) value;
        List<Expression> values = new ArrayList<>();
        for (Object v : vs) {
            Expression exp = type.generateExpressionFromValue(symbolTable, v);
            if (exp == null) {
                return null;
            }
            values.add(exp);
        }
        return new SeqLiteral(symbolTable, this, values);
    }

    @Override
    public String getVariableType() {
        if (type == null) {
            return "seq";
        }
        return String.format("seq<%s>", type.getVariableType());
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        if (type == null) {
            RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
            Type t = typeGenerator.generateTypes(1, symbolTable).get(0);
            return new Seq(t);
        }
        return new Seq(type.concrete(symbolTable));
    }

    @Override
    public boolean operatorExists() {
        return true;
    }

    @Override
    public Boolean contains(Object lhsV, Object rhsV) {
        List<Object> rhsVL = (List<Object>) rhsV;
        return rhsVL.contains(lhsV);
    }

    @Override
    public Boolean disjoint(Object lhsV, Object rhsV) {
        List<Object> lhsVL = (List<Object>) lhsV;
        List<Object> rhsVL = (List<Object>) rhsV;

        return !lhsVL.containsAll(rhsVL);
    }

    @Override
    public boolean isPrintable() {
        return type != null && type.isPrintable();
    }

    @Override
    public Boolean lessThan(Object lhsV, Object rhsV) {
        List<Object> lhsVL = (List<Object>) lhsV;
        List<Object> rhsVL = (List<Object>) rhsV;

        if (lhsVL.size() >= rhsVL.size()) {
            return false;
        }

        for (int i = 0; i < lhsVL.size(); i++) {
            if (!Objects.equals(lhsVL.get(i), rhsVL.get(i))) {
                return false;
            }
        }
        return !(lhsVL.isEmpty() && rhsVL.isEmpty());
    }

    @Override
    public Boolean equal(Object lhsV, Object rhsV) {
        List<Object> lhsVL = (List<Object>) lhsV;
        List<Object> rhsVL = (List<Object>) rhsV;

        if (lhsVL.size() != rhsVL.size()) {
            return false;
        }

        for (int i = 0; i < lhsVL.size(); i++) {
            if (!Objects.equals(lhsVL.get(i), rhsVL.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String formatEnsures(String variableName, Object object) {
        if (type == null) {
            return null;
        }
        List<Object> value = (List<Object>) object;
        List<String> res = new ArrayList<>();

        res.add(String.format("|%s| == %d", variableName, value.size()));

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
    public String formatPrint(Object object) {
        printDepth ++;
        String res;
        List<Object> value = (List<Object>) object;
//        if (type.equals(new Char()) && printDepth < 2) {
//            res = value.stream()
//                .map(v -> ((Char) type).formatPrintWithNoQuotes(v))
//                .collect(Collectors.joining(""));
//        } else {
            res =
                "[" + value.stream().map(v -> type.formatPrint(v)).collect(Collectors.joining(", "))
                    + "]";
//        }
        printDepth--;
        return res;
    }

    @Override
    public Object union(Object lhsV, Object rhsV) {
        List<Object> lhsVL = (List<Object>) lhsV;
        List<Object> rhsVL = (List<Object>) rhsV;

        List<Object> res = new ArrayList<>(lhsVL);
        res.addAll(rhsVL);
        return res;
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
    public Object of(Object value) {
        List<Object> r = new ArrayList<>();

        List<Object> vs = (List<Object>) value;
        for (Object v : vs) {
            r.add(type != null ? type.of(v) : v);
        }

        return r;
    }
}
