package AST.SymbolTable.DCollectionTypes;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.DSetLiteral;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.DCollection;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;

public class DSet implements DCollection {

    public static final int MAX_SIZE_OF_SET = 10;
    private Type type;

    public DSet(Type type) {
        this.type = type;
    }

    public DSet() {
        this(null);
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getTypeIndicatorString() {
        return String.format(": set<%s>", type.getName());
    }

    @Override
    public Type setInnerType(Type type) {
        return new DSet(type);
    }

    @Override
    public Type getInnerType() {
        return type;
    }

    @Override
    public boolean isSameType(Type other) {
        if (!(other instanceof DSet)) {
            return false;
        }

        DSet dsetOther = (DSet) other;

        if (type == null || dsetOther.type == null) {
            return true;
        }

        return dsetOther.type.isSameType(type);
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();

        int noOfElems = GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_SET) + 1;
        DSetLiteral expression = new DSetLiteral(symbolTable, type);
        for (int i = 0; i < noOfElems; i++) {
            expression.addValue(expressionGenerator.generateExpression(type, symbolTable));
        }
        return expression;
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        if (type == null) {
            RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
            Type t = typeGenerator.generateNonCollectionType(1, symbolTable);
            return new DSet(t);
        }
        return this;
    }


    @Override
    public boolean operatorExists() {
        return true;
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
