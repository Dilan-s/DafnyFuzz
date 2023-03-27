package AST.SymbolTable.Types.UserDefinedTypes;

import AST.SymbolTable.Types.Type;

public interface UserDefined extends Type {

    @Override
    default boolean operatorExists() {
        return false;
    }

    @Override
    default boolean isCollection() {
        return false;
    }
}
