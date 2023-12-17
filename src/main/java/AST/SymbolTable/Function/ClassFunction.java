package AST.SymbolTable.Function;

import AST.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.DClass;
import AST.SymbolTable.Types.Variables.Variable;
import AST.SymbolTable.Types.Variables.VariableThis;
import java.util.List;
import java.util.Map;

public class ClassFunction extends Function {

    private final DClass dClass;
    private VariableThis thisVariable;

    public ClassFunction(String name, Type returnType, List<Variable> args, Expression body, SymbolTable symbolTable, DClass dClass) {
        super(name, returnType, args, body, symbolTable);
        this.dClass = dClass;
        addThisToMethod();
    }

    public void addThisToMethod() {
        this.thisVariable = dClass.getThis();
        for (Variable arg : thisVariable.getSymbolTableArgs()) {
            getSymbolTable().addVariable(arg);
            arg.setDeclared();
        }
    }

    @Override
    public void assignThis(Variable classVariable) {
        thisVariable.set(classVariable);
    }
}
