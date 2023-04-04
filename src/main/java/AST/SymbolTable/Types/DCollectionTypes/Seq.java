package AST.SymbolTable.Types.DCollectionTypes;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.SeqLiteral;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.AbstractType;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.List;

public class Seq extends AbstractType implements DCollection{

    public static final int MAX_SIZE_OF_SET = 10;
    private List<Expression> sequence;
    private Type type;

    public Seq(Type type) {
        this.type = type;
        this.sequence = null;
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
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public Object getValue() {
        return sequence;
    }

    @Override
    public void setValue(Object value) {
        this.sequence = (List<Expression>) value;
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

        this.sequence = new ArrayList<>();
        int length = GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_SET) + 1;
        List<Expression> values = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Type concrete = type.concrete(symbolTable);
            Expression exp = expressionGenerator.generateExpression(concrete, symbolTable);
            Expression expLiteral = concrete.generateLiteral(symbolTable, exp, concrete.getValue());
            values.add(exp);
            sequence.add(expLiteral);
        }
        SeqLiteral expression = new SeqLiteral(symbolTable,this, values);
        return expression;
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable, Object value) {
        Type t = this.concrete(symbolTable);
        t.setValue(value);
        return new SeqLiteral(symbolTable, t, (List<Expression>) value);
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
    public int getSize() {
        return sequence.size();
    }

    @Override
    public boolean contains(Expression val) {
        return sequence.contains(val);
    }

    @Override
    public boolean disjoint(DCollection rhs) {
        Seq rhsSeq = (Seq) rhs;
        return rhsSeq.sequence.stream().noneMatch(this::contains);
    }

    @Override
    public boolean isPrintable() {
        return type != null && type.isPrintable();
    }

    @Override
    public boolean lessThanOrEqual(Type rhsT) {
        Seq rhsSeq = (Seq) rhsT;

        if (sequence.size() > rhsSeq.getSize()) {
            return false;
        }

        for (int i = 0; i < sequence.size(); i++) {
            if (!sequence.get(i).equals(rhsSeq.sequence.get(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean lessThan(Type rhsT) {
        Seq rhsSeq = (Seq) rhsT;

        if (sequence.size() > rhsSeq.getSize()) {
            return false;
        }

        for (int i = 0; i < sequence.size(); i++) {
            if (!sequence.get(i).equals(rhsSeq.sequence.get(i))) {
                return false;
            }
        }

        return rhsSeq.sequence.size() > sequence.size();
    }

    @Override
    public boolean equal(Type rhsT) {
        Seq rhsSeq = (Seq) rhsT;

        if (sequence.size() != rhsSeq.getSize()) {
            return false;
        }

        for (int i = 0; i < sequence.size(); i++) {
            if (!sequence.get(i).equals(rhsSeq.sequence.get(i))) {
                return false;
            }
        }

        return true;
    }

    public Object get(Object value) {
        return sequence.get((Integer) value).getTypes().get(0).getValue();
    }

    public Object subsequence(Object indI, Object indJ) {
        Integer i = (Integer) indI;
        Integer j = (Integer) indJ;

        int min = Math.min(i, j);
        int max = Math.max(i, j);
        return sequence.subList(min, max);
    }

    @Override
    public Object union(DCollection rhs) {
        Seq rhsSeq = (Seq) rhs;
        List<Expression> res = new ArrayList<>(this.sequence);
        res.addAll(rhsSeq.sequence);
        return res;
    }

    public Object reassignIndex(Integer i, Expression value) {
        List<Expression> res = new ArrayList<>();
        List<Expression> curr = new ArrayList<>(sequence);
        res.addAll(curr.subList(0, i));
        res.add(value);
        res.addAll(curr.subList(i + 1, curr.size()));
        return res;
    }
}
