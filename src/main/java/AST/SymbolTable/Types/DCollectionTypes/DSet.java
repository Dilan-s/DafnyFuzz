package AST.SymbolTable.Types.DCollectionTypes;

import AST.Expressions.DSetLiteral;
import AST.Expressions.Expression;
import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DSet implements DCollection {

  public static final int MAX_SIZE_OF_SET = 5;
  public static int MIN_SIZE_OF_SET = 0;
  private final Type type;

  public DSet(Type type) {
    this.type = type;
  }

  public DSet() {
    this(null);
  }

  @Override
  public boolean validMethodType() {
    return false;
  }

  @Override
  public String getName() {
    return "set";
  }

  @Override
  public Type setInnerType(Type type) {
    return new DSet(type);
  }

  @Override
  public Type getInnerType() {
    return type;
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
    if (!(other instanceof DSet)) {
      return false;
    }

    DSet dsetOther = other.asDSet();

    if (type == null || dsetOther.type == null) {
      return true;
    }

    return dsetOther.type.equals(type);
  }

  @Override
  public Expression generateLiteral(SymbolTable symbolTable) {
    RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();

    int noOfElems = MIN_SIZE_OF_SET + GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_SET);
    if (type.equals(new DArray())) {
      while (noOfElems == 1) {
        noOfElems = MIN_SIZE_OF_SET + GeneratorConfig.getRandom().nextInt(MAX_SIZE_OF_SET);
      }
    }
    List<Expression> values = new ArrayList<>();
    for (int i = 0; i < noOfElems; i++) {
      Type t = type.concrete(symbolTable);
      Expression exp = expressionGenerator.generateExpression(t, symbolTable);
      values.add(exp);
    }

    DSetLiteral expression = new DSetLiteral(symbolTable, this, values);
    return expression;
  }

  @Override
  public Expression generateExpressionFromValue(SymbolTable symbolTable, Object value) {
    Set<Object> vs = (Set<Object>) value;
    List<Expression> values = new ArrayList<>();
    for (Object v : vs) {
      Expression exp = type.generateExpressionFromValue(symbolTable, v);
      if (exp == null) {
        return null;
      }
      values.add(exp);
    }
    return new DSetLiteral(symbolTable, this, values);
  }

  @Override
  public String getVariableType() {
    if (type == null) {
      return "set";
    }
    return String.format("set<%s>", type.getVariableType());
  }

  @Override
  public Type concrete(SymbolTable symbolTable) {
    if (type == null) {
      RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
      Type t = typeGenerator.generateEqualTypes(1, symbolTable).get(0);
      return new DSet(t);
    }
    return new DSet(type.concrete(symbolTable));
  }

  @Override
  public boolean operatorExists() {
    return true;
  }

  @Override
  public Boolean disjoint(Object lhsV, Object rhsV) {
    Set<Object> rhsVS = (Set<Object>) rhsV;
    Set<Object> lhsVS = (Set<Object>) lhsV;

    return lhsVS.isEmpty() || rhsVS.isEmpty() || rhsVS.stream().noneMatch(lhsVS::contains);
  }

  @Override
  public Object difference(Object lhsV, Object rhsV) {
    Set<Object> rhsVS = (Set<Object>) rhsV;
    Set<Object> lhsVS = (Set<Object>) lhsV;

    Set<Object> ret = new HashSet<>(lhsVS);
    ret.removeAll(rhsVS);
    return ret;
  }

  @Override
  public Object intersection(Object lhsV, Object rhsV) {
    Set<Object> rhsVS = (Set<Object>) rhsV;
    Set<Object> lhsVS = (Set<Object>) lhsV;

    Set<Object> ret = new HashSet<>(lhsVS);
    ret.retainAll(rhsVS);
    return ret;
  }

  @Override
  public Boolean contains(Object lhsV, Object rhsV) {
    Set<Object> rhsVL = (Set<Object>) rhsV;
    return rhsVL.contains(lhsV);
  }

  @Override
  public Object union(Object lhsV, Object rhsV) {
    Set<Object> rhsVS = (Set<Object>) rhsV;
    Set<Object> lhsVS = (Set<Object>) lhsV;

    Set<Object> ret = new HashSet<>(lhsVS);
    ret.addAll(rhsVS);
    return ret;
  }

  @Override
  public boolean isPrintable() {
    return false;
  }

  @Override
  public String formatPrint(Object object) {
    Set<Object> value = (Set<Object>) object;
    String res =
      "{" + value.stream().map(v -> type.formatPrint(v)).collect(Collectors.joining(", ")) + "}";
    return res;
  }

  @Override
  public String formatEnsures(Object object) {
    Set<Object> value = (Set<Object>) object;
    String res =
      "{" + value.stream().map(v -> type.formatEnsures(v)).collect(Collectors.joining(", ")) + "}";
    return res;
  }


  @Override
  public Boolean lessThan(Object lhsV, Object rhsV) {
    Set<Object> rhsVS = (Set<Object>) rhsV;
    Set<Object> lhsVS = (Set<Object>) lhsV;

    return rhsVS.containsAll(lhsVS) && !lhsVS.containsAll(rhsVS);
  }

  @Override
  public Boolean equal(Object lhsV, Object rhsV) {
    Set<Object> rhsVS = (Set<Object>) rhsV;
    Set<Object> lhsVS = (Set<Object>) lhsV;

    if (lhsVS.size() != rhsVS.size()) {
      return false;
    }

    for (Object lhsK : lhsVS) {
      boolean found = false;
      for (Iterator<Object> iterator = rhsVS.iterator(); !found && iterator.hasNext(); ) {
        Object rhsK = iterator.next();
        if (type.equal(lhsK, rhsK)) {
          found = true;
        }
      }
      if (!found) {
        return false;
      }
    }
    return true;
  }

  @Override
  public BigInteger cardinality(Object value) {
    Set<Object> valS = (Set<Object>) value;
    return BigInteger.valueOf(valS.size());
  }

  @Override
  public String formatEnsures(String variableName, Object object) {
    if (type == null) {
      return null;
    }

    String res;
    Set<Object> value = (Set<Object>) object;
    res =
      "{" + value.stream().map(v -> type.formatEnsures(v)).collect(Collectors.joining(", ")) + "}";
    return variableName + " == " + res;
  }

  @Override
  public Object of(Object value) {
    Set<Object> r = new HashSet<>();

    Set<Object> s = (Set<Object>) value;
    for (Object v : s) {
      r.add(type != null ? type.of(v) : v);
    }

    return r;
  }

  @Override
  public boolean validForFunctionBody() {
    return type.validForFunctionBody();
  }
}
