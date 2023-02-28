package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.SymbolTable.Method;
import AST.SymbolTable.PrimitiveTypes.Int;
import AST.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import java.util.List;

public class NumericLiteral implements Expression {

    private final int value;
    private SymbolTable symbolTable;

    public NumericLiteral(int value) {
        this.value = value;
    }


    @Override
    public List<Type> getTypes() {
        return List.of(new Int());
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {
    }

    @Override
    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
