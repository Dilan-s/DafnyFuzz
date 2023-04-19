package AST.Statements.Expressions.DMap;

import AST.Errors.SemanticException;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.IfElseExpression;
import AST.Statements.Expressions.Operator.BinaryOperator;
import AST.Statements.Expressions.Operator.OperatorExpression;
import AST.Statements.Expressions.VariableExpression;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DMapSelection implements Expression {

    private final SymbolTable symbolTable;
    private Type type;

    private AssignmentStatement mapAssign;
    private IfElseExpression ifElseExp;
    private AssignmentStatement indexAssign;

    public DMapSelection(SymbolTable symbolTable, Type type, Expression map, Expression index, Expression def) {
        this.symbolTable = symbolTable;
        this.type = type;
        generateVariableCalls(map, index, def);
    }

    private void generateVariableCalls(Expression map, Expression index, Expression def) {
        Type tM = map.getTypes().get(0);
        Variable mapVar = new Variable(VariableNameGenerator.generateVariableValueName(tM, symbolTable), tM);
        mapAssign = new AssignmentStatement(symbolTable, List.of(mapVar), map);

        Type tI = index.getTypes().get(0);
        Variable indexVar = new Variable(VariableNameGenerator.generateVariableValueName(tI, symbolTable), tI);
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
    public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s) {
        return ifElseExp.getValue(paramsMap, s);
    }

    @Override
    public List<Statement> expand() {
        List<Statement> s = new ArrayList<>();

        s.addAll(mapAssign.expand());
        s.addAll(indexAssign.expand());
        s.addAll(ifElseExp.expand());

        return s;
    }

    @Override
    public List<String> toOutput() {
        return ifElseExp.toOutput();
    }

    @Override
    public String toString() {
        return ifElseExp.toString();
    }
}
