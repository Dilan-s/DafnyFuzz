package AST.SymbolTable.Types.PrimitiveTypes;

import AST.SymbolTable.Types.Type;

public interface BaseType extends Type {

  default String getVariableType() {
    return getName();
  }

  default boolean isCollection() {
    return false;
  }
}
