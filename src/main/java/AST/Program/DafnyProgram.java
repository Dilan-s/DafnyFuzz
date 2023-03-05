package AST.Program;

import AST.Generator.RandomTokenGenerator;
import AST.Generator.VariableNameGenerator;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.IfElseExpression;
import AST.Statements.Expressions.IntLiteral;
import AST.Statements.Expressions.Operator.BinaryOperator;
import AST.Statements.Expressions.OperatorExpression;
import AST.Statements.Expressions.VariableExpression;
import AST.Statements.ReturnStatement;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.PrimitiveTypes.Int;
import AST.SymbolTable.PrimitiveTypes.Void;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Variable;
import java.util.Random;

public class DafnyProgram {

    private final Random random;

    public DafnyProgram(long seed) {
        random = new Random(seed);
    }

    public DafnyProgram() {
        random = new Random();
    }

    public void generateProgram() {
        Method main = new Method(new Void(), "Main");

        Method safe_division = safe_division();
        System.out.println(safe_division);
        main.addMethod(safe_division);

        Method safe_modulus = safe_modulus();
        System.out.println(safe_modulus);
        main.addMethod(safe_modulus);

        RandomTokenGenerator randomTokenGenerator = new RandomTokenGenerator(random);
        Statement statement = randomTokenGenerator.generateBody(main);
        main.setBody(statement);
        System.out.println(main);
    }

//    private Method safe

    private Method safe_division() {
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

        return safe_div;
    }

    private Method safe_modulus() {
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
        return safe_mod;
    }
}
