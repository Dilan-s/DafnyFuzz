package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Char;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.List;

public class CharLiteral implements Expression {

    private final char value;
    private SymbolTable symbolTable;

    public CharLiteral(SymbolTable symbolTable, char value) {
        this.symbolTable = symbolTable;
        this.value = value;
    }

    @Override
    public List<Type> getTypes() {
        return List.of(new Char());
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {
    }

    @Override
    public String toString() {
        return String.format("'%c'", value);
    }
}
