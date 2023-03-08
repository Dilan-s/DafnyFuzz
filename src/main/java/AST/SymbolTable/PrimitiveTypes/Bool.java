package AST.SymbolTable.PrimitiveTypes;

import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.BoolLiteral;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import java.util.Random;

public class Bool implements Type {

    @Override
    public String getName() {
        return "bool";
    }

    @Override
    public boolean isSameType(Type other) {
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
}
