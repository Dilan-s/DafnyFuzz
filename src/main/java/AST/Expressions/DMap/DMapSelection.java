package AST.Expressions.DMap;

import AST.Expressions.BaseExpression;
import AST.Expressions.Expression;
import AST.Expressions.IfElseExpression;
import AST.Expressions.Operator.BinaryOperator;
import AST.Expressions.Operator.OperatorExpression;
import AST.Expressions.Variable.VariableExpression;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.Statement;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DMapSelection extends BaseExpression {

  private final SymbolTable symbolTable;
  private final Type type;

  private AssignmentStatement mapAssign;
  private IfElseExpression ifElseExp;
  private AssignmentStatement indexAssign;

  private final List<List<Statement>> expanded;

  public DMapSelection(SymbolTable symbolTable, Type type, Expression map, Expression index,
    Expression def) {
    super();
    this.symbolTable = symbolTable;
    this.type = type;
    generateVariableCalls(map, index, def);

    this.expanded = new ArrayList<>();
    expanded.add(mapAssign.expand());
    expanded.add(indexAssign.expand());
    expanded.add(ifElseExp.expand());
  }

  private void generateVariableCalls(Expression map, Expression index, Expression def) {
    Type tM = map.getTypes().get(0);
    Variable mapVar = new Variable(VariableNameGenerator.generateVariableValueName(tM, symbolTable),
      tM);
    mapAssign = new AssignmentStatement(symbolTable, List.of(mapVar), map);

    Type tI = index.getTypes().get(0);
    Variable indexVar = new Variable(
      VariableNameGenerator.generateVariableValueName(tI, symbolTable), tI);
    indexAssign = new AssignmentStatement(symbolTable, List.of(indexVar), index);

    VariableExpression mapVarExp = new VariableExpression(symbolTable, mapVar, tM);
    VariableExpression indexVarExp = new VariableExpression(symbolTable, indexVar, tI);

    DMapIndex dMapIndex = new DMapIndex(symbolTable, type, mapVarExp, indexVarExp);

    List<Expression> args = new ArrayList<>();
    args.add(indexVarExp);
    args.add(mapVarExp);
    var test = new OperatorExpression(symbolTable, new Bool(), BinaryOperator.MembershipMap, args);

    ifElseExp = new IfElseExpression(symbolTable, type, test, dMapIndex, def);
  }

  @Override
  public List<Type> getTypes() {
    return List.of(type);
  }

  @Override
  protected List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s,
    boolean unused) {
    return ifElseExp.getValue(paramsMap, s);
  }

  @Override
  public List<Statement> expand() {
    expanded.set(0, mapAssign.expand());
    expanded.set(1, indexAssign.expand());
    expanded.set(2, ifElseExp.expand());
    return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
  }

  @Override
  public boolean validForFunctionBody() {
    return super.validForFunctionBody() && ifElseExp.validForFunctionBody();
  }

  @Override
  public List<String> toOutput() {
    return ifElseExp.toOutput();
  }

  @Override
  public String toString() {
    return ifElseExp.toString();
  }

  @Override
  public String minimizedTestCase() {
    return ifElseExp.minimizedTestCase();
  }
}
