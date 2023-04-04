package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IndexExpression implements Expression {

    private AssignmentStatement asStatSeq;
    private AssignmentStatement asStatInd;
    private SymbolTable symbolTable;
    private Type type;

    private Variable seqVar;
    private Variable indVar;

    public IndexExpression(SymbolTable symbolTable, Type type, Expression seq, Expression index) {
        this.symbolTable = symbolTable;
        this.type = type;
        setSeqAndInd(seq, index);
    }

    public VariableExpression getSequenceVariableExpression() {
        return new VariableExpression(symbolTable, seqVar, seqVar.getType());
    }

    public void setSeqAndInd(Expression seq, Expression index) {
        DCollection seqT = (DCollection) seq.getTypes().get(0);

        seqVar = new Variable(VariableNameGenerator.generateVariableValueName(seqT, symbolTable), seqT);
        VariableExpression seqVarExp = getSequenceVariableExpression();

        asStatSeq = new AssignmentStatement(symbolTable);
        asStatSeq.addAssignment(List.of(seqVar), seq);
        asStatSeq.addAssignmentsToSymbolTable();

        Int indT = new Int();
        Type indType = index.getTypes().get(0);
        indVar = new Variable(VariableNameGenerator.generateVariableValueName(indT, symbolTable), indT);

        if (seqT.getValue() != null && indType.getValue() != null) {
            Integer i = (Integer) indType.getValue();
            int iIndex = i < seqT.getSize() && 0 <= i ? i : 0;
            indT.setValue(iIndex);
        }

        CallExpression callExp = new CallExpression(symbolTable, symbolTable.getMethod(String.format("safe_index_%s", seqT.getName())), List.of(seqVarExp, index));

        asStatInd = new AssignmentStatement(symbolTable);
        asStatInd.addAssignment(List.of(indVar), callExp);
        asStatInd.addAssignmentsToSymbolTable();
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {

    }

    @Override
    public List<String> toCode() {
        List<String> code = new ArrayList<>();

        code.addAll(asStatSeq.toCode());
        code.addAll(asStatInd.toCode());

        return code;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", seqVar.getName(), indVar.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(seqVar, indVar);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IndexExpression)) {
            return false;
        }
        IndexExpression other = (IndexExpression) obj;
        return other.seqVar.equals(seqVar) && other.indVar.equals(indVar);
    }
}
