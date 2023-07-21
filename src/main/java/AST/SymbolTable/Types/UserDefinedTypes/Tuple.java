package AST.SymbolTable.Types.UserDefinedTypes;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Expressions.Expression;
import AST.Expressions.Tuple.TupleLiteral;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.DCollectionTypes.Seq;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Tuple implements UserDefinedType {

    public static final int MAX_SIZE_OF_TUPLE = 2;
    public static final int MIN_SIZE_OF_TUPLE = 2;
    List<Type> typeList;

    public Tuple() {
        this.typeList = null;
    }

    public Tuple(List<Type> typeList) {
        this.typeList = typeList;
    }

    @Override
    public boolean validMethodType() {
        return typeList.stream().allMatch(Type::validMethodType);
    }

    @Override
    public String getName() {
        return String.format("%s", typeList.stream()
            .map(Type::getName)
            .collect(Collectors.joining("_")));
    }

    @Override
    public String getVariableType() {
        return String.format("(%s)", typeList.stream()
            .map(Type::getVariableType)
            .collect(Collectors.joining(", ")));
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();
        List<Expression> values = new ArrayList<>();
        for (int i = 0; i < typeList.size(); i++) {
            Type concrete = typeList.get(i).concrete(symbolTable);
            values.add(expressionGenerator.generateExpression(concrete, symbolTable));
        }
        return new TupleLiteral(symbolTable, this, values);
    }

    @Override
    public Expression generateExpressionFromValue(SymbolTable symbolTable, Object value) {
        List<Object> vs = (List<Object>) value;
        List<Expression> values = new ArrayList<>();
        for (int i = 0; i < typeList.size(); i++) {
            Expression exp = typeList.get(i).generateExpressionFromValue(symbolTable, vs.get(i));
            if (exp == null) {
                return null;
            }
            values.add(exp);
        }
        return new TupleLiteral(symbolTable, this, values);
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        if (typeList == null) {
            int noTypes = GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_TUPLE) + MIN_SIZE_OF_TUPLE;
            RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
            List<Type> types = typeGenerator.generateTypes(noTypes, symbolTable);
            return new Tuple(types);
        }
        return new Tuple(typeList.stream()
            .map(t -> t.concrete(symbolTable))
            .collect(Collectors.toList()));
    }

    @Override
    public Boolean lessThan(Object lhsV, Object rhsV) {
        return null;
    }

    @Override
    public Boolean equal(Object lhsV, Object rhsV) {
        List<Object> lhsVL = (List<Object>) lhsV;
        List<Object> rhsVL = (List<Object>) rhsV;
        for (int i = 0; i < typeList.size(); i++) {
            if (!typeList.get(i).equal(lhsVL.get(i), rhsVL.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String formatPrint(Object object) {
        String res = "(";
        List<Object> value = (List<Object>) object;

        boolean first = true;
        for (int i = 0; i < typeList.size(); i++) {
            Type type = typeList.get(i);
            Object val = value.get(i);

            if (!first) {
                res = res + ", ";
            }
            first = false;
            Seq.printDepth++;
            res = res + type.formatPrint(val);
            Seq.printDepth--;
        }

        res = res + ")";

        return res;
    }

    @Override
    public String formatEnsures(String variableName, Object object) {
        if (typeList == null) {
            return null;
        }

        List<String> res = new ArrayList<>();
        List<Object> value = (List<Object>) object;

        for (int i = 0; i < typeList.size(); i++) {
            Type t = typeList.get(i);
            Object v = value.get(i);

            String element = t.formatEnsures(String.format("(%s).%d", variableName, i), v);

            if (element == null) {
                return null;
            }
            res.add(element);

        }
        return String.join(" && ", res);
    }



    @Override
    public boolean isPrintable() {
        return typeList.stream().allMatch(Type::isPrintable);
    }

    public Type getType(int i) {
        return typeList.get(i);
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
        if (!(other instanceof Tuple)) {
            return false;
        }

        Tuple tupleOther = (Tuple) other;

        if (typeList == null || tupleOther.typeList == null) {
            return true;
        }

        if (tupleOther.typeList.size() != typeList.size()) {
            return false;
        }

        for (int i = 0; i < typeList.size(); i++) {
            Type type = typeList.get(i);
            Type otype = tupleOther.typeList.get(i);

            if (type != null && otype != null && !type.equals(otype)) {
                return false;
            }
        }

        return true;
    }

    public int getNoOfType() {
        return typeList.size();
    }
}
