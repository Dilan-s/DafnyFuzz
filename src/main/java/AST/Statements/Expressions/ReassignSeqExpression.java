package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReassignSeqExpression implements Expression {

    private final Expression seq;
    private SymbolTable symbolTable;

    private AssignmentStatement seqAssign;
    private AssignmentStatement indAssign;

    private Variable seqVar;
    private Variable indVar;
    private Expression exp;
    private CallExpression callExp;


    public ReassignSeqExpression(SymbolTable symbolTable, Expression seq, Expression ind, Expression exp) {
        this.symbolTable = symbolTable;
        this.seq = seq;
        this.exp = exp;
        generateVariableCalls(this.seq, ind);
    }

    private void generateVariableCalls(Expression seq, Expression ind) {
        Type t = seq.getTypes().get(0);
        seqVar = new Variable(VariableNameGenerator.generateVariableValueName(t, symbolTable), t);

        seqAssign = new AssignmentStatement(symbolTable, List.of(seqVar), seq);
        VariableExpression seqVarExp = getSequenceVariableExpression();

        callExp = new CallExpression(symbolTable, symbolTable.getMethod("safe_index_seq"), List.of(seqVarExp, ind));

        indVar = new Variable(VariableNameGenerator.generateVariableValueName(new Int(), symbolTable), new Int());

        indAssign = new AssignmentStatement(symbolTable, List.of(indVar), callExp);
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

        code.addAll(seqAssign.toCode());
        code.addAll(indAssign.toCode());
        code.addAll(exp.toCode());

        return code;
    }

    @Override
    public String toString() {
        return String.format("%s[%s := %s]", seqVar.getName(), indVar.getName(), exp);
    }

    public VariableExpression getSequenceVariableExpression() {
        return new VariableExpression(symbolTable, seqVar, seqVar.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(seqVar, indVar, exp);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ReassignSeqExpression)) {
            return false;
        }
        ReassignSeqExpression other = (ReassignSeqExpression) obj;
        return other.seqVar.equals(seqVar) && other.indVar.equals(indVar) && other.exp.equals(exp);

    }
}
