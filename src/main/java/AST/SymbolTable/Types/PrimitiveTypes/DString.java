package AST.SymbolTable.Types.PrimitiveTypes;

import AST.Expressions.Expression;
import AST.Expressions.StringLiteral;
import AST.Generator.GeneratorConfig;
import AST.Statements.util.RandomStringUtils;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.DCollectionTypes.Seq;
import AST.SymbolTable.Types.Type;
import java.math.BigInteger;
import java.util.List;

public class DString implements BaseType {

    private static final int MAX_STRING_SIZE = 20;
    private static final double PROB_USE_SEQ_CHAR = 0.2;
    private static final Type SEQ_CHAR = new Seq(new Char());
    private boolean isPrintable;

    public DString(boolean isPrintable) {
        this.isPrintable = isPrintable;
    }

    public DString() {
        this(false);
    }

    @Override
    public String getName() {
        return "string";
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
        return other instanceof DString;
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        double probUseSeq = GeneratorConfig.getRandom().nextDouble();
        if (probUseSeq < PROB_USE_SEQ_CHAR) {
            Expression expression = SEQ_CHAR.generateLiteral(symbolTable);
            expression.setType(List.of(new DString()));
            return expression;
        }

        int size = GeneratorConfig.getRandom().nextInt(MAX_STRING_SIZE);
        String s = RandomStringUtils.generateRandomString(size);
        return new StringLiteral(this, symbolTable, s);
    }

    @Override
    public Expression generateExpressionFromValue(SymbolTable symbolTable, Object value) {
        String v = value.toString();
        return new StringLiteral(this, symbolTable, v);
    }

    @Override
    public boolean validMethodType() {
        return true;
    }

    @Override
    public boolean validFunctionType() {
        return true;
    }

    @Override
    public boolean operatorExists() {
        return true;
    }


    @Override
    public Boolean lessThan(Object lhsV, Object rhsV) {
        String lhs = valueToString(lhsV);
        String rhs = valueToString(rhsV);
        return rhs.startsWith(lhs);
    }

    @Override
    public Boolean equal(Object lhsV, Object rhsV) {
        String lhs = valueToString(lhsV);
        String rhs = valueToString(rhsV);
        return lhs.compareTo(rhs) == 0;
    }

    @Override
    public BigInteger cardinality(Object value) {
        String valS = valueToString(value);
        return BigInteger.valueOf(valS.length());
    }

    @Override
    public String concatenate(Object lhsV, Object rhsV) {

        String lhsVS = valueToString(lhsV);
        String rhsVS = valueToString(rhsV);

        return lhsVS + rhsVS;
    }

    private String valueToString(Object value) {
        String vs;
        if (value instanceof List) {
            String v = "";
            List<Character> chrs = (List<Character>) value;
            for (Character c : chrs) {
                v += c;
            }
            vs = v;
        } else {
            vs = (String) value;
        }
        return vs;
    }

    @Override
    public String formatPrint(Object object) {
        return "" + object + "";
    }

    @Override
    public String formatEnsures(String variableName, Object object) {
        if (object == null) {
            return null;
        }
        String v = object.toString();

        return String.format("(%s == \"%s\")", variableName, v);
    }

    @Override
    public boolean isPrintable() {
        return isPrintable;
    }

    @Override
    public Object of(Object value) {
        if (value instanceof List) {
            String v = "";
            List<Character> chrs = (List<Character>) value;
            for (Character c : chrs) {
                v += c;
            }
            return v;
        }
        return BaseType.super.of(value);
    }
}
