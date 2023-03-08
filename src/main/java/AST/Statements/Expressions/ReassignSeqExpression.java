package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import java.util.List;

public class ReassignSeqExpression implements Expression {

    private SymbolTable symbolTable;

    public ReassignSeqExpression(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public List<Type> getTypes() {
        return null;
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {

    }

    @Override
    public List<String> toCode() {
        return Expression.super.toCode();
    }
}
