package AST.Statements.Expressions;

import AST.Errors.InvalidArgumentException;
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
import java.util.stream.Collectors;

public class IndexExpression implements Expression {

    private AssignmentStatement asStatSeq;
    private AssignmentStatement asStatInd;
    private SymbolTable symbolTable;

    private Variable seqVar;
    private Variable indVar;

    public IndexExpression(SymbolTable symbolTable, Expression seq, Expression index) {
        this.symbolTable = symbolTable;
        setSeqAndInd(seq, index);
    }

    public void setSeqAndInd(Expression seq, Expression index) {
        DCollection type = (DCollection) seq.getTypes().get(0);

        seqVar = new Variable(VariableNameGenerator.generateVariableValueName(type), type);
        VariableExpression seqVarExp = new VariableExpression(symbolTable, seqVar);

        asStatSeq = new AssignmentStatement(symbolTable);
        asStatSeq.addAssignment(List.of(seqVar), seq);
        asStatSeq.addAssignmentsToSymbolTable();

        indVar = new Variable(VariableNameGenerator.generateVariableValueName(new Int()), new Int());

        CallExpression callExp = new CallExpression(symbolTable, symbolTable.getMethod(String.format("safe_index_%s", type.getName())), List.of(seqVarExp, index));

        asStatInd = new AssignmentStatement(symbolTable);
        asStatInd.addAssignment(List.of(indVar), callExp);
        asStatInd.addAssignmentsToSymbolTable();
    }

    @Override
    public List<Type> getTypes() {
        DCollection collection = (DCollection) seqVar.getType();
        return List.of(collection.getInnerType());
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
