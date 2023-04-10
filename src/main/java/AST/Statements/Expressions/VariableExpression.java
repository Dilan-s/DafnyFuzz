package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VariableExpression implements Expression {

    private Variable variable;
    private Type type;
    private SymbolTable symbolTable;

    public VariableExpression(SymbolTable symbolTable, Variable variable, Type type) {
        this.symbolTable = symbolTable;
        this.variable = variable;
        this.type = type;
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {
        Variable varInSymbolTable = method.getSymbolTable().getVariable(this.variable);
        if (varInSymbolTable == null) {
            throw new SemanticException(
                String.format("Variable with name %s does not exist", variable.getName()));
        }
    }

    @Override
    public String toString() {
        return variable.getName();
    }

    @Override
    public int hashCode() {
        return variable.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VariableExpression)) {
            return false;
        }
        VariableExpression other = (VariableExpression) obj;
        return other.variable.equals(variable);
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap) {
        if (paramsMap.containsKey(variable)) {
            return paramsMap.get(variable).getValue(paramsMap);
        }
        return variable.getValue(paramsMap);
    }
}
