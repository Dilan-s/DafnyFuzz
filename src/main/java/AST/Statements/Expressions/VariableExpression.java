package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.List;

public class VariableExpression implements Expression {

    private Variable variable;
    private SymbolTable symbolTable;

    public VariableExpression(SymbolTable symbolTable, Variable variable) {
        this.symbolTable = symbolTable;
        this.variable = variable;
    }

    @Override
    public List<Type> getTypes() {
        return List.of(variable.getType());
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
}
