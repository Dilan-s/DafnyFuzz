package AST.SymbolTable.Types.PrimitiveTypes;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.BoolLiteral;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;

public class Bool implements BaseType {

    @Override
    public String getName() {
        return "bool";
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Type)) {
            return false;
        }
        Type other = (Type) obj;
        return other instanceof Bool;
    }

    @Override
    public Expression generateLiteral(SymbolTable symbolTable) {
        return new BoolLiteral(symbolTable, GeneratorConfig.getRandom().nextBoolean());
    }

    @Override
    public boolean operatorExists() {
        return true;
    }

    @Override
    public Type concrete(SymbolTable symbolTable) {
        return new Bool();
    }
}
