package AST.SymbolTable.Types.DCollectionTypes;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.DSetLiteral;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DSet implements DCollection {

    public static final int MAX_SIZE_OF_SET = 10;
    private Set<Expression> set;
    private Type type;

    public DSet(Type type) {
        this.type = type;
        this.set = new HashSet<>();
    }

    public DSet() {
        this(null);
    }

    @Override
    public String getName() {
        return "set";
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
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Type)) {
            return false;
        }
        Type other = (Type) obj;
        if (!(other instanceof DSet)) {
            return false;
        }

        DSet dsetOther = (DSet) other;

        if (type == null || dsetOther.type == null) {
            return true;
        }

        return dsetOther.type.equals(type);
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();

        int noOfElems = GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_SET) + 1;
        List<Expression> values = new ArrayList<>();
        for (int i = 0; i < noOfElems; i++) {
            Expression exp = expressionGenerator.generateExpression(type.concrete(symbolTable), symbolTable);
            values.add(exp);
            set.add(exp);
        }
        DSetLiteral expression = new DSetLiteral(symbolTable, this, values);
        return expression;
    }

    @Override
    public String getVariableType() {
        if (type == null) {
            return "set";
        }
        return String.format("set<%s>", type.getVariableType());
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        if (type == null) {
            RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
            Type t = typeGenerator.generateTypes(1, symbolTable).get(0);
            return new DSet(t);
        }
        return new DSet(type.concrete(symbolTable));
    }

    public Set<Expression> getSet() {
        return set;
    }

    @Override
    public boolean operatorExists() {
        return true;
    }

    @Override
    public boolean isPrintable() {
        return false;
    }
}
