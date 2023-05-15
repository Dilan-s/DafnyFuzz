package AST.Statements;

import AST.Generator.GeneratorConfig;
import AST.Generator.VariableNameGenerator;
import AST.Statements.Expressions.CallExpression;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.VariableExpression;
import AST.Statements.util.ReturnStatus;
import AST.StringUtils;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ForStatement extends BaseStatement {

    private SymbolTable symbolTable;

    private Variable loopVar;
    private Variable initVar;
    private Variable finalVar;
    private Statement body;

    private Statement assignment;

    private Optional<Statement> statCallMinMax;
    private Direction direction;
    private Map<String, Set<String>> loopInvariants;

    private List<List<Statement>> expanded;
    private boolean update;

    public ForStatement(SymbolTable symbolTable, Expression initExp, Expression finalExp, Variable loopVar, Statement body) {
        this.symbolTable = symbolTable;

        Type loopVarType = loopVar.getType();
        this.initVar = new Variable(VariableNameGenerator.generateVariableValueName(loopVarType, symbolTable), loopVarType);
        initVar.setConstant();
        this.finalVar = new Variable(VariableNameGenerator.generateVariableValueName(loopVarType, symbolTable), loopVarType);
        finalVar.setConstant();

        assignment = new AssignmentStatement(this.symbolTable,  List.of(initVar, finalVar),  List.of(initExp, finalExp));

        this.loopVar = loopVar;
        this.body = body;

        this.statCallMinMax = Optional.empty();
        this.direction = null;
        this.update = false;
        this.loopInvariants = new HashMap<>();

        this.expanded = new ArrayList<>();
        expanded.add(assignment.expand());
        expanded.add(List.of(this));
    }

    @Override
    protected ReturnStatus execute(Map<Variable, Variable> paramMap, StringBuilder s, boolean unused) {
        Integer initVarValue = (Integer) initVar.getValue(paramMap).get(0);
        Integer finalVarValue = (Integer) finalVar.getValue(paramMap).get(0);

        if (direction != null) {
            if (!direction.validBounds(initVarValue, finalVarValue)) {
                setMinMaxCall(paramMap, s);
                initVarValue = (Integer) initVar.getValue(paramMap).get(0);
                finalVarValue = (Integer) finalVar.getValue(paramMap).get(0);
            }
            loopVar.setValue(symbolTable, paramMap, initVarValue);

            for (int i = direction.getInitBound(initVarValue); direction.withinFinalBound(i, finalVarValue); i = direction.iterate(i)) {
                loopVar.setValue(symbolTable, paramMap, i);
                Set<Variable> modSet = body.getModifies();
                addInvariantForModSet(modSet, paramMap);
                ReturnStatus execute = body.execute(paramMap, s);
                if (execute == ReturnStatus.RETURN) {
                    return execute;
                } else if (execute == ReturnStatus.BREAK) {
                    return ReturnStatus.UNKNOWN;
                }
            }
        } else {
            direction = Direction.setDirection(initVarValue, finalVarValue);
            loopVar.setValue(symbolTable, paramMap, initVarValue);
            for (int i = direction.getInitBound(initVarValue); direction.withinFinalBound(i, finalVarValue); i = direction.iterate(i)) {
                loopVar.setValue(symbolTable, paramMap, i);
                Set<Variable> modSet = body.getModifies();
                addInvariantForModSet(modSet, paramMap);
                ReturnStatus execute = body.execute(paramMap, s);
                if (execute == ReturnStatus.RETURN) {
                    return execute;
                } else if (execute == ReturnStatus.BREAK) {
                    return ReturnStatus.UNKNOWN;
                }
            }

        }
        Set<Variable> modSet = body.getModifies();
        addInvariantForModSet(modSet, paramMap);
        return ReturnStatus.UNKNOWN;
    }

    private void addInvariantForModSet(Set<Variable> modSet, Map<Variable, Variable> paramMap) {
        Set<Variable> vs = modSet.stream()
            .filter(v -> symbolTable.getAllVariables(v.getType()).contains(v))
            .filter(v -> v != loopVar)
            .collect(Collectors.toSet());

        if (!vs.isEmpty()) {

            Object value = loopVar.getValue(paramMap).get(0);
            String key = loopVar.getType().formatEnsures(loopVar.getName(), value);

            if (!loopInvariants.containsKey(key)) {
                loopInvariants.put(key, new HashSet<>());
            }
            Set<String> invs = loopInvariants.get(key);

            List<String> rhs = new ArrayList<>();
            for (Variable v : vs) {
                Object obj = v.getValue(paramMap).get(0);
                rhs.add(String.format("(%s)", v.getType().formatEnsures(v.getName(), obj)));
            }
            String rhsV = "(" + String.join(" && ", rhs) + ")";
            invs.add(rhsV);

        }
    }

    @Override
    public Set<Variable> getModifies() {
        return body.getModifies();
    }

    private void setMinMaxCall(Map<Variable, Variable> paramMap, StringBuilder s) {
        update = true;

        Type initT = initVar.getType();
        Type finalT = finalVar.getType();

        Expression initVarExp = new VariableExpression(symbolTable, initVar, initT);
        Expression finalVarExp = new VariableExpression(symbolTable, finalVar, finalT);

        CallExpression exp = new CallExpression(symbolTable, symbolTable.getMethod("safe_min_max"), List.of(initVarExp, finalVarExp));

        statCallMinMax = Optional.of(new AssignmentStatement(symbolTable, List.of(initVar, finalVar), exp));

        List<Statement> expand = statCallMinMax.get().expand();
        for (Statement stat : expand) {
            stat.execute(paramMap, s);
        }
        expanded.add(1, statCallMinMax.get().expand());

        direction = Direction.TO;
    }

    @Override
    public List<Statement> expand() {
        if (assignment.requireUpdate()) {
            expanded.set(0, assignment.expand());
        }

        if (statCallMinMax.isPresent() && statCallMinMax.get().requireUpdate()) {
            expanded.set(1, statCallMinMax.get().expand());
        }
        update = false;
        return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public List<String> toOutput() {
        Set<String> res = new HashSet<>();

        Direction direction = this.direction == null ? Direction.TO : this.direction;

        String start = String.format("for %s := %s %s %s \n", loopVar.getName(), initVar.getName(), direction.rep, finalVar.getName());
        start = start + StringUtils.indent(direction.invariantClause(loopVar, finalVar));
        if (!loopInvariants.isEmpty()) {
            List<String> loopInvariants = this.loopInvariants.entrySet().stream()
                .map(x -> String.format("(%s) ==> (%s)", x.getKey(),
                    String.join(" || ", x.getValue())))
                .collect(Collectors.toList());

            start = start + " && (" + String.join(" && ", loopInvariants) + ")";
        }

        start = start + "\n{\n";
        res.add(start);

        List<String> temp = new ArrayList<>();
        List<String> bodyOptions = body.toOutput();
        for (String f : res) {
            for (String bodyOption : bodyOptions) {
                String curr = f + StringUtils.indent(bodyOption);
                temp.add(curr);
            }
        }
        if (bodyOptions.isEmpty()) {
            temp.addAll(res);
        }

        res = new HashSet<>(temp);
        temp = new ArrayList<>();
        for (String f : res) {
            String curr = f + "\n}";
            temp.add(curr);
        }

        res = new HashSet<>(temp);
        List<String> r = new ArrayList<>(res);
        Collections.shuffle(r, GeneratorConfig.getRandom());
        return r.subList(0, Math.min(5, r.size()));
    }

    @Override
    public String minimizedTestCase() {
        Direction direction = this.direction == null ? Direction.TO : this.direction;
        if (body.getNoOfUses() > 0) {
            String res = String.format("for %s := %s %s %s \n", loopVar.getName(), initVar.getName(), direction.rep, finalVar.getName());
            res = res + StringUtils.indent(direction.invariantClause(loopVar, finalVar));
            if (!loopInvariants.isEmpty()) {
                List<String> loopInvariants = this.loopInvariants.entrySet().stream()
                    .map(x -> String.format("((%s) ==> (%s))", x.getKey(),
                        String.join(" || ", x.getValue())))
                    .collect(Collectors.toList());

                res = res + " && (" + String.join(" && ", loopInvariants) + ")";
            }

            res = res + "\n{\n";
            res = res + StringUtils.indent(body.minimizedTestCase()) + "\n";
            res = res + "}";
            return res;
        }
        return "";
    }

    @Override
    public String invalidValidationTests() {
        Direction direction = this.direction == null ? Direction.TO : this.direction;
        if (body.getNoOfUses() > 0) {
            String res = String.format("for %s := %s %s %s \n", loopVar.getName(), initVar.getName(), direction.rep, finalVar.getName());
            res = res + StringUtils.indent(direction.invariantClause(loopVar, finalVar));
            if (!loopInvariants.isEmpty()) {
                List<String> loopInvariants = this.loopInvariants.entrySet().stream()
                    .map(x -> String.format("((%s) ==> (%s))", x.getKey(),
                        String.join(" || ", x.getValue())))
                    .collect(Collectors.toList());

                res = res + " && (" + String.join(" && ", loopInvariants) + ")";
            }

            res = res + "\n{\n";
            res = res + StringUtils.indent(body.invalidValidationTests()) + "\n";
            res = res + "}";
            return res;
        }
        return "";
    }

    @Override
    public String toString() {
        Direction direction = this.direction == null ? Direction.TO : this.direction;
        String res = String.format("for %s := %s %s %s \n", loopVar.getName(), initVar.getName(), direction.rep, finalVar.getName());
        res = res + StringUtils.indent(direction.invariantClause(loopVar, finalVar));
        if (!loopInvariants.isEmpty()) {
            List<String> loopInvariants = this.loopInvariants.entrySet().stream()
                .map(x -> String.format("((%s) ==> (%s))", x.getKey(),
                    String.join(" || ", x.getValue())))
                .collect(Collectors.toList());

            res = res + " && (" + String.join(" && ", loopInvariants) + ")";
        }

        res = res + "\n{\n";
        res = res + StringUtils.indent(body.toString()) + "\n";
        res = res + "}";
        return res;
    }

    @Override
    public boolean requireUpdate() {
        return update || assignment.requireUpdate() || (statCallMinMax.isPresent() && statCallMinMax.get().requireUpdate()) || body.requireUpdate();
    }

    private enum Direction {
        TO("to"),
        DOWNTO("downto"),
        ;

        private String rep;

        Direction(String rep) {
            this.rep = rep;
        }

        public static Direction setDirection(Integer initVarValue, Integer finalVarValue) {
            if (initVarValue < finalVarValue) {
                return TO;
            } else {
                return DOWNTO;
            }
        }

        @Override
        public String toString() {
            return super.toString();
        }

        public boolean validBounds(Integer lower, Integer upper) {
            if (this == DOWNTO) {
                return lower >= upper;
            } else {
                return lower <= upper;
            }
        }

        public int getInitBound(Integer init) {
            if (this == DOWNTO) {
                return init - 1;
            } else {
                return init;
            }
        }

        public int getFinalBound(Integer fin) {
            if (this == DOWNTO) {
                return fin;
            } else {
                return fin -1;
            }
        }

        public int iterate(int i) {
            if (this == DOWNTO) {
                return i - 1;
            } else {
                return i + 1;
            }
        }

        public String invariantClause(Variable loopVar, Variable finalVar) {
            if (this == DOWNTO) {
                return String.format("invariant (%s - %s >= 0)", loopVar.getName(), finalVar.getName());
            } else {
                return String.format("invariant (%s - %s >= 0)", finalVar.getName(), loopVar.getName());
            }
        }

        public boolean withinFinalBound(int i, Integer finalVarValue) {
            if (this == DOWNTO) {
                return i >= finalVarValue;
            } else {
                return i < finalVarValue;
            }
        }
    }
}
