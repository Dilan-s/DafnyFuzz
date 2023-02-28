package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.SymbolTable.Method;
import AST.SymbolTable.PrimitiveTypes.Bool;
import AST.SymbolTable.PrimitiveTypes.Char;
import AST.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import java.util.List;

public class CharLiteral implements Expression {

    private final char value;
    private SymbolTable symbolTable;

    public CharLiteral(char value) {
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
    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public String toString() {
        return String.format("'%c'", value);
    }
}
