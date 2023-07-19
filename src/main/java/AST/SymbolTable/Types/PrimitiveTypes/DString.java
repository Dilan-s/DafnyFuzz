package AST.SymbolTable.Types.PrimitiveTypes;

import AST.Expressions.Expression;
import AST.Expressions.StringLiteral;
import AST.Generator.GeneratorConfig;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.Objects;
import org.apache.commons.lang3.RandomStringUtils;

public class DString implements BaseType {

    private static final int MAX_STRING_SIZE = 20;

    public DString() {
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
        String s = RandomStringUtils.random(size, 0, 0, true, true, null, GeneratorConfig.getRandom());
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
        return false;
    }

    @Override
    public Boolean equal(Object lhsV, Object rhsV) {
        String lhsVS = lhsV.toString();
        String rhsVS = rhsV.toString();
        return Objects.equals(lhsVS, rhsVS);
    }

    @Override
    public String formatPrint(Object object) {
        return object.toString();
    }

    @Override
    public String formatEnsures(String variableName, Object object) {
        if (object == null) {
            return null;
        }
        String v = object.toString();

        return String.format("(%s == \"%s\")", variableName, v);
    }
}
