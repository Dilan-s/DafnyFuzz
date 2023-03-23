package AST.Statements.Expressions;

import AST.Errors.InvalidArgumentException;
import AST.Errors.SemanticException;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.SymbolTable.Method;
import AST.SymbolTable.PrimitiveTypes.Int;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.List;

public class SubsequenceExpression implements Expression {

    private SymbolTable symbolTable;

    private final Expression seq;
    private AssignmentStatement statSeq;
    private Variable seqVar;

    private AssignmentStatement statLoHi;
    private Variable loVar;
    private Variable hiVar;

    public SubsequenceExpression(SymbolTable symbolTable, Expression seq) {
        this.symbolTable = symbolTable;
        this.seq = seq;
    }

    public VariableExpression getSequenceVariableExpression() {
        return new VariableExpression(symbolTable, seqVar);
    }

    public void addIndexes(Expression i) {
        addIndexes(i, new IntLiteral(symbolTable, 0));
    }

    public void addIndexes(Expression i, Expression j) {
        Type seqType = seq.getTypes().get(0);
        seqVar = new Variable(VariableNameGenerator.generateVariableValueName(seqType), seqType);
        VariableExpression seqVarExp = new VariableExpression(symbolTable, seqVar);

        statSeq = new AssignmentStatement(symbolTable);
        statSeq.addAssignment(List.of(seqVar), seq);
        statSeq.addAssignmentsToSymbolTable();

        this.loVar = new Variable(VariableNameGenerator.generateVariableValueName(new Int()), new Int());
        this.hiVar = new Variable(VariableNameGenerator.generateVariableValueName(new Int()), new Int());

        CallExpression expr = new CallExpression(symbolTable, symbolTable.getMethod("safe_subsequence"));
        try {
            expr.addArg(seqVarExp);
            expr.addArg(i);
            expr.addArg(j);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();

        }
        statLoHi = new AssignmentStatement(symbolTable);
        statLoHi.addAssignment(List.of(loVar, hiVar), expr);
        statLoHi.addAssignmentsToSymbolTable();
    }

    @Override
    public List<Type> getTypes() {
        return seq.getTypes();
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {

    }

    @Override
    public List<String> toCode() {
        List<String> code = new ArrayList<>();

        code.addAll(statSeq.toCode());
        code.addAll(statLoHi.toCode());

        return code;
    }

    @Override
    public String toString() {
        return String.format("%s[%s..%s]", seqVar.getName(), loVar.getName(), hiVar.getName());
    }
}
