package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.SymbolTable.Method;
import AST.SymbolTable.PrimitiveTypes.Int;
import AST.SymbolTable.PrimitiveTypes.Real;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import java.util.List;

public class RealLiteral implements Expression {

    private final double value;
    private SymbolTable symbolTable;

    public RealLiteral(double value) {
        this.value = value;
    }

    @Override
    public List<Type> getTypes() {
        return List.of(new Real());
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
        return String.format("%.2f", value);
    }
}
