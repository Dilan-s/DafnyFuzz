package AST.Expressions.Operator;

import AST.Errors.SemanticException;
import AST.Expressions.Expression;
import AST.Generator.RandomTypeGenerator;
import AST.SymbolTable.Method.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Types.DCollectionTypes.DSet;
import AST.SymbolTable.Types.DCollectionTypes.Multiset;
import AST.SymbolTable.Types.DCollectionTypes.Seq;
import AST.SymbolTable.Types.DMap.DMap;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.DataType.DataType;
import AST.SymbolTable.Types.UserDefinedTypes.Tuple;
import AST.SymbolTable.Types.UserDefinedTypes.TypeAlias;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Operator {

  String formExpression(List<Expression> args);

  String formMinimizedExpression(List<Expression> args);

  List<Type> getType();

  void semanticCheck(Method method, List<Expression> expressions) throws SemanticException;

  List<List<Type>> getTypeArgs();

  int getNumberOfArgs();

  default boolean returnType(Type type) {
    return getType().stream().anyMatch(t -> t.equals(type));
  }

  default List<Type> concreteType(List<Type> types, SymbolTable symbolTable, Type expected) {
    RandomTypeGenerator typeGenerator = new RandomTypeGenerator();

    Type collectionInnerType = typeGenerator.generateTypes(1, symbolTable).get(0);
    Type tuple = new Tuple().concrete(symbolTable);
    Type datatype = new DataType();
    datatype = datatype.concrete(symbolTable);
    Type map = new DMap().concrete(symbolTable);
    Type typeAlias = null;

    List<Type> ret = new ArrayList<>();
    for (Type type : types) {
      if (type.isTypeAlias()) {
        if (typeAlias == null) {
           typeAlias = typeGenerator.generateTypeAlias().concrete(symbolTable);
        }
        ret.add(typeAlias);
      } else if (type.isCollection()) {
        DCollection collection = type.asDCollection();
        ret.add(collection.setInnerType(collectionInnerType.concrete(symbolTable))
          .concrete(symbolTable));
      } else if (type.equals(new Tuple())) {
        ret.add(tuple.concrete(symbolTable));
      } else if (type.equals(new DataType())) {
        ret.add(datatype.concrete(symbolTable));
      } else if (type.equals(new DMap())) {
        ret.add(map.concrete(symbolTable));
      } else {
        ret.add(type.concrete(symbolTable));
      }
    }
    return ret;
  }

  Object apply(List<Expression> args, Map<Variable, Variable> paramsMap);

  List<String> formOutput(List<Expression> args);

  default boolean requiresSafe(List<Object> vals) {
    return false;
  }

  default List<Operator> mutateForInvalidValidation(List<Type> argTypes) {
    List<Operator> ops = new ArrayList<>();
    ops.add(BinaryOperator.Not_Equals);
    if (argTypes.stream().allMatch(Type::isOrdered)) {
      ops.add(BinaryOperator.Less_Than);
      ops.add(BinaryOperator.Greater_Than);
    }
    return ops;
  }

  default int restrictions() {
    int prev = Seq.MIN_SIZE_OF_SEQ;
    Seq.MIN_SIZE_OF_SEQ = 1;
    DSet.MIN_SIZE_OF_SET = 1;
    Multiset.MIN_SIZE_OF_MULTISET = 1;
    return prev;
  }

  default void restore(int v) {
    Seq.MIN_SIZE_OF_SEQ = v;
    DSet.MIN_SIZE_OF_SET = v;
    Multiset.MIN_SIZE_OF_MULTISET = v;
  }
}
