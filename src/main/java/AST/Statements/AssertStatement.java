package AST.Statements;

import AST.Statements.Expressions.Expression;
import AST.Statements.util.ReturnStatus;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AssertStatement extends BaseStatement {

    private final SymbolTable symbolTable;
    private final List<Variable> variables;
    private final Set<String> disjuncts;

    public AssertStatement(SymbolTable symbolTable, List<Variable> variables) {
        super();
        this.symbolTable = symbolTable;
        this.variables = variables;

        this.disjuncts = new HashSet<>();
    }

    @Override
    protected ReturnStatus execute(Map<Variable, Variable> paramMap, StringBuilder s, boolean unused) {
        List<String> conjuncts = new ArrayList<>();
        for (Variable v : variables) {
            Object val = v.getValue(paramMap).get(0);
            if (val == null) {
                return null;
            }
            String e = v.getType().formatEnsures(v.getName(), val);
            conjuncts.add(e);
        }
        String e = "(" + String.join(" && ", conjuncts) + ")";
        disjuncts.add(e);
        return ReturnStatus.UNKNOWN;
    }

    @Override
    public List<Statement> expand() {
        return List.of(this);
    }

    @Override
    public List<String> toOutput() {
        return List.of(toString());
    }

    @Override
    public String toString() {
        if (disjuncts.size() == 0) {
            return "assert true;";
        }
        return String.format("assert %s;", String.join(" || ", disjuncts));
    }

    @Override
    public String minimizedTestCase() {
        if (disjuncts.size() > 0) {
            return toString();
        }
        return "";
    }
}