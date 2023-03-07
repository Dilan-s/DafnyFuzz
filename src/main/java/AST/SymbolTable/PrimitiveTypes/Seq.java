package AST.SymbolTable.PrimitiveTypes;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomStatementGenerator;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.SeqLiteral;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import java.util.Random;

public class Seq implements Type {

    public static final int MAX_SIZE_OF_SET = 20;
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
        return String.format("seq");
    }

    @Override
    public String getTypeIndicatorString() {
        if (type == null) {
            return ": seq";
        }
        return String.format(": seq<%s>", type.getName());
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
        SeqLiteral expression = new SeqLiteral(this);
        for (int i = 0; i < length; i++) {
            expression.addValue(expressionGenerator.generateExpression(type, symbolTable));
        }
        return expression;
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
