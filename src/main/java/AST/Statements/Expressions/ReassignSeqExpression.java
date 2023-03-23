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

public class ReassignSeqExpression implements Expression {

    private SymbolTable symbolTable;

    private Expression seq;
    private Variable seqVar;
    private Expression ind;
    private Variable indVar;
    private Expression exp;


    public ReassignSeqExpression(SymbolTable symbolTable, Expression seq, Expression ind, Expression exp) {
        this.symbolTable = symbolTable;
        this.seq = seq;
        Type t = seq.getTypes().get(0);
        seqVar = new Variable(VariableNameGenerator.generateVariableValueName(t), t);
        this.ind = ind;
        this.exp = exp;
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

        AssignmentStatement seqAssign = new AssignmentStatement(symbolTable);
        seqAssign.addAssignment(List.of(seqVar), seq);
        seqAssign.addAssignmentsToSymbolTable();

        code.addAll(seqAssign.toCode());

        CallExpression callExp = new CallExpression(symbolTable, symbolTable.getMethod("safe_index_seq"));
        try {
            callExp.addArg(new VariableExpression(symbolTable, seqVar));
            callExp.addArg(ind);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        AssignmentStatement indAssign = new AssignmentStatement(symbolTable);
        indVar = new Variable(VariableNameGenerator.generateVariableValueName(new Int()), new Int());
        indAssign.addAssignment(List.of(indVar), callExp);
        indAssign.addAssignmentsToSymbolTable();

        code.addAll(indAssign.toCode());

        code.addAll(exp.toCode());
        return code;
    }

    @Override
    public String toString() {
        return String.format("%s[%s := %s]", seqVar.getName(), indVar.getName(), exp);
    }

    public VariableExpression getSequenceVariableExpression() {
        return new VariableExpression(symbolTable, seqVar);
    }
}
