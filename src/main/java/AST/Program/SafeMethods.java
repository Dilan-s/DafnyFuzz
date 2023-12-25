package AST.Program;

import AST.Expressions.Expression;
import AST.Expressions.IfElseExpression;
import AST.Expressions.IntLiteral;
import AST.Expressions.Method.CallBaseMethodExpression;
import AST.Expressions.Operator.BinaryOperator;
import AST.Expressions.Operator.OperatorExpression;
import AST.Expressions.Operator.UnaryOperator;
import AST.Expressions.Variable.VariableExpression;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.BlockStatement;
import AST.Statements.IfElseStatement;
import AST.Statements.ReturnStatement;
import AST.SymbolTable.Method.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.DCollectionTypes.Seq;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.List;

public class SafeMethods {

  public static Method safe_min_max() {
    Method safe_min_max = new Method(List.of(new Int(), new Int()), "safe_min_max");
    safe_min_max.addEnsures(
      "((p_safe_min_max_1 < p_safe_min_max_2) ==> ((ret_1 <= ret_2) && (ret_1 == p_safe_min_max_1) && (ret_2 == p_safe_min_max_2))) && ((p_safe_min_max_1 >= p_safe_min_max_2) ==> ((ret_1 <= ret_2) && (ret_1 == p_safe_min_max_2) && (ret_2 == p_safe_min_max_1)))");
    SymbolTable symbolTable = safe_min_max.getSymbolTable();

    BlockStatement statement = new BlockStatement(symbolTable);
    safe_min_max.setBody(statement);

    String p1 = VariableNameGenerator.generateArgumentName(safe_min_max);
    Int p1T = new Int();
    Variable p1Var = new Variable(p1, p1T);
    VariableExpression p1VarExp = new VariableExpression(symbolTable, p1Var, p1T);
    safe_min_max.addArgument(p1Var);

    String p2 = VariableNameGenerator.generateArgumentName(safe_min_max);
    Int p2T = new Int();
    Variable p2Var = new Variable(p2, p2T);
    VariableExpression p2VarExp = new VariableExpression(symbolTable, p2Var, p2T);
    safe_min_max.addArgument(p2Var);

    OperatorExpression test = new OperatorExpression(symbolTable, new Bool(),
      BinaryOperator.Less_Than, List.of(p1VarExp, p2VarExp));

    IfElseExpression lhs = new IfElseExpression(symbolTable, new Int(), test, p1VarExp, p2VarExp);
    IfElseExpression rhs = new IfElseExpression(symbolTable, new Int(), test, p2VarExp, p1VarExp);
    ReturnStatement returnStatement = new ReturnStatement(symbolTable, List.of(lhs, rhs), false);

    statement.addStatement(returnStatement);

    return safe_min_max;
  }

  public static Method safe_subsequence() {
    Method safe_subsequence = new Method(List.of(new Int(), new Int()), "safe_subsequence");
    safe_subsequence.addEnsures(
      "((|p_safe_subsequence_1| > 0) ==> ((0 <= ret_1 < |p_safe_subsequence_1|) && (0 <= ret_2 < |p_safe_subsequence_1|) && ret_1 <= ret_2)) && ((((0 <= p_safe_subsequence_2 < |p_safe_subsequence_1|) ==> (ret_1 == p_safe_subsequence_2)) && ((0 > p_safe_subsequence_2 || p_safe_subsequence_2 >= |p_safe_subsequence_1|) ==> (ret_1 == 0)) && ((0 <= p_safe_subsequence_3 < |p_safe_subsequence_1|) ==> (ret_2 == p_safe_subsequence_3)) && ((0 > p_safe_subsequence_3 || p_safe_subsequence_3 >= |p_safe_subsequence_1|) ==> (ret_2 == 0))) || ((((0 <= p_safe_subsequence_2 < |p_safe_subsequence_1|) ==> (ret_2 == p_safe_subsequence_2)) && ((0 > p_safe_subsequence_2 || p_safe_subsequence_2 >= |p_safe_subsequence_1|) ==> (ret_2 == 0)) && ((0 <= p_safe_subsequence_3 < |p_safe_subsequence_1|) ==> (ret_1 == p_safe_subsequence_3)) && ((0 > p_safe_subsequence_3 || p_safe_subsequence_3 >= |p_safe_subsequence_1|) ==> (ret_1 == 0)))))");
    SymbolTable symbolTable = safe_subsequence.getSymbolTable();

    String p1 = VariableNameGenerator.generateArgumentName(safe_subsequence);
    Seq p1T = new Seq();
    Variable p1Var = new Variable(p1, p1T);
    VariableExpression p1VarExp = new VariableExpression(symbolTable, p1Var, p1T);
    safe_subsequence.addArgument(p1Var);

    String p2 = VariableNameGenerator.generateArgumentName(safe_subsequence);
    Int p2T = new Int();
    Variable p2Var = new Variable(p2, p2T);
    VariableExpression p2VarExp = new VariableExpression(symbolTable, p2Var, p2T);
    safe_subsequence.addArgument(p2Var);

    String p3 = VariableNameGenerator.generateArgumentName(safe_subsequence);
    Int p3T = new Int();
    Variable p3Var = new Variable(p3, p3T);
    VariableExpression p3VarExp = new VariableExpression(symbolTable, p3Var, p3T);
    safe_subsequence.addArgument(p3Var);

    BlockStatement statement = new BlockStatement(symbolTable);
    safe_subsequence.setBody(statement);

    Int iT = new Int();
    String i = VariableNameGenerator.generateVariableValueName(iT, symbolTable);
    Variable iVar = new Variable(i, iT);
    VariableExpression iVarExp = new VariableExpression(symbolTable, iVar, iT);

    CallBaseMethodExpression iSafeIndex = new CallBaseMethodExpression(symbolTable,
      symbolTable.getMethod("safe_index_seq"), List.of(p1VarExp, p2VarExp));

    AssignmentStatement asI = new AssignmentStatement(symbolTable, List.of(iVar), iSafeIndex);
    statement.addStatement(asI);

    Int jT = new Int();
    String j = VariableNameGenerator.generateVariableValueName(jT, symbolTable);
    Variable jVar = new Variable(j, jT);
    VariableExpression jVarExp = new VariableExpression(symbolTable, jVar, jT);

    CallBaseMethodExpression jSafeIndex = new CallBaseMethodExpression(symbolTable,
      symbolTable.getMethod("safe_index_seq"), List.of(p1VarExp, p3VarExp));

    AssignmentStatement asJ = new AssignmentStatement(symbolTable, List.of(jVar), jSafeIndex);
    statement.addStatement(asJ);

    OperatorExpression test = new OperatorExpression(symbolTable, new Bool(),
      BinaryOperator.Less_Than_Or_Equal, List.of(iVarExp, jVarExp));

    ReturnStatement ifRet = new ReturnStatement(symbolTable, List.of(iVarExp, jVarExp), false);

    ReturnStatement elseRet = new ReturnStatement(symbolTable, List.of(jVarExp, iVarExp), false);

    IfElseStatement ifElseStatement = new IfElseStatement(symbolTable, test, ifRet, elseRet);

    statement.addStatement(ifElseStatement);
    return safe_subsequence;
  }

  public static Method safe_index_seq() {
    Method safe_index_seq = new Method(new Int(), "safe_index_seq");
    safe_index_seq.addEnsures(
      "((0 <= p_safe_index_seq_2 < |p_safe_index_seq_1|) ==> (ret_1 == p_safe_index_seq_2)) && ((0 > p_safe_index_seq_2 || p_safe_index_seq_2 >= |p_safe_index_seq_1|) ==> (ret_1 == 0))");
    SymbolTable symbolTable = safe_index_seq.getSymbolTable();
    BlockStatement statement = new BlockStatement(symbolTable);
    safe_index_seq.setBody(statement);

    String p1 = VariableNameGenerator.generateArgumentName(safe_index_seq);
    Seq p1T = new Seq();
    Variable p1Var = new Variable(p1, p1T);
    safe_index_seq.addArgument(p1Var);

    String p2 = VariableNameGenerator.generateArgumentName(safe_index_seq);
    Int p2T = new Int();
    Variable p2Var = new Variable(p2, p2T);
    safe_index_seq.addArgument(p2Var);

    VariableExpression p1VarExp = new VariableExpression(symbolTable, p1Var, p1T);

    VariableExpression p2VarExp = new VariableExpression(symbolTable, p2Var, p2T);

    OperatorExpression size = new OperatorExpression(symbolTable, new Int(),
      UnaryOperator.Cardinality, List.of(p1VarExp));

    OperatorExpression ltSize = new OperatorExpression(symbolTable, new Bool(),
      BinaryOperator.Less_Than, List.of(p2VarExp, size));

    IntLiteral zero = new IntLiteral(new Int(), symbolTable, 0);
    OperatorExpression gtZero = new OperatorExpression(symbolTable, new Bool(),
      BinaryOperator.Less_Than_Or_Equal, List.of(zero, p2VarExp));

    OperatorExpression test = new OperatorExpression(symbolTable, new Bool(), BinaryOperator.And,
      List.of(ltSize, gtZero));

    IfElseExpression ifElseExpression = new IfElseExpression(symbolTable, new Int(), test, p2VarExp,
      new IntLiteral(new Int(), symbolTable, 0));

    ReturnStatement returnStatement = new ReturnStatement(symbolTable, List.of(ifElseExpression),
      false);

    statement.addStatement(returnStatement);

    return safe_index_seq;
  }

  static Method safe_division() {
    Method safe_div = new Method(new Int(), "safe_division");
    safe_div.addEnsures(
      "(p_safe_division_2 == 0 ==> ret_1 == p_safe_division_1) && (p_safe_division_2 != 0 ==> ret_1 == p_safe_division_1 / p_safe_division_2)");
    SymbolTable symbolTable = safe_div.getSymbolTable();
    BlockStatement statement = new BlockStatement(symbolTable);
    safe_div.setBody(statement);

    Int p1T = new Int();
    String p1 = VariableNameGenerator.generateArgumentName(safe_div);
    Variable p1Var = new Variable(p1, p1T);
    safe_div.addArgument(p1Var);

    Int p2T = new Int();
    String p2 = VariableNameGenerator.generateArgumentName(safe_div);
    Variable p2Var = new Variable(p2, p2T);
    safe_div.addArgument(p2Var);

    VariableExpression lhsTest = new VariableExpression(symbolTable, p2Var, p2T);
    IntLiteral rhsTest = new IntLiteral(new Int(), symbolTable, 0);
    OperatorExpression test = new OperatorExpression(symbolTable, new Bool(),
      BinaryOperator.Not_Equals, List.of(lhsTest, rhsTest));

    VariableExpression lhsIf = new VariableExpression(symbolTable, p1Var, p1T);
    VariableExpression rhsIf = new VariableExpression(symbolTable, p2Var, p2T);
    OperatorExpression ifDiv = new OperatorExpression(symbolTable, new Int(), BinaryOperator.Divide,
      List.of(lhsIf, rhsIf), false);

    Expression elseDiv = new VariableExpression(symbolTable, p1Var, p1T);

    IfElseExpression expression = new IfElseExpression(symbolTable, new Int(), test, ifDiv,
      elseDiv);

    ReturnStatement returnStatement = new ReturnStatement(symbolTable, List.of(expression), false);

    statement.addStatement(returnStatement);

    return safe_div;
  }

  static Method safe_modulus() {
    Method safe_mod = new Method(new Int(), "safe_modulus");
    safe_mod.addEnsures(
      "(p_safe_modulus_2 == 0 ==> ret_1 == p_safe_modulus_1) && (p_safe_modulus_2 != 0 ==> ret_1 == p_safe_modulus_1 % p_safe_modulus_2)");
    SymbolTable symbolTable = safe_mod.getSymbolTable();
    BlockStatement statement = new BlockStatement(symbolTable);
    safe_mod.setBody(statement);

    Int p1T = new Int();
    String p1 = VariableNameGenerator.generateArgumentName(safe_mod);
    Variable p1Var = new Variable(p1, p1T);
    safe_mod.addArgument(p1Var);

    Int p2T = new Int();
    String p2 = VariableNameGenerator.generateArgumentName(safe_mod);
    Variable p2Var = new Variable(p2, p2T);
    safe_mod.addArgument(p2Var);

    VariableExpression lhsTest = new VariableExpression(symbolTable, p2Var, p2T);
    IntLiteral rhsTest = new IntLiteral(new Int(), symbolTable, 0);
    OperatorExpression test = new OperatorExpression(symbolTable, new Bool(),
      BinaryOperator.Not_Equals, List.of(lhsTest, rhsTest));

    VariableExpression lhsIf = new VariableExpression(symbolTable, p1Var, p1T);
    VariableExpression rhsIf = new VariableExpression(symbolTable, p2Var, p2T);
    OperatorExpression ifDiv = new OperatorExpression(symbolTable, new Int(),
      BinaryOperator.Modulus, List.of(lhsIf, rhsIf), false);

    Expression elseDiv = new VariableExpression(symbolTable, p1Var, p1T);

    IfElseExpression expression = new IfElseExpression(symbolTable, new Int(), test, ifDiv,
      elseDiv);

    ReturnStatement returnStatement = new ReturnStatement(symbolTable, List.of(expression), false);

    statement.addStatement(returnStatement);

    return safe_mod;
  }
}
