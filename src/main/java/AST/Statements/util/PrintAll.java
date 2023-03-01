package AST.Statements.util;

import AST.Errors.SemanticException;
import AST.Statements.Expressions.VariableExpression;
import AST.Statements.PrintStatement;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.List;

public class PrintAll implements Statement {

    private SymbolTable symbolTable;

    public PrintAll(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }



    @Override
    public void semanticCheck(Method method) throws SemanticException {

    }

    @Override
    public List<String> toCode() {
        List<String> code = new ArrayList<>();
        List<Variable> allVariablesInCurrentScope = symbolTable.getAllVariablesInCurrentScope();
        for (Variable v : allVariablesInCurrentScope) {
            PrintStatement statement = new PrintStatement(symbolTable);
            VariableExpression expression = new VariableExpression(v);
            expression.setSymbolTable(symbolTable);
            statement.addValue(expression);
            code.addAll(statement.toCode());
        }
        return code;
    }
}
