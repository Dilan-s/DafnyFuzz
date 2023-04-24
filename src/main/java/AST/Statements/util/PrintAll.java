package AST.Statements.util;

import AST.Errors.SemanticException;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.StringLiteral;
import AST.Statements.Expressions.VariableExpression;
import AST.Statements.PrintStatement;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.PrimitiveTypes.DString;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PrintAll implements Statement {

    private SymbolTable symbolTable;

    public PrintAll(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public List<String> toOutput() {
        return Collections.emptyList();
    }

    @Override
    public List<Object> execute(Map<Variable, Variable> paramMap, StringBuilder s) {
        return null;
    }

    @Override
    public List<Statement> expand() {

        List<Variable> allVariablesInCurrentScope = symbolTable.getAllVariablesInCurrentScope();
        PrintStatement statement = new PrintStatement(symbolTable);

        for (Variable v : allVariablesInCurrentScope) {
            if (v.getType().isPrintable()) {
                StringLiteral stringLiteral = new StringLiteral(new DString(), symbolTable, v.getName());
                VariableExpression expression = new VariableExpression(symbolTable, v, v.getType());
                statement.addValue(stringLiteral);
                statement.addValue(expression);
            }
        }
        return statement.expand();
    }
}
