package AST.SymbolTable;

import AST.Expressions.DClass.DClassValue;
import AST.Expressions.Expression;
import AST.Expressions.Method.CallBaseMethodExpression;
import AST.Expressions.Method.CallMethodExpression;
import AST.Generator.GeneratorConfig;
import AST.Generator.RandomExpressionGenerator;
import AST.Generator.RandomTypeGenerator;
import AST.Generator.VariableNameGenerator;
import AST.Statements.Statement;
import AST.Statements.util.ReturnStatus;
import AST.StringUtils;
import AST.SymbolTable.Types.PrimitiveTypes.Void;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.DClass;
import AST.SymbolTable.Types.UserDefinedTypes.DataType.DataType;
import AST.SymbolTable.Types.UserDefinedTypes.TypeAlias;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Method implements Identifier {

    private final String name;
    private final List<Type> returnTypes;
    private final List<Variable> args;
    private final List<Variable> retArgs;
    private final SymbolTable symbolTable;
    private Statement body;

    private final Set<String> requires;
    private final Set<String> ensures;
    private Set<String> modifies;

    private int noOfUses;

    public Method(List<Type> returnTypes, String name, SymbolTable symbolTable, List<Variable> args) {
        this.returnTypes = returnTypes;
        this.name = name;
        this.symbolTable = symbolTable;
        this.args = args;
        this.requires = new HashSet<>();
        this.ensures = new HashSet<>();
        this.modifies = new HashSet<>();

        this.retArgs = returnTypes.stream()
            .filter(x -> !x.equals(new Void()))
            .map(t -> new Variable(VariableNameGenerator.generateReturnVariableName(getName()), t))
            .collect(Collectors.toList());
        this.noOfUses = 0;
    }

    public Method(List<Type> returnTypes, String name, SymbolTable symbolTable) {
        this(returnTypes, name, symbolTable, new ArrayList<>());
    }

    public Method(Type returnTypes, String name, SymbolTable symbolTable) {
        this(List.of(returnTypes), name, symbolTable);
    }

    public Method(Type returnTypes, String name) {
        this(List.of(returnTypes), name, new SymbolTable());
    }

    public Method(List<Type> returnTypes, String name) {
        this(returnTypes, name, new SymbolTable());
    }

    public void incrementUse() {
        this.noOfUses++;
    }

    public int getNoOfUses() {
        return noOfUses;
    }

    public void addArgument(String name, Type type) {
        addArgument(new Variable(name, type));
    }

    public void addArgument(List<Variable> vars) {
        for (Variable v : vars) {
            addArgument(v);
        }
    }

    public void addArgument(Variable argument) {
        args.add(argument);
        argument.setConstant();
        for (Variable arg : argument.getSymbolTableArgs()) {
            symbolTable.addVariable(arg);
            arg.setDeclared();
        }
    }

    public List<Type> getReturnTypes() {
        return returnTypes;
    }

    public boolean hasReturn() {
        return (returnTypes.size() == 1 && !returnTypes.get(0).equals(new Void()))
            || returnTypes.size() > 1;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public List<Variable> getArgs() {
        return args;
    }

    public List<Type> getArgTypes() {
        return args.stream().map(Variable::getType).collect(Collectors.toList());
    }

    public void setBody(Statement body) {
        this.body = body;

        Set<Variable> modifiedByBody = body.getModifies();
        this.modifies = args.stream()
            .filter(x -> modifiedByBody.stream().anyMatch(y -> y.modified(x)))
            .map(Variable::getName)
            .collect(Collectors.toSet());
    }

    @Override
    public String getName() {
        return name;
    }

    public String toCode() {
        return toCode(true);
    }

    public String toCode(boolean printAll) {
        List<String> code = new ArrayList<>();

        if (printAll) {
            for (DataType d : RandomTypeGenerator.DEFINED_DATA_TYPES) {
                code.add(d.declaration());
            }
            for (TypeAlias t : RandomTypeGenerator.DEFINED_TYPE_ALIAS) {
                code.add(t.declaration());
            }

            for (DClass t : RandomTypeGenerator.DEFINED_DCLASS) {
                code.add(t.declaration());
            }

            List<Method> allMethods = symbolTable.getAllMethods();
            for (Method m : allMethods) {
                code.add(m.toCode(false));
            }
            List<Function> allFunctions = symbolTable.getAllFunctions();
            for (Function f : allFunctions) {
                code.add(f.toCode());
            }
        }

        code.add(declarationLine());
        code.add(StringUtils.indent(body.toString()));
        code.add("}\n");
        return String.join("\n", code);
    }

    @Override
    public String toString() {
        return toCode(true);
    }

    public List<String> toOutput(boolean printAll) {
        Set<String> res = new HashSet<>();
        List<String> temp = new ArrayList<>();

        res.add("");
        if (printAll) {
            String dataTypes = "";
            for (DataType d : RandomTypeGenerator.DEFINED_DATA_TYPES) {
                dataTypes = dataTypes + d.declaration() + "\n";
            }
            temp = new ArrayList<>();
            for (String f : res) {
                temp.add(f + dataTypes);
            }
            if (dataTypes.isEmpty()) {
                temp.addAll(res);
            }
            res = new HashSet<>(temp);

            String typeAliases = "";
            for (TypeAlias t : RandomTypeGenerator.DEFINED_TYPE_ALIAS) {
                typeAliases = typeAliases + t.declaration() + "\n";
            }
            temp = new ArrayList<>();
            for (String f : res) {
                temp.add(f + typeAliases);
            }
            if (typeAliases.isEmpty()) {
                temp.addAll(res);
            }
            res = new HashSet<>(temp);

            String dclasses = "";
            for (DClass t : RandomTypeGenerator.DEFINED_DCLASS) {
                dclasses = dclasses + t.declaration() + "\n";
            }
            temp = new ArrayList<>();
            for (String f : res) {
                temp.add(f + dclasses);
            }
            if (typeAliases.isEmpty()) {
                temp.addAll(res);
            }
            res = new HashSet<>(temp);

            List<Method> allMethods = symbolTable.getAllMethods();

            for (Method m : allMethods) {
                List<String> methodBodyOptions = m.toOutput(false);
                temp = new ArrayList<>();
                for (String f : res) {
                    for (String mBody : methodBodyOptions) {
                        String curr = f + mBody;
                        temp.add(curr);
                    }
                }
                res = new HashSet<>(temp);

                List<String> r = new ArrayList<>(res);
                Collections.shuffle(r, GeneratorConfig.getRandom());
                res = new HashSet<>(r.subList(0, Math.min(5, res.size())));
            }

            List<Function> allFunctions = symbolTable.getAllFunctions();

            for (Function func : allFunctions) {
                List<String> functionBodyOptions = func.toOutput();
                temp = new ArrayList<>();
                for (String f : res) {
                    for (String fBody : functionBodyOptions) {
                        String curr = f + fBody;
                        temp.add(curr);
                    }
                }

                res = new HashSet<>(temp);

                List<String> r = new ArrayList<>(res);
                Collections.shuffle(r, GeneratorConfig.getRandom());
                res = new HashSet<>(r.subList(0, Math.min(5, res.size())));
            }

        }

        temp = new ArrayList<>();
        for (String f : res) {
            for (String b : body.toOutput()) {
                String func = declarationLine() + "\n";
                String curr = StringUtils.indent(b);
                curr = func + curr + "\n}\n\n";
                curr = f + curr;
                temp.add(curr);
            }
        }
        res = new HashSet<>(temp);

        List<String> r = new ArrayList<>(res);
        Collections.shuffle(r, GeneratorConfig.getRandom());
        return r.subList(0, Math.min(5, res.size()));
    }

    public List<String> toOutput() {
        return toOutput(true);
    }

    public String declarationLine() {
        String arguments = getArgs().stream()
            .map(Variable::toString)
            .collect(Collectors.joining(", "));

        String types = retArgs.stream()
            .map(v -> String.format("%s: %s", v.getName(), v.getType().getVariableType()))
            .collect(Collectors.joining(", "));

        String res = String.format("method %s(%s) returns (%s)\n", getName(), arguments, types);

        if (!requires.isEmpty()) {
            res = res + StringUtils.indent("requires " + StringUtils.intersperse(" || ", requires)) + "\n";
        }
        if (!ensures.isEmpty()) {
            res = res + StringUtils.indent("ensures " + StringUtils.intersperse(" && ", ensures)) + "\n";
        }
        if (!modifies.isEmpty()) {
            res = res + StringUtils.indent("modifies " + StringUtils.intersperse(", ", modifies)) + "\n";
        }

        res = res + "{";
        return res;
    }

    public void addMethod(Method method) {
        symbolTable.addMethod(method);
    }

    public List<Object> execute(List<Variable> params) {
        return execute(params, new StringBuilder());
    }

    public List<Object> execute(List<Variable> params, StringBuilder s) {
        incrementUse();
        Map<Variable, Variable> requiresEnsures = new HashMap<>();
        Map<Variable, Variable> paramMap = new HashMap<>();
        for (int i = 0, argsSize = args.size(); i < argsSize; i++) {
            Variable arg = args.get(i);
            Variable param = params.get(i);
            paramMap.put(arg, param);
            requiresEnsures.put(arg, param);
        }

        ReturnStatus returnStatus = body.execute(paramMap, s);
        List<Object> execute = new ArrayList<>();
        if (returnStatus != null) {
            execute = returnStatus.getValues();
        }

        if (!getName().startsWith("safe") && !getName().equals("Main")) {
            addRequires(requiresEnsures);
            addEnsures(requiresEnsures, execute);
        }
        return execute;
    }

    private void addEnsures(Map<Variable, Variable> requiresEnsures, List<Object> execute) {
        List<String> clausesRequires = getRequiresClauses(requiresEnsures);
        if (clausesRequires == null) {
            return;
        }

        List<String> clausesEnsures = getEnsuresClauses(execute);

        String res = "";

        if (!clausesEnsures.isEmpty()) {

            if (!clausesRequires.isEmpty()) {
                res = res + "(" + String.join(" && ", clausesRequires) + ") ==> ";
            }

            res = res + "(" + String.join(" && ", clausesEnsures) + ")";
        }

        if (!res.isEmpty()) {
            ensures.add("(" + res + ")");
        }


    }

    private void addRequires(Map<Variable, Variable> requiresEnsures) {
        List<String> clausesRequires = getRequiresClauses(requiresEnsures);
        if (clausesRequires == null) {
            return;
        }

        if (!clausesRequires.isEmpty()) {
            String res = "(" + String.join(" && ", clausesRequires) + ")";
            requires.add(res);
        }
    }

    private List<String> getEnsuresClauses(List<Object> execute) {
        List<String> ensures = new ArrayList<>();

        for (int i = 0, executeSize = execute.size(); i < executeSize; i++) {
            Variable retV = retArgs.get(i);
            Object o = execute.get(i);

            String variable = retV.getName();
            String v = retV.getType().formatEnsures(variable, o);

            ensures.add(v);
        }

        return ensures;
    }

    private List<String> getRequiresClauses(Map<Variable, Variable> requiresEnsures) {
        List<String> clauses = new ArrayList<>();

        for (Map.Entry<Variable, Variable> entry : requiresEnsures.entrySet()) {
            Variable key = entry.getKey();
            Variable value = entry.getValue();

            String variable = key.getName();
            String v = value.getType().formatEnsures(variable, value.getValue(requiresEnsures).get(0));

            if (v == null) {
                return null;
            }

            clauses.add(v);
        }
        return clauses;
    }

    public void executeWithOutput(StringBuilder s) {
        execute(new ArrayList<>(), s);
    }

    public void execute() {
        execute(new ArrayList<>(), new StringBuilder());
    }

    public void addEnsures(String s) {
        ensures.add(s);
    }

    public String minimizedTestCase() {
        return minimizedTestCase(true);
    }

    public String minimizedTestCase(boolean printAll) {
        List<String> code = new ArrayList<>();

        if (printAll) {
            for (DataType d : RandomTypeGenerator.DEFINED_DATA_TYPES) {
                code.add(d.declaration());
            }
            for (TypeAlias t : RandomTypeGenerator.DEFINED_TYPE_ALIAS) {
                code.add(t.declaration());
            }
            for (DClass t : RandomTypeGenerator.DEFINED_DCLASS) {
                code.add(t.declaration());
            }

            List<Method> allMethods = symbolTable.getAllMethods();
            for (Method m : allMethods) {
                if (m.getNoOfUses() > 0) {
                    if (m.getName().startsWith("safe")) {
                        code.add(m.toCode(false));
                    } else {
                        code.add(m.minimizedTestCase(false));
                    }
                }
            }
            List<Function> allFunctions = symbolTable.getAllFunctions();
            for (Function f : allFunctions) {
                if (f.getNoOfUses() > 0) {
                    code.add(f.minimizedTestCase());
                }
            }
        }

        code.add(declarationLine());
        code.add(StringUtils.indent(body.minimizedTestCase()));
        code.add("}\n");
        return String.join("\n", code);
    }

    public Map<String, String> invalidValidationTests() {
        return invalidValidationTests(true);
    }
    public Map<String, String> invalidValidationTests(boolean printAll) {
        Map<String, String> res = new HashMap<>();

        if (printAll) {
            List<Method> allMethods = symbolTable.getAllMethods();
            for (Method m : allMethods) {
                if (m.getNoOfUses() > 0) {
                    res.putAll(m.invalidValidationTests(false));
                }
            }
        }

        res.putAll(body.invalidValidationTests());
        return res;
    }

    public CallMethodExpression generateCall(SymbolTable symbolTable) {
        RandomExpressionGenerator expressionGenerator = new RandomExpressionGenerator();

        List<Type> argTypes = getArgTypes();
        List<Expression> args = new ArrayList<>();
        for (Type t : argTypes) {
            Type concrete = t.concrete(symbolTable);
            Expression exp = expressionGenerator.generateExpression(concrete, symbolTable);
            args.add(exp);
        }

        CallBaseMethodExpression expression = new CallBaseMethodExpression(symbolTable, this, args);
        return expression;
    }
}
