package AST.SymbolTable.Types.PrimitiveTypes;

import AST.Expressions.Expression;
import AST.Expressions.StringLiteral;
import AST.Generator.GeneratorConfig;
import AST.Statements.util.RandomStringUtils;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;

public class DString implements BaseType {

    private static final int MAX_STRING_SIZE = 20;
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
        return false;
    }


    @Override
    public Boolean lessThan(Object lhsV, Object rhsV) {
        String lhs = (String) lhsV;
        String rhs = (String) rhsV;
        return rhs.startsWith(lhs);
    }

    @Override
    public Boolean equal(Object lhsV, Object rhsV) {
        String lhs = (String) lhsV;
        String rhs = (String) rhsV;
        return lhs.compareTo(rhs) == 0;
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
}
