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
import java.util.Objects;
import java.util.stream.Collectors;

public class SeqIndexExpression implements Expression {

    private AssignmentStatement asStatSeq;
    private AssignmentStatement asStatInd;
    private SymbolTable symbolTable;

    private Expression seq;
    private Variable seqVar;

    private Variable indVar;

    public SeqIndexExpression(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public void setSeqAndInd(Expression seq, Expression index) {
        this.seq = seq;
        Type type = seq.getTypes().get(0);

        seqVar = new Variable(VariableNameGenerator.generateVariableValueName(type), type);
        VariableExpression seqVarExp = new VariableExpression(symbolTable, seqVar);

        asStatSeq = new AssignmentStatement(symbolTable);
        asStatSeq.addAssignment(List.of(seqVar), seq);
        asStatSeq.addAssignmentsToSymbolTable();

        indVar = new Variable(VariableNameGenerator.generateVariableValueName(new Int()), new Int());

        asStatInd = new AssignmentStatement(symbolTable);
        CallExpression callExp = new CallExpression(symbolTable, symbolTable.getMethod("safe_index_seq"));
        try {
            callExp.addArg(seqVarExp);
            callExp.addArg(index);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
        asStatInd.addAssignment(List.of(indVar), callExp);
        asStatInd.addAssignmentsToSymbolTable();
    }

    @Override
    public List<Type> getTypes() {
        return seq.getTypes().stream()
            .map(Type::getInnerType)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
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
}
