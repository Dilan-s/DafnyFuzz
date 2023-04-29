package AST.Statements.Expressions.DSeq;

import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.Expressions.CallExpression;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.VariableExpression;
import AST.Statements.Statement;
import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SeqIndexExpression implements Expression {

    private final Expression index;

    private AssignmentStatement asStatSeq;
    private Variable seqVar;

    private SymbolTable symbolTable;
    private Type type;

    private Optional<AssignmentStatement> asStatInd;
    private Optional<Variable> indVar;
    private Optional<CallExpression> callExp;

    private boolean setInd;

    public SeqIndexExpression(SymbolTable symbolTable, Type type, Expression seq, Expression index) {
        this.symbolTable = symbolTable;
        this.type = type;
        this.index = index;
        this.setInd = false;
        setSeqAssign(seq);
        this.asStatInd = Optional.empty();
        this.indVar = Optional.empty();
        this.callExp = Optional.empty();
    }

    private void setSeqAssign(Expression seq) {
        DCollection seqT = (DCollection) seq.getTypes().get(0);
        seqVar = new Variable(VariableNameGenerator.generateVariableValueName(seqT, symbolTable), seqT);
        asStatSeq = new AssignmentStatement(symbolTable, List.of(seqVar), seq);
    }

    public VariableExpression getSequenceVariableExpression() {
        return new VariableExpression(symbolTable, seqVar, seqVar.getType());
    }

    public void setInd(Map<Variable, Variable> paramsMap, StringBuilder s) {
        setInd = true;
        DCollection seqT = (DCollection) seqVar.getType();
        VariableExpression seqVarExp = getSequenceVariableExpression();

        Int indT = new Int();
        Variable var = new Variable(VariableNameGenerator.generateVariableValueName(indT, symbolTable), indT);
        indVar = Optional.of(var);
        CallExpression exp = new CallExpression(symbolTable, symbolTable.getMethod(String.format("safe_index_%s", seqT.getName())), List.of(seqVarExp, index));
        callExp = Optional.of(exp);
        asStatInd = Optional.of(new AssignmentStatement(symbolTable, List.of(var), exp));
        for (Statement stat : asStatInd.get().expand()) {
            stat.execute(paramsMap, s);
        }
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    @Override
    public List<Statement> expand() {
        List<Statement> r = new ArrayList<>();
        r.addAll(asStatSeq.expand());
        if (asStatInd.isPresent() && indVar.isPresent() && callExp.isPresent()) {
            r.addAll(asStatInd.get().expand());
        } else {
            r.addAll(index.expand());
        }
        return r;
    }

    @Override
    public String toString() {
        if (asStatInd.isPresent() && indVar.isPresent() && callExp.isPresent()) {
            return String.format("%s[%s]", seqVar.getName(), indVar.get().getName());
        }
        return String.format("%s[%s]", seqVar.getName(), index);
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s) {
        List<Object> r = new ArrayList<>();

        Object seqVarValue = seqVar.getValue(paramsMap).get(0);
        Object indexValue = index.getValue(paramsMap).get(0);

        if (seqVarValue != null && indexValue != null) {
            List<Object> seqVarL = (List<Object>) seqVarValue;
            Integer indexValueI = (Integer) indexValue;

            if (0 <= indexValueI && indexValueI < seqVarL.size()) {
                r.add(seqVarL.get(indexValueI));
                return r;
            }
            if (!setInd) {
                setInd(paramsMap, s);
            }
            if (indVar.isPresent()) {
                Object indVarValue = indVar.get().getValue(paramsMap).get(0);
                if (indVarValue != null) {
                    Integer indVarValueI = (Integer) indVarValue;
                    if (0 <= indVarValueI && indVarValueI < seqVarL.size()) {
                        r.add(seqVarL.get(indVarValueI));
                        return r;
                    }
                }
            }


        }
        r.add(null);
        return r;
    }
}
