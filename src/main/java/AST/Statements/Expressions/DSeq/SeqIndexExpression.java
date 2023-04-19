package AST.Statements.Expressions.DSeq;

import AST.Errors.SemanticException;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.Expressions.CallExpression;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.VariableExpression;
import AST.Statements.Statement;
import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SeqIndexExpression implements Expression {

    private final Expression seq;
    private AssignmentStatement asStatSeq;
    private AssignmentStatement asStatInd;
    private SymbolTable symbolTable;
    private Type type;

    private Variable seqVar;
    private Variable indVar;
    private CallExpression callExp;

    public SeqIndexExpression(SymbolTable symbolTable, Type type, Expression seq, Expression index) {
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
    public List<Statement> expand() {
        List<Statement> r = new ArrayList<>();
        r.addAll(asStatSeq.expand());
        r.addAll(asStatInd.expand());
        return r;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", seqVar.getName(), indVar.getName());
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s) {
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
