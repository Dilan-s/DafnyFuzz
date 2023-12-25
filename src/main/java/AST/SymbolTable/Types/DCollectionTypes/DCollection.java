package AST.SymbolTable.Types.DCollectionTypes;

import AST.SymbolTable.Types.Type;

public interface DCollection extends Type {

  Type setInnerType(Type type);

  Type getInnerType();

  @Override
  default boolean isCollection() {
    return true;
  }

  @Override
  default boolean operatorExists() {
    return !getInnerType().isCollection();
  }

  Boolean disjoint(Object lhsV, Object rhsV);

  Object union(Object lhsV, Object rhsV);

  Object difference(Object lhsV, Object rhsV);

  Object intersection(Object lhsV, Object rhsV);

  Boolean contains(Object lhsV, Object rhsV);
}
