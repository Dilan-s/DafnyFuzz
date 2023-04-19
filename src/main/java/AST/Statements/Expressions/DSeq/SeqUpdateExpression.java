package AST.Statements.Expressions.DSeq;

import AST.Errors.SemanticException;
import AST.Generator.GeneratorConfig;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.Expressions.CallExpression;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.VariableExpression;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SeqUpdateExpression implements Expression {

    private final Expression seq;
    private SymbolTable symbolTable;

    private AssignmentStatement seqAssign;
    private AssignmentStatement indAssign;

    private Variable seqVar;
    private Variable indVar;
    private Expression exp;
    private CallExpression callExp;


    public SeqUpdateExpression(SymbolTable symbolTable, Expression seq, Expression ind, Expression exp) {
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
    public List<Statement> expand() {
        List<Statement> r = new ArrayList<>();

        r.addAll(seqAssign.expand());
        r.addAll(indAssign.expand());
        r.addAll(exp.expand());
        return r;
    }

    @Override
    public String toString() {
        return String.format("%s[%s := %s]", seqVar.getName(), indVar.getName(), exp);
    }

    @Override
    public List<String> toOutput() {
        Set<String> res = new HashSet<>();
        List<String> temp = new ArrayList<>();
        res.add(String.format("%s[%s := ", seqVar.getName(), indVar.getName()));

        List<String> expOptions = exp.toOutput();
        temp = new ArrayList<>();
        for (String f : res) {
            for (String expOption : expOptions) {
                String curr = f + expOption;
                temp.add(curr);
            }
        }
        if (expOptions.isEmpty()) {
            temp.addAll(res);
        }
        res = new HashSet(temp);

        temp = new ArrayList<>();
        for (String f : res) {
            temp.add(f + "]");
        }
        res = new HashSet(temp);

        List<String> r = new ArrayList<>(res);
        Collections.shuffle(r, GeneratorConfig.getRandom());
        return r.subList(0, Math.min(5, res.size()));
    }

    public VariableExpression getSequenceVariableExpression() {
        return new VariableExpression(symbolTable, seqVar, seqVar.getType());
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s) {
        List<Object> r = new ArrayList<>();

        Object seqVarValue = seqVar.getValue(paramsMap).get(0);
        Object indVarValue = indVar.getValue(paramsMap).get(0);
        Object expValue = exp.getValue(paramsMap, s).get(0);

        if (seqVarValue != null && indVarValue != null && expValue != null) {
            List<Object> seqVarL = (List<Object>) seqVarValue;
            Integer indVarI = (Integer) indVarValue;

            List<Object> l = new ArrayList<>();
            l.addAll(seqVarL.subList(0, indVarI));
            l.add(expValue);
            l.addAll(seqVarL.subList(indVarI + 1, seqVarL.size()));

            r.add(l);
            return r;
        }

        r.add(null);
        return r;
    }
}
