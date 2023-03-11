package AST.SymbolTable;

public interface DCollection extends Type {

    Type setInnerType(Type type);

    Type getInnerType();
}
