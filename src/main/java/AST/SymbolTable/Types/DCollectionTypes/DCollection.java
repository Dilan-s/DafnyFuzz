package AST.SymbolTable.Types.DCollectionTypes;

import AST.SymbolTable.Types.Type;

public interface DCollection extends Type {

    Type setInnerType(Type type);

    Type getInnerType();

    @Override
    default boolean isCollection() {
        return true;
    }
}
