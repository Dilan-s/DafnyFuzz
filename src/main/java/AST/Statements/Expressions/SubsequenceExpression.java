package AST.Statements.Expressions;

import AST.Errors.InvalidArgumentException;
import AST.Errors.SemanticException;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SubsequenceExpression implements Expression {

    private SymbolTable symbolTable;

    private final Expression seq;

    private AssignmentStatement statSeq;
    private AssignmentStatement statLoHi;

    private Variable seqVar;
    private Variable loVar;
    private Variable hiVar;

    public SubsequenceExpression(SymbolTable symbolTable, Expression seq, Expression i, Expression j) {
        this.symbolTable = symbolTable;
        this.seq = seq;
        addIndexes(seq, i, j);
    }

    public SubsequenceExpression(SymbolTable symbolTable, Expression seq, Expression i) {
        this(symbolTable, seq, i , new IntLiteral(new Int(), symbolTable, 0));
    }

    public VariableExpression getSequenceVariableExpression() {
        return new VariableExpression(symbolTable, seqVar);
    }

    private void addIndexes(Expression seq, Expression i, Expression j) {
        Type seqType = seq.getTypes().get(0);
        seqVar = new Variable(VariableNameGenerator.generateVariableValueName(seqType), seqType);
        VariableExpression seqVarExp = new VariableExpression(symbolTable, seqVar);

        statSeq = new AssignmentStatement(symbolTable);
        statSeq.addAssignment(List.of(seqVar), seq);
        statSeq.addAssignmentsToSymbolTable();

        this.loVar = new Variable(VariableNameGenerator.generateVariableValueName(new Int()), new Int());
        this.hiVar = new Variable(VariableNameGenerator.generateVariableValueName(new Int()), new Int());

        CallExpression expr = new CallExpression(symbolTable, symbolTable.getMethod("safe_subsequence"), List.of(seqVarExp, i, j));

        statLoHi = new AssignmentStatement(symbolTable);
        statLoHi.addAssignment(List.of(loVar, hiVar), expr);
        statLoHi.addAssignmentsToSymbolTable();
    }

    @Override
    public List<Type> getTypes() {
        return List.of(seqVar.getType());
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

    @Override
    public int hashCode() {
        return Objects.hash(seqVar, loVar, hiVar);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubsequenceExpression)) {
            return false;
        }
        SubsequenceExpression other = (SubsequenceExpression) obj;
        return other.seqVar.equals(seqVar) && other.loVar.equals(loVar) && other.hiVar.equals(hiVar);
    }
}
