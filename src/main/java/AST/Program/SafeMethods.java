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
import AST.SymbolTable.DCollectionTypes.Seq;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Variable;
import java.util.List;

public class SafeMethods {

    public static Method safe_subsequence() {
        Method safe_subsequence = new Method(List.of(new Int(), new Int()), "safe_subsequence");
        SymbolTable symbolTable = safe_subsequence.getSymbolTable();

        String p1 = VariableNameGenerator.generateArgumentName(safe_subsequence);
        Variable p1Var = new Variable(p1, new Seq());
        VariableExpression p1VarExp = new VariableExpression(symbolTable,p1Var);
        safe_subsequence.addArgument(p1Var);

        String p2 = VariableNameGenerator.generateArgumentName(safe_subsequence);
        Variable p2Var = new Variable(p2, new Int());
        VariableExpression p2VarExp = new VariableExpression(symbolTable,p2Var);
        safe_subsequence.addArgument(p2Var);

        String p3 = VariableNameGenerator.generateArgumentName(safe_subsequence);
        Variable p3Var = new Variable(p3, new Int());
        VariableExpression p3VarExp = new VariableExpression(symbolTable,p3Var);
        safe_subsequence.addArgument(p3Var);


        BlockStatement statement = new BlockStatement(symbolTable);
        safe_subsequence.setBody(statement);

        String i = VariableNameGenerator.generateVariableValueName(new Int());
        Variable iVar = new Variable(i, new Int());
        VariableExpression iVarExp = new VariableExpression(symbolTable,iVar);

        CallExpression iSafeIndex = new CallExpression(symbolTable, symbolTable.getMethod("safe_index_seq"));
        try {
            iSafeIndex.addArg(p1VarExp);
            iSafeIndex.addArg(p2VarExp);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        AssignmentStatement asI = new AssignmentStatement(symbolTable);
        statement.addStatement(asI);
        asI.addAssignment(List.of(iVar), iSafeIndex);
        asI.addAssignmentsToSymbolTable();


        String j = VariableNameGenerator.generateVariableValueName(new Int());
        Variable jVar = new Variable(j, new Int());
        VariableExpression jVarExp = new VariableExpression(symbolTable,jVar);

        CallExpression jSafeIndex = new CallExpression(symbolTable, symbolTable.getMethod("safe_index_seq"));
        try {
            jSafeIndex.addArg(p1VarExp);
            jSafeIndex.addArg(p3VarExp);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        AssignmentStatement asJ = new AssignmentStatement(symbolTable);
        statement.addStatement(asJ);
        asJ.addAssignment(List.of(jVar), jSafeIndex);
        asJ.addAssignmentsToSymbolTable();

        IfElseStatement ifElseStatement = new IfElseStatement(symbolTable);
        statement.addStatement(ifElseStatement);

        OperatorExpression test = new OperatorExpression(symbolTable, BinaryOperator.Less_Than);
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

        VariableExpression p1VarExp = new VariableExpression(symbolTable,p1Var);

        VariableExpression p2VarExp = new VariableExpression(symbolTable,p2Var);

        OperatorExpression size = new OperatorExpression(symbolTable, UnaryOperator.Cardinality);
        size.addArgument(p1VarExp);

        OperatorExpression ltSize = new OperatorExpression(symbolTable, BinaryOperator.Less_Than);
        ltSize.addArgument(p2VarExp);
        ltSize.addArgument(size);

        OperatorExpression gtZero = new OperatorExpression(symbolTable, BinaryOperator.Less_Than_Or_Equal);
        gtZero.addArgument(new IntLiteral(symbolTable, 0));
        gtZero.addArgument(p2VarExp);

        OperatorExpression test = new OperatorExpression(symbolTable, BinaryOperator.And);
        test.addArgument(ltSize);
        test.addArgument(gtZero);

        IfElseExpression ifElseExpression = new IfElseExpression(symbolTable, test, p2VarExp, new IntLiteral(symbolTable, 0));
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

        SymbolTable symbolTable = safe_div.getSymbolTable();
        ReturnStatement statement = new ReturnStatement(symbolTable);
        safe_div.setBody(statement);

        OperatorExpression test = new OperatorExpression(symbolTable, BinaryOperator.Not_Equals);

        VariableExpression lhsTest = new VariableExpression(symbolTable,p2Var);
        test.addArgument(lhsTest);

        IntLiteral rhsTest = new IntLiteral(symbolTable, 0);
        test.addArgument(rhsTest);

        OperatorExpression ifDiv = new OperatorExpression(symbolTable, BinaryOperator.Divide, false);

        VariableExpression lhsIf = new VariableExpression(symbolTable,p1Var);
        ifDiv.addArgument(lhsIf);

        VariableExpression rhsIf = new VariableExpression(symbolTable,p2Var);
        ifDiv.addArgument(rhsIf);

        Expression elseDiv = new VariableExpression(symbolTable,p1Var);

        IfElseExpression expression = new IfElseExpression(symbolTable, test, ifDiv, elseDiv);
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

        SymbolTable symbolTable = safe_mod.getSymbolTable();
        ReturnStatement statement = new ReturnStatement(symbolTable);
        safe_mod.setBody(statement);

        OperatorExpression test = new OperatorExpression(symbolTable, BinaryOperator.Not_Equals);

        VariableExpression lhsTest = new VariableExpression(symbolTable,p2Var);
        test.addArgument(lhsTest);

        IntLiteral rhsTest = new IntLiteral(symbolTable, 0);
        test.addArgument(rhsTest);

        OperatorExpression ifDiv = new OperatorExpression(symbolTable, BinaryOperator.Modulus, false);

        VariableExpression lhsIf = new VariableExpression(symbolTable,p1Var);
        ifDiv.addArgument(lhsIf);

        VariableExpression rhsIf = new VariableExpression(symbolTable,p2Var);
        ifDiv.addArgument(rhsIf);

        Expression elseDiv = new VariableExpression(symbolTable,p1Var);

        IfElseExpression expression = new IfElseExpression(symbolTable, test, ifDiv, elseDiv);
        statement.addValue(expression);

        System.out.println(safe_mod);
        return safe_mod.getSimpleMethod();
    }
}
