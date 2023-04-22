package AST.SymbolTable.Types.UserDefinedTypes;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.Tuple.TupleLiteral;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Tuple implements UserDefinedType {

    public static final int MAX_SIZE_OF_TUPLE = 5;
    public static final int MIN_SIZE_OF_TUPLE = 2;
    List<Type> typeList;

    public Tuple() {
        this.typeList = null;
    }

    public Tuple(List<Type> typeList) {
        this.typeList = typeList;
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
        return null;
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

            res = res + type.formatPrint(val);
        }

        res = res + ")";

        return res;
    }

    @Override
    public boolean isPrintable() {
        return typeList.stream().allMatch(Type::isPrintable);
    }

    public Type getType(int i) {
        return typeList.get(i);
    }
}
