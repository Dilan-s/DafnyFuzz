package AST.Statements.util;

import AST.Errors.SemanticException;
import AST.Statements.BaseStatement;
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
import java.util.prefs.BackingStoreException;
import java.util.stream.Collectors;

public class PrintAll extends BaseStatement {

    private final List<Variable> variables;
    private final PrintStatement printStat;
    private SymbolTable symbolTable;

    public PrintAll(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.variables = symbolTable.getAllVariablesInCurrentScope().stream()
            .filter(v -> v.getType().isPrintable())
            .collect(Collectors.toList());

        this.printStat = new PrintStatement(symbolTable);

        variables.forEach(v -> {
            StringLiteral stringLiteral = new StringLiteral(new DString(), symbolTable, v.getName());
            VariableExpression expression = new VariableExpression(symbolTable, v, v.getType());
            printStat.addValue(stringLiteral);
            printStat.addValue(expression);
        });
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
    public boolean requireUpdate() {
        return printStat.requireUpdate();
    }

    @Override
    public List<Statement> expand() {
        return printStat.expand();
    }

    @Override
    public String minimizedTestCase() {
        return null;
    }
}
