package AST.Expressions;

import AST.Statements.Statement;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BitVectorLiteral extends BaseExpression {

  private final BigInteger value;
  private final Type type;
  private final SymbolTable symbolTable;

  public BitVectorLiteral(Type type, SymbolTable symbolTable, BigInteger value) {
    super();
    this.type = type;
    this.symbolTable = symbolTable;
    this.value = value;
  }

  @Override
  public List<Type> getTypes() {
    return List.of(type);
  }

  @Override
  public String toString() {
    return String.format("(%d as %s)", value, type.getName());
  }

  @Override
  public List<String> toOutput() {
    Set<String> res = new HashSet<>();
    res.add(String.valueOf(value));
    res.add(String.valueOf(String.format("(%d as %s)", value, type.getName())));
    return new ArrayList<>(res);
  }

  @Override
  public List<Statement> expand() {
    return new ArrayList<>();
  }

  @Override
  protected List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s,
    boolean unused) {
    List<Object> r = new ArrayList<>();
    r.add(value);
    return r;
  }
}
