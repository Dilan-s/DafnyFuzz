package AST.SymbolTable.Types.DCollectionTypes;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.DSetLiteral;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.AbstractType;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DSet extends AbstractType implements DCollection{

    public static final int MAX_SIZE_OF_SET = 10;
    private Set<Expression> set;
    private Type type;

    public DSet(Type type) {
        this.type = type;
        this.set = null;
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

        this.set = new HashSet<>();
        int noOfElems = GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_SET) + 1;
        List<Expression> values = new ArrayList<>();
        for (int i = 0; i < noOfElems; i++) {
            Type t = type.concrete(symbolTable);

            Expression exp = expressionGenerator.generateExpression(t, symbolTable);
            Expression expLiteral = t.generateLiteral(symbolTable, exp, t.getValue());

            values.add(exp);
            set.add(expLiteral);
        }
        DSetLiteral expression = new DSetLiteral(symbolTable, this, values);
        return expression;
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable, Object value) {
        Type t = this.concrete(symbolTable);
        t.setValue(value);
        return new DSetLiteral(symbolTable, t, new ArrayList<>((Set<Expression>) value));
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

    @Override
    public Object getValue() {
        return set;
    }

    @Override
    public void setValue(Object value) {
        this.set = (Set<Expression>) value;
    }

    @Override
    public boolean operatorExists() {
        return true;
    }

    @Override
    public int getSize() {
        return set.size();
    }

    @Override
    public boolean contains(Expression val) {
        return set.contains(val);
    }

    @Override
    public boolean disjoint(DCollection rhs) {
        DSet rhsSet = (DSet) rhs;
        return rhsSet.set.stream().noneMatch(this::contains);
    }

    @Override
    public boolean isPrintable() {
        return false;
    }

    public Set<Expression> intersection(DSet rhsSet) {
        Set<Expression> s = new HashSet<>(set);
        s.retainAll(rhsSet.set);
        return s;
    }

    public Set<Expression> difference(DSet rhsSet) {
        Set<Expression> s = new HashSet<>(set);
        s.retainAll(rhsSet.set);
        return s;
    }

    @Override
    public Object union(DCollection rhs) {
        DSet rhsSet = (DSet) rhs;
        Set<Expression> s = new HashSet<>(set);
        s.addAll(rhsSet.set);
        return s;
    }

    @Override
    public boolean lessThan(Type rhsT) {
        DSet rhsDset = (DSet) rhsT;
        return rhsDset.set.containsAll(set) && !set.containsAll(rhsDset.set);
    }

    @Override
    public boolean lessThanOrEqual(Type rhsT) {
        DSet rhsDset = (DSet) rhsT;
        return rhsDset.set.containsAll(set);
    }

    @Override
    public boolean equal(Type rhsT) {
        DSet rhsDset = (DSet) rhsT;
        return rhsDset.set.containsAll(set) && set.containsAll(rhsDset.set);
    }
}
