package AST.SymbolTable.Types.DCollectionTypes;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.SeqLiteral;
import AST.SymbolTable.DCollection;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;

public class Seq implements DCollection {

    public static final int MAX_SIZE_OF_SET = 10;
    private Type type;

    public int getLength() {
        return length;
    }

    private int length;

    public Seq(Type type) {
        this.type = type;
    }

    public Seq() {
        this(null);
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
    public boolean isSameType(Type other) {
        if (!(other instanceof Seq)) {
            return false;
        }

        Seq dsetOther = (Seq) other;

        if (type == null || dsetOther.type == null) {
            return true;
        }

        return dsetOther.type.isSameType(type);
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();

        length = GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_SET) + 1;
        SeqLiteral expression = new SeqLiteral(symbolTable,this);
        for (int i = 0; i < length; i++) {
            expression.addValue(expressionGenerator.generateExpression(type, symbolTable));
        }
        return expression;
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
        return this;
    }

    @Override
    public boolean operatorExists() {
        return true;
    }

    @Override
    public boolean isPrintable() {
        return type != null && type.isPrintable();
    }

    @Override
    public boolean isCollection() {
        return true;
    }
}
