package AST.Statements;

import AST.Generator.GeneratorConfig;
import AST.Expressions.Expression;
import AST.Expressions.Operator.BinaryOperator;
import AST.Expressions.Operator.OperatorExpression;
import AST.Expressions.VariableExpression;
import AST.Statements.util.ReturnStatus;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AssertStatement extends BaseStatement {

    private final SymbolTable symbolTable;
    private List<Variable> variables;
    private List<List<OperatorExpression>> disjuncts;
    private Set<String> disjunctsEnsures;

    private List<List<List<Statement>>> expanded;

    public AssertStatement(SymbolTable symbolTable, List<Variable> variables) {
        super();
        this.symbolTable = symbolTable;
        this.variables = variables;

        this.disjuncts = new ArrayList<>();
        this.disjunctsEnsures = new HashSet<>();
        this.expanded = new ArrayList<>();
    }

    @Override
    protected ReturnStatus execute(Map<Variable, Variable> paramMap, StringBuilder s, boolean unused) {
        List<OperatorExpression> conjuncts = new ArrayList<>();
        List<String> conjunctsEnsures = new ArrayList<>();
        List<List<Statement>> currExpand = new ArrayList<>();
        for (Variable v : variables) {
            Object val = v.getValue(paramMap).get(0);
            if (val == null) {
                return ReturnStatus.UNKNOWN;
            }
            Expression expression = v.getType().generateExpressionFromValue(symbolTable, val);
            if (expression == null) {
                return ReturnStatus.UNKNOWN;
            }

            Expression varExp = new VariableExpression(symbolTable, v, v.getType());
            OperatorExpression opE = new OperatorExpression(symbolTable, new Bool(), BinaryOperator.Equals, List.of(varExp, expression));

            conjuncts.add(opE);
            currExpand.add(opE.expand());

            conjunctsEnsures.add(v.getType().formatEnsures(v.getName(), val));
        }

        if (!conjuncts.isEmpty()) {
            String f = String.format("(%s)", String.join(" && ", conjunctsEnsures));
            if (!disjunctsEnsures.contains(f)) {
                disjuncts.add(conjuncts);
                disjunctsEnsures.add(f);
                expanded.add(currExpand);
                for (List<Statement> ss : currExpand) {
                    for (Statement st : ss) {
                        st.execute(paramMap, s);
                    }

                }

            }
        }


        return ReturnStatus.UNKNOWN;
    }

    @Override
    public List<Statement> expand() {
        for (int i = 0; i < disjuncts.size(); i++) {
            List<OperatorExpression> disjunct = disjuncts.get(i);
            for (int j = 0; j < disjunct.size(); j++) {
                Expression e = disjunct.get(j);
                if (e.requireUpdate()) {
                    expanded.get(i).set(j, e.expand());
                }

            }
        }
        List<Statement> ss = new ArrayList<>();
        List<Statement> collect = expanded.stream()
            .flatMap(Collection::stream)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
        ss.addAll(collect);
        ss.add(this);

        return ss;
    }

    @Override
    public List<String> toOutput() {
        return List.of(toString());
    }

    @Override
    public String toString() {
        if (disjuncts.isEmpty()) {
            return "assert true;\nexpect true;";
        }
        List<String> disJS = new ArrayList<>();
        for (List<OperatorExpression> es : disjuncts) {
            String e = String.format("(%s)", es.stream()
                .map(Expression::toString)
                .collect(Collectors.joining(" && ")));
            disJS.add(e);
        }

        String cond = String.join(" || ", disJS);
        return String.format("assert %s;\nexpect %s;", cond, cond);
    }

    @Override
    public String minimizedTestCase() {
        if (disjuncts.size() > 0) {
            return toString();
        }
        return "";
    }

    @Override
    public Map<String, String> invalidValidationTests() {
        Map<String, String> res = new HashMap<>();
        if (disjuncts.size() == 0) {
            res.put("assert true;", "assert false;");
            return res;
        }
        List<String> disJSInv = new ArrayList<>();
        List<String> disJSVal = new ArrayList<>();
        for (List<OperatorExpression> es : disjuncts) {
            int mutateInd = GeneratorConfig.getRandom().nextInt(es.size());
            List<String> clausesInv = new ArrayList<>();
            List<String> clausesVal = new ArrayList<>();
            for (int i = 0; i < es.size(); i++) {
                OperatorExpression expression = es.get(i);
                if (i == mutateInd) {
                    expression = expression.mutateForInvalidValidation();
                } else {
                    expression = es.get(i);
                }
                clausesInv.add(expression.toString());
                clausesVal.add(es.get(i).toString());
            }

            disJSInv.add(String.format("(%s)", String.join(" && ", clausesInv)));
            disJSVal.add(String.format("(%s)", String.join(" && ", clausesVal)));
        }

        res.put("assert " + String.join(" && ", disJSVal), "assert " + String.join(" || ", disJSInv));
        return res;

    }
}