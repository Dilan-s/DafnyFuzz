package AST.Statements.util;

import AST.Errors.SemanticException;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.VariableExpression;
import AST.Statements.PrintStatement;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        PrintStatement statement = new PrintStatement(symbolTable);
        for (Variable v : allVariablesInCurrentScope) {
            VariableExpression expression = new VariableExpression(symbolTable, v, v.getType());
            statement.addValue(expression);
        }
        code.addAll(statement.toCode());
        return code;
    }

    @Override
    public ReturnStatus assignReturnIfPossible(Method method, ReturnStatus currStatus, List<Expression> dependencies) {
        return currStatus;
    }

    @Override
    public List<Object> execute(Map<Variable, Variable> paramMap) {
        return null;
    }

    @Override
    public List<Statement> expand() {
        return new ArrayList<>();
    }
}
