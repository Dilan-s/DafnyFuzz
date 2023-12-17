package AST.SymbolTable.Types.Variables;

import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.UserDefinedTypes.DClass;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VariableThis extends Variable {

    private Variable variable;

    public VariableThis(DClass dClass) {
        super("this", dClass);
    }

    @Override
    public String getName() {
        return "this";
    }

    @Override
    public boolean isDeclared() {
        return true;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash("this");
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VariableThis;
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap) {
        return variable.getValue(paramsMap);
    }

    @Override
    public void setValue(SymbolTable symbolTable, Map<Variable, Variable> paramMap, Object value) {
        variable.setValue(symbolTable, paramMap, value);
    }

    public void set(Variable variable) {
        this.variable = variable;
    }
}
