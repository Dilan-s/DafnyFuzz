package AST.Program;

import AST.Errors.InvalidArgumentException;
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
import AST.SymbolTable.PrimitiveTypes.Int;
import AST.SymbolTable.PrimitiveTypes.Seq;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Variable;
import java.util.List;

public class SafeMethods {

    public static Method safe_subsequence() {
        Method safe_subsequence = new Method(List.of(new Int(), new Int()), "safe_subsequence");
        SymbolTable symbolTable = safe_subsequence.getSymbolTable();

        String p1 = VariableNameGenerator.generateArgumentName(safe_subsequence);
        Variable p1Var = new Variable(p1, new Seq());
        VariableExpression p1VarExp = new VariableExpression(p1Var);
        p1VarExp.setSymbolTable(symbolTable);
        safe_subsequence.addArgument(p1Var);

        String p2 = VariableNameGenerator.generateArgumentName(safe_subsequence);
        Variable p2Var = new Variable(p2, new Int());
        VariableExpression p2VarExp = new VariableExpression(p2Var);
        p2VarExp.setSymbolTable(symbolTable);
        safe_subsequence.addArgument(p2Var);

        String p3 = VariableNameGenerator.generateArgumentName(safe_subsequence);
        Variable p3Var = new Variable(p3, new Int());
        VariableExpression p3VarExp = new VariableExpression(p3Var);
        p3VarExp.setSymbolTable(symbolTable);
        safe_subsequence.addArgument(p3Var);


        BlockStatement statement = new BlockStatement(symbolTable);
        safe_subsequence.setBody(statement);

        String i = VariableNameGenerator.generateVariableValueName(new Int());
        Variable iVar = new Variable(i, new Int());
        VariableExpression iVarExp = new VariableExpression(iVar);
        iVarExp.setSymbolTable(symbolTable);

        CallExpression iSafeIndex = new CallExpression(symbolTable.getMethod("safe_index_seq"));
        iSafeIndex.setSymbolTable(symbolTable);
        try {
            iSafeIndex.addArg(p1VarExp);
            iSafeIndex.addArg(p2VarExp);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        AssignmentStatement asI = new AssignmentStatement(symbolTable);
        statement.addStatement(asI);
        asI.addAssignment(List.of(iVar), iSafeIndex);


        String j = VariableNameGenerator.generateVariableValueName(new Int());
        Variable jVar = new Variable(j, new Int());
        VariableExpression jVarExp = new VariableExpression(jVar);
        jVarExp.setSymbolTable(symbolTable);

        CallExpression jSafeIndex = new CallExpression(symbolTable.getMethod("safe_index_seq"));
        jSafeIndex.setSymbolTable(symbolTable);
        try {
            jSafeIndex.addArg(p1VarExp);
            jSafeIndex.addArg(p3VarExp);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        AssignmentStatement asJ = new AssignmentStatement(symbolTable);
        statement.addStatement(asJ);
        asJ.addAssignment(List.of(jVar), jSafeIndex);

        IfElseStatement ifElseStatement = new IfElseStatement(symbolTable);
        statement.addStatement(ifElseStatement);

        OperatorExpression test = new OperatorExpression(BinaryOperator.Less_Than);
        test.setSymbolTable(symbolTable);
        test.addArgument(iVarExp);
        test.addArgument(jVarExp);
        ifElseStatement.setTest(test);

        ReturnStatement ifRet = new ReturnStatement(symbolTable);
        ifRet.addValue(iVarExp);
        ifRet.addValue(jVarExp);
        ifElseStatement.setIfStat(ifRet);

        ReturnStatement elseRet = new ReturnStatement(symbolTable);
        elseRet.addValue(jVarExp);
        elseRet.addValue(iVarExp);
        ifElseStatement.setElseStat(elseRet);

        System.out.println(safe_subsequence);
        return safe_subsequence.getSimpleMethod();
    }

    public static Method safe_index_seq() {
        Method safe_index_seq = new Method(new Int(), "safe_index_seq");

        String p1 = VariableNameGenerator.generateArgumentName(safe_index_seq);
        Variable p1Var = new Variable(p1, new Seq());
        safe_index_seq.addArgument(p1Var);

        String p2 = VariableNameGenerator.generateArgumentName(safe_index_seq);
        Variable p2Var = new Variable(p2, new Int());
        safe_index_seq.addArgument(p2Var);

        SymbolTable symbolTable = safe_index_seq.getSymbolTable();
        ReturnStatement statement = new ReturnStatement(symbolTable);
        safe_index_seq.setBody(statement);

        VariableExpression p1VarExp = new VariableExpression(p1Var);
        p1VarExp.setSymbolTable(symbolTable);

        VariableExpression p2VarExp = new VariableExpression(p2Var);
        p2VarExp.setSymbolTable(symbolTable);

        OperatorExpression size = new OperatorExpression(UnaryOperator.Cardinality);
        size.setSymbolTable(symbolTable);
        size.addArgument(p1VarExp);

        OperatorExpression ltSize = new OperatorExpression(BinaryOperator.Less_Than);
        ltSize.setSymbolTable(symbolTable);
        ltSize.addArgument(p2VarExp);
        ltSize.addArgument(size);

        OperatorExpression gtZero = new OperatorExpression(BinaryOperator.Less_Than_Or_Equal);
        gtZero.setSymbolTable(symbolTable);
        gtZero.addArgument(new IntLiteral(0));
        gtZero.addArgument(p2VarExp);

        OperatorExpression test = new OperatorExpression(BinaryOperator.And);
        test.setSymbolTable(symbolTable);
        test.addArgument(ltSize);
        test.addArgument(gtZero);

        IfElseExpression ifElseExpression = new IfElseExpression(test, p2VarExp, new IntLiteral(0));
        ifElseExpression.setSymbolTable(symbolTable);
        statement.addValue(ifElseExpression);

        System.out.println(safe_index_seq);
        return safe_index_seq.getSimpleMethod();
    }

    static Method safe_division() {
        Method safe_div = new Method(new Int(), "safe_division");

        String p1 = VariableNameGenerator.generateArgumentName(safe_div);
        Variable p1Var = new Variable(p1, new Int());
        safe_div.addArgument(p1Var);
        String p2 = VariableNameGenerator.generateArgumentName(safe_div);
        Variable p2Var = new Variable(p2, new Int());
        safe_div.addArgument(p2Var);

        SymbolTable safe_div_symbolTable = safe_div.getSymbolTable();
        ReturnStatement statement = new ReturnStatement(safe_div_symbolTable);
        safe_div.setBody(statement);

        OperatorExpression test = new OperatorExpression(BinaryOperator.Not_Equals);
        test.setSymbolTable(safe_div_symbolTable);

        VariableExpression lhsTest = new VariableExpression(p2Var);
        lhsTest.setSymbolTable(safe_div_symbolTable);
        test.addArgument(lhsTest);

        IntLiteral rhsTest = new IntLiteral(0);
        rhsTest.setSymbolTable(safe_div_symbolTable);
        test.addArgument(rhsTest);

        OperatorExpression ifDiv = new OperatorExpression(BinaryOperator.Divide, false);
        ifDiv.setSymbolTable(safe_div_symbolTable);

        VariableExpression lhsIf = new VariableExpression(p1Var);
        lhsIf.setSymbolTable(safe_div_symbolTable);
        ifDiv.addArgument(lhsIf);

        VariableExpression rhsIf = new VariableExpression(p2Var);
        rhsIf.setSymbolTable(safe_div_symbolTable);
        ifDiv.addArgument(rhsIf);

        Expression elseDiv = new VariableExpression(p1Var);
        elseDiv.setSymbolTable(safe_div_symbolTable);

        IfElseExpression expression = new IfElseExpression(test, ifDiv, elseDiv);
        expression.setSymbolTable(safe_div_symbolTable);
        statement.addValue(expression);

        System.out.println(safe_div);
        return safe_div.getSimpleMethod();
    }

    static Method safe_modulus() {
        Method safe_mod = new Method(new Int(), "safe_modulus");

        String p1 = VariableNameGenerator.generateArgumentName(safe_mod);
        Variable p1Var = new Variable(p1, new Int());
        safe_mod.addArgument(p1Var);
        String p2 = VariableNameGenerator.generateArgumentName(safe_mod);
        Variable p2Var = new Variable(p2, new Int());
        safe_mod.addArgument(p2Var);

        SymbolTable safe_mod_symbolTable = safe_mod.getSymbolTable();
        ReturnStatement statement = new ReturnStatement(safe_mod_symbolTable);
        safe_mod.setBody(statement);

        OperatorExpression test = new OperatorExpression(BinaryOperator.Not_Equals);
        test.setSymbolTable(safe_mod_symbolTable);

        VariableExpression lhsTest = new VariableExpression(p2Var);
        lhsTest.setSymbolTable(safe_mod_symbolTable);
        test.addArgument(lhsTest);

        IntLiteral rhsTest = new IntLiteral(0);
        rhsTest.setSymbolTable(safe_mod_symbolTable);
        test.addArgument(rhsTest);

        OperatorExpression ifDiv = new OperatorExpression(BinaryOperator.Modulus, false);
        ifDiv.setSymbolTable(safe_mod_symbolTable);

        VariableExpression lhsIf = new VariableExpression(p1Var);
        lhsIf.setSymbolTable(safe_mod_symbolTable);
        ifDiv.addArgument(lhsIf);

        VariableExpression rhsIf = new VariableExpression(p2Var);
        rhsIf.setSymbolTable(safe_mod_symbolTable);
        ifDiv.addArgument(rhsIf);

        Expression elseDiv = new VariableExpression(p1Var);
        elseDiv.setSymbolTable(safe_mod_symbolTable);

        IfElseExpression expression = new IfElseExpression(test, ifDiv, elseDiv);
        expression.setSymbolTable(safe_mod_symbolTable);
        statement.addValue(expression);

        System.out.println(safe_mod);
        return safe_mod.getSimpleMethod();
    }
}
