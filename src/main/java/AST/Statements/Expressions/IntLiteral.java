package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.SymbolTable.Method;
import AST.SymbolTable.PrimitiveTypes.Int;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import java.util.List;

public class IntLiteral implements Expression {

    private final int value;
    private final boolean asHex;
    private SymbolTable symbolTable;

    public IntLiteral(SymbolTable symbolTable, int value, boolean asHex) {
        this.symbolTable = symbolTable;
        this.value = value;
        this.asHex = asHex;
    }

    public IntLiteral(SymbolTable symbolTable, int value) {
        this(symbolTable, value, false);
    }


    @Override
    public List<Type> getTypes() {
        return List.of(new Int());
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {
    }

    @Override
    public String toString() {
        if (asHex) {
            return String.format("0x%X", value);
        }
        return String.valueOf(value);
    }
}
