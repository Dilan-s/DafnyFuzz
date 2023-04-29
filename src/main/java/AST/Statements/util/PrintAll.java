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
import java.util.stream.Collectors;

public class PrintAll implements Statement {

    private final List<Variable> variables;
    private SymbolTable symbolTable;

    public PrintAll(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.variables = symbolTable.getAllVariablesInCurrentScope().stream()
            .filter(v -> v.getType().isPrintable())
            .collect(Collectors.toList());
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

        PrintStatement statement = new PrintStatement(symbolTable);

        for (Variable v : variables) {
            StringLiteral stringLiteral = new StringLiteral(new DString(), symbolTable, v.getName());
            VariableExpression expression = new VariableExpression(symbolTable, v, v.getType());
            statement.addValue(stringLiteral);
            statement.addValue(expression);
        }
        return statement.expand();
    }

    @Override
    public void incrementUse() {
    }

    @Override
    public int getNoOfUses() {
        return 0;
    }

    @Override
    public String minimizedTestCase() {
        return null;
    }
}
