package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import java.util.List;

public class SequenceIndexExpression implements Expression {

    Expression sequence;
    Expression index;
    private SymbolTable symbolTable;


    @Override
    public List<Type> getTypes() {
        return null;
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {

    }

    @Override
    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }
}
