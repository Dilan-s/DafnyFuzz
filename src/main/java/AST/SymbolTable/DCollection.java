package AST.SymbolTable;

import AST.SymbolTable.Types.Type;

public interface DCollection extends Type {

    Type setInnerType(Type type);

    Type getInnerType();
}
