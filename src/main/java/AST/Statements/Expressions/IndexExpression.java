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
import java.util.Map;
import java.util.Objects;

public class IndexExpression implements Expression {

    private final Expression seq;
    private AssignmentStatement asStatSeq;
    private AssignmentStatement asStatInd;
    private SymbolTable symbolTable;
    private Type type;

    private Variable seqVar;
    private Variable indVar;
    private CallExpression callExp;

    public IndexExpression(SymbolTable symbolTable, Type type, Expression seq, Expression index) {
        this.symbolTable = symbolTable;
        this.type = type;
        this.seq = seq;
        setSeqAndInd(index);
    }

    public VariableExpression getSequenceVariableExpression() {
        return new VariableExpression(symbolTable, seqVar, seqVar.getType());
    }

    public void setSeqAndInd(Expression index) {
        DCollection seqT = (DCollection) seq.getTypes().get(0);

        seqVar = new Variable(VariableNameGenerator.generateVariableValueName(seqT, symbolTable), seqT);
        VariableExpression seqVarExp = getSequenceVariableExpression();

        asStatSeq = new AssignmentStatement(symbolTable, List.of(seqVar), seq);

        Int indT = new Int();
        indVar = new Variable(VariableNameGenerator.generateVariableValueName(indT, symbolTable), indT);

        callExp = new CallExpression(symbolTable, symbolTable.getMethod(String.format("safe_index_%s", seqT.getName())), List.of(seqVarExp, index));

        asStatInd = new AssignmentStatement(symbolTable, List.of(indVar), callExp);
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

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap) {
        List<Object> r = new ArrayList<>();

        Object seqVarValue = seqVar.getValue(paramsMap).get(0);
        Object indVarValue = indVar.getValue(paramsMap).get(0);

        if (seqVarValue != null && indVarValue != null) {
            List<Object> seqVarL = (List<Object>) seqVarValue;
            Integer indVarI = (Integer) indVarValue;

            r.add(seqVarL.get(indVarI));
            return r;
        }
        r.add(null);
        return r;
    }
}
