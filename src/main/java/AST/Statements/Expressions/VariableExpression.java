package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VariableExpression extends BaseExpression {

    private Variable variable;
    private Type type;
    private SymbolTable symbolTable;

    public VariableExpression(SymbolTable symbolTable, Variable variable, Type type) {
        super();
        this.symbolTable = symbolTable;
        this.variable = variable;
        this.type = type;
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    @Override
    public String toString() {
        return variable.getName();
    }

    @Override
    protected List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s, boolean unused) {
        if (paramsMap.containsKey(variable)) {
            return paramsMap.get(variable).getValue(paramsMap);
        }
        return variable.getValue(paramsMap);
    }

    public Variable getVariable() {
        return variable;
    }

    @Override
    public List<Statement> expand() {
        return new ArrayList<>();
    }
}
