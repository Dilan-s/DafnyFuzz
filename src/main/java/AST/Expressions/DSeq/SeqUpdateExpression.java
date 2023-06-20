package AST.Expressions.DSeq;

import AST.Generator.GeneratorConfig;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Expressions.BaseExpression;
import AST.Expressions.CallMethodExpression;
import AST.Expressions.Expression;
import AST.Expressions.VariableExpression;
import AST.Statements.Statement;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SeqUpdateExpression extends BaseExpression {

    private final Expression seq;
    private SymbolTable symbolTable;

    private AssignmentStatement seqAssign;
    private AssignmentStatement indAssign;

    private Variable seqVar;
    private Variable indVar;
    private Expression exp;
    private CallMethodExpression callExp;

    private List<List<Statement>> expanded;

    public SeqUpdateExpression(SymbolTable symbolTable, Expression seq, Expression ind, Expression exp) {
        super();
        this.symbolTable = symbolTable;
        this.seq = seq;
        this.exp = exp;
        generateVariableCalls(this.seq, ind);

        this.expanded = new ArrayList<>();
        expanded.add(seqAssign.expand());
        expanded.add(indAssign.expand());
        expanded.add(exp.expand());
    }

    private void generateVariableCalls(Expression seq, Expression ind) {
        Type t = seq.getTypes().get(0);
        seqVar = new Variable(VariableNameGenerator.generateVariableValueName(t, symbolTable), t);

        seqAssign = new AssignmentStatement(symbolTable, List.of(seqVar), seq);
        VariableExpression seqVarExp = getSequenceVariableExpression();

        callExp = new CallMethodExpression(symbolTable, symbolTable.getMethod("safe_index_seq"), List.of(seqVarExp, ind));

        indVar = new Variable(VariableNameGenerator.generateVariableValueName(new Int(), symbolTable), new Int());

        indAssign = new AssignmentStatement(symbolTable, List.of(indVar), callExp);
    }

    @Override
    public boolean validForFunction() {
        return true;
    }

    @Override
    public List<Type> getTypes() {
        return List.of(seqVar.getType());
    }

    @Override
    public List<Statement> expand() {
        if (seqAssign.requireUpdate()) {
            expanded.set(0, seqAssign.expand());
        }
        if (indAssign.requireUpdate()) {
            expanded.set(1, indAssign.expand());
        }
        if (exp.requireUpdate()) {
            expanded.set(2, exp.expand());
        }

        return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public boolean requireUpdate() {
        return seqAssign.requireUpdate() || indAssign.requireUpdate() || exp.requireUpdate();
    }

    @Override
    public String toString() {
        return String.format("%s[%s := %s]", seqVar.getName(), indVar.getName(), exp);
    }

    @Override
    public String minimizedTestCase() {
        return String.format("%s[%s := %s]", seqVar.getName(), indVar.getName(), exp.minimizedTestCase());
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
    protected List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s, boolean unused) {
        List<Object> r = new ArrayList<>();

        Object seqVarValue = seqVar.getValue(paramsMap).get(0);
        Object indVarValue = indVar.getValue(paramsMap).get(0);
        Object expValue = exp.getValue(paramsMap, s).get(0);

        if (seqVarValue != null && indVarValue != null && expValue != null) {
            List<Object> seqVarL = (List<Object>) seqVarValue;
            BigInteger indVarI = (BigInteger) indVarValue;

            List<Object> l = new ArrayList<>();
            l.addAll(seqVarL.subList(0, indVarI.intValueExact()));
            l.add(expValue);
            l.addAll(seqVarL.subList(indVarI.add(BigInteger.ONE).intValueExact(), seqVarL.size()));

            r.add(l);
            return r;
        }

        r.add(null);
        return r;
    }
}
