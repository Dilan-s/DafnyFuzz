package AST.Program;

import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.BlockStatement;
import AST.Statements.Expressions.CallExpression;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.IfElseExpression;
import AST.Statements.Expressions.IntLiteral;
import AST.Statements.Expressions.Operator.BinaryOperator;
import AST.Statements.Expressions.Operator.UnaryOperator;
import AST.Statements.Expressions.OperatorExpression;
import AST.Statements.Expressions.VariableExpression;
import AST.Statements.IfElseStatement;
import AST.Statements.ReturnStatement;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.Types.DCollectionTypes.Seq;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Variable;
import java.util.List;

public class SafeMethods {

    public static Method safe_subsequence() {
        Method safe_subsequence = new Method(List.of(new Int(), new Int()), "safe_subsequence");
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

        CallExpression iSafeIndex = new CallExpression(symbolTable, symbolTable.getMethod("safe_index_seq"), List.of(p1VarExp, p2VarExp));

        AssignmentStatement asI = new AssignmentStatement(symbolTable, List.of(iVar), iSafeIndex);
        statement.addStatement(asI.expand());

        Int jT = new Int();
        String j = VariableNameGenerator.generateVariableValueName(jT, symbolTable);
        Variable jVar = new Variable(j, jT);
        VariableExpression jVarExp = new VariableExpression(symbolTable, jVar, jT);

        CallExpression jSafeIndex = new CallExpression(symbolTable, symbolTable.getMethod("safe_index_seq"), List.of(p1VarExp, p3VarExp));

        AssignmentStatement asJ = new AssignmentStatement(symbolTable, List.of(jVar), jSafeIndex);
        statement.addStatement(asJ.expand());


        OperatorExpression test = new OperatorExpression(symbolTable, new Bool(), BinaryOperator.Less_Than, List.of(iVarExp, jVarExp));

        ReturnStatement ifRet = new ReturnStatement(symbolTable, List.of(iVarExp, jVarExp));
        ifRet.setPrintAll(false);

        ReturnStatement elseRet = new ReturnStatement(symbolTable, List.of(jVarExp, iVarExp));
        elseRet.setPrintAll(false);

        IfElseStatement ifElseStatement = new IfElseStatement(symbolTable);
        ifElseStatement.setTest(test);
        ifElseStatement.setIfStat(ifRet);
        ifElseStatement.setElseStat(elseRet);

        statement.addStatement(ifElseStatement.expand());
        safe_subsequence.assignReturn();
        System.out.println(safe_subsequence);
        return safe_subsequence.getSimpleMethod();
    }

    public static Method safe_index_seq() {
        Method safe_index_seq = new Method(new Int(), "safe_index_seq");
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

        OperatorExpression size = new OperatorExpression(symbolTable, new Int(), UnaryOperator.Cardinality, List.of(p1VarExp));

        OperatorExpression ltSize = new OperatorExpression(symbolTable, new Bool(), BinaryOperator.Less_Than, List.of(p2VarExp, size));

        IntLiteral zero = new IntLiteral(new Int(), symbolTable, 0);
        OperatorExpression gtZero = new OperatorExpression(symbolTable, new Bool(), BinaryOperator.Less_Than_Or_Equal, List.of(zero, p2VarExp));

        OperatorExpression test = new OperatorExpression(symbolTable, new Bool(), BinaryOperator.And, List.of(ltSize, gtZero));

        IfElseExpression ifElseExpression = new IfElseExpression(symbolTable, new Int(), test, p2VarExp, new IntLiteral(new Int(), symbolTable, 0));

        ReturnStatement returnStatement = new ReturnStatement(symbolTable, List.of(ifElseExpression));
        returnStatement.setPrintAll(false);

        statement.addStatement(returnStatement.expand());

        safe_index_seq.assignReturn();
        System.out.println(safe_index_seq);
        return safe_index_seq.getSimpleMethod();
    }

    static Method safe_division() {
        Method safe_div = new Method(new Int(), "safe_division");
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
        OperatorExpression test = new OperatorExpression(symbolTable, new Bool(), BinaryOperator.Not_Equals, List.of(lhsTest, rhsTest));


        VariableExpression lhsIf = new VariableExpression(symbolTable, p1Var, p1T);
        VariableExpression rhsIf = new VariableExpression(symbolTable, p2Var, p2T);
        OperatorExpression ifDiv = new OperatorExpression(symbolTable, new Int(), BinaryOperator.Divide, List.of(lhsIf, rhsIf), false);

        Expression elseDiv = new VariableExpression(symbolTable, p1Var, p1T);

        IfElseExpression expression = new IfElseExpression(symbolTable, new Int(), test, ifDiv, elseDiv);

        ReturnStatement returnStatement = new ReturnStatement(symbolTable, List.of(expression));
        returnStatement.setPrintAll(false);

        statement.addStatement(returnStatement.expand());

        safe_div.assignReturn();
        System.out.println(safe_div);
        return safe_div.getSimpleMethod();
    }

    static Method safe_modulus() {
        Method safe_mod = new Method(new Int(), "safe_modulus");
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
        OperatorExpression test = new OperatorExpression(symbolTable, new Bool(), BinaryOperator.Not_Equals, List.of(lhsTest, rhsTest));

        VariableExpression lhsIf = new VariableExpression(symbolTable, p1Var, p1T);
        VariableExpression rhsIf = new VariableExpression(symbolTable, p2Var, p2T);
        OperatorExpression ifDiv = new OperatorExpression(symbolTable, new Int(), BinaryOperator.Modulus, List.of(lhsIf, rhsIf), false);

        Expression elseDiv = new VariableExpression(symbolTable, p1Var, p1T);

        IfElseExpression expression = new IfElseExpression(symbolTable, new Int(), test, ifDiv, elseDiv);

        ReturnStatement returnStatement = new ReturnStatement(symbolTable, List.of(expression));
        returnStatement.setPrintAll(false);


        statement.addStatement(returnStatement.expand());

        safe_mod.assignReturn();
        System.out.println(safe_mod);
        return safe_mod.getSimpleMethod();
    }
}
