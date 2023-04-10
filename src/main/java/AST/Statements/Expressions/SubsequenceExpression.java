package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SubsequenceExpression implements Expression {

    private final Expression seq;

    private SymbolTable symbolTable;

    private AssignmentStatement statSeq;
    private AssignmentStatement statLoHi;

    private Variable seqVar;
    private Variable loVar;
    private Variable hiVar;
    private CallExpression callExp;

    public SubsequenceExpression(SymbolTable symbolTable, Expression seq, Expression i, Expression j) {
        this.symbolTable = symbolTable;
        this.seq = seq;
        addIndexes(seq, i, j);
    }

    public VariableExpression getSequenceVariableExpression() {
        return new VariableExpression(symbolTable, seqVar, seqVar.getType());
    }

    private void addIndexes(Expression seq, Expression i, Expression j) {
        DCollection seqType = (DCollection) seq.getTypes().get(0);
        seqVar = new Variable(VariableNameGenerator.generateVariableValueName(seqType, symbolTable), seqType);
        VariableExpression seqVarExp = getSequenceVariableExpression();

        statSeq = new AssignmentStatement(symbolTable, List.of(seqVar), seq);

        Int loT = new Int();
        Int hiT = new Int();
        loVar = new Variable(VariableNameGenerator.generateVariableValueName(loT, symbolTable), loT);
        hiVar = new Variable(VariableNameGenerator.generateVariableValueName(hiT, symbolTable), hiT);

        callExp = new CallExpression(symbolTable, symbolTable.getMethod("safe_subsequence"), List.of(seqVarExp, i, j));

        statLoHi = new AssignmentStatement(symbolTable, List.of(loVar, hiVar), callExp);
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

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap) {
        Object seqVarValue = seqVar.getValue(paramsMap).get(0);
        Object loVarValue = loVar.getValue(paramsMap).get(0);
        Object hiVarValue = hiVar.getValue(paramsMap).get(0);

        List<Object> r = new ArrayList<>();
        if (seqVarValue != null && loVarValue != null && hiVarValue != null) {
            List<Object> seqVarL = (List<Object>) seqVarValue;
            Integer loVarI = (Integer) loVarValue;
            Integer hiVarI = (Integer) hiVarValue;

            r.add(seqVarL.subList(loVarI, hiVarI));
            return r;

        }
        r.add(null);
        return r;
    }
}
