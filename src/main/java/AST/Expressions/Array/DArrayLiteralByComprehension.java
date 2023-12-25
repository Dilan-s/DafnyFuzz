package AST.Expressions.Array;

import AST.Expressions.BaseExpression;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.Statement;
import AST.SymbolTable.Function.Function;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import AST.SymbolTable.Types.Variables.VariableArrayIndex;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DArrayLiteralByComprehension extends BaseExpression {

  private final SymbolTable symbolTable;
  private final Type type;
  private final int length;
  private final Function func;
  private final Variable variable;
  private final List<List<Statement>> expanded;
  private final Statement statement;

  public DArrayLiteralByComprehension(SymbolTable symbolTable, Type type, int length,
    Function func) {
    super();
    this.symbolTable = symbolTable;
    this.type = type;
    this.length = length;
    this.func = func;
    func.incrementUse();

    this.variable = new Variable(VariableNameGenerator.generateVariableValueName(type, symbolTable),
      type);
    this.variable.setConstant();
    this.statement = new AssignmentStatement(symbolTable, List.of(variable), new ArrayInitValues());

    this.expanded = new ArrayList<>();
    expanded.add(statement.expand());
    generateAssignments();
  }

  private void generateAssignments() {
    DCollection t = this.type.asDCollection();
    Type valType = t.getInnerType();

    for (int i = 0; i < length; i++) {
      VariableArrayIndex v = new VariableArrayIndex(variable, valType, i);
      v.setDeclared();
      symbolTable.addVariable(v);
    }
  }

  @Override
  public String toString() {
    return variable.getName();
  }

  @Override
  protected List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s,
    boolean unused) {
    return variable.getValue(paramsMap);
  }

  @Override
  public boolean validForFunctionBody() {
    return false;
  }


  @Override
  public List<Type> getTypes() {
    return List.of(type);
  }

  @Override
  public List<Statement> expand() {
    expanded.set(0, statement.expand());
    return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
  }

  private class ArrayInitValues extends BaseExpression {

    public ArrayInitValues() {
      super();
    }

    @Override
    public List<Type> getTypes() {
      return List.of(type);
    }

    @Override
    public List<Statement> expand() {
      return new ArrayList<>();
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s,
      boolean unused) {
      List<Object> r = new ArrayList<>();

      List<Object> l = new ArrayList<>();
      for (int i = 0; i < length; i++) {
        Variable arg = new Variable("TEMP", new Int());
        arg.setValue(symbolTable, paramsMap, BigInteger.valueOf(i));
        List<Object> value = func.execute(List.of(arg), s);
        for (Object v : value) {
          if (v == null) {
            r.add(null);
            return r;
          }
          l.add(v);
        }
      }
      r.add(new ArrayValue(variable, l));
      return r;
    }

    @Override
    public String toString() {
      DCollection t = type.asDCollection();
      return String.format("new %s[%d](i => %s(i))", t.getInnerType().getVariableType(),
        length, func.getName());
    }

    @Override
    public boolean validForFunctionBody() {
      return false;
    }
  }
}
