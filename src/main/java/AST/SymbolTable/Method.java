package AST.SymbolTable;

import AST.Generator.VariableNameGenerator;
import AST.Statements.Expressions.Expression;
import AST.Statements.Statement;
import AST.Statements.util.ReturnStatus;
import AST.StringUtils;
import AST.SymbolTable.Types.PrimitiveTypes.Void;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Method implements Identifier {

    private final String name;
    private final List<Type> returnTypes;
    private final List<Variable> args;
    private final SymbolTable symbolTable;
    private Statement body;
    private List<PotentialValue> returnValues;

    public Method(List<Type> returnTypes, String name, SymbolTable symbolTable, List<Variable> args, List<PotentialValue> returnValues) {
        this.returnTypes = returnTypes;
        this.name = name;
        this.symbolTable = symbolTable;
        this.args = args;
        this.returnValues = returnValues;
    }

    public Method(List<Type> returnTypes, String name, SymbolTable symbolTable) {
        this(returnTypes, name, symbolTable, new ArrayList<>(), new ArrayList<>());
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
        symbolTable.addVariable(argument);
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
    }

    @Override
    public String getName() {
        return name;
    }

    public List<String> toCode() {
        return toCode(true);
    }

    public List<String> toCode(boolean printMethods) {
        List<String> code = new ArrayList<>();

        if (printMethods) {
            List<Method> allMethods = symbolTable.getAllMethods();
            for (Method m : allMethods) {
                code.addAll(m.toCode(false));
            }
        }

        code.add(declarationLine());
        code.add(StringUtils.indent(body.toString()));
        code.add("}\n");
        return code;
    }

    public String declarationLine() {
        String arguments = getArgs().stream()
            .map(Variable::toString)
            .collect(Collectors.joining(", "));
        String types = getReturnTypes().stream()
            .filter(x -> !x.equals(new Void()))
            .map(Type::getVariableType)
            .map(x -> String.format("%s: %s", VariableNameGenerator.generateReturnVariableName(getName()), x))
            .collect(Collectors.joining(", "));
        return String.format("method %s(%s) returns (%s) { ", getName(), arguments, types);
    }

    public void addMethod(Method method) {
        symbolTable.addMethod(method);
    }

    @Override
    public String toString() {
        List<String> code = toCode(true);
        return String.join("\n",code);
    }

    public Method getSimpleMethod() {
        return this;
    }

    public void assignReturn() {
        if (hasReturn()) {
            body.assignReturnIfPossible(this, ReturnStatus.UNASSIGNED, new ArrayList<>());
        }
    }

    public void setReturnValues(List<Expression> expression, List<Expression> dependencies) {
        this.returnValues.add(new PotentialValue(expression, dependencies));
    }

    public List<Object> execute(List<Variable> params) {
        Map<Variable, Variable> paramMap = new HashMap<>();
        for (int i = 0, argsSize = args.size(); i < argsSize; i++) {
            Variable arg = args.get(i);
            Variable param = params.get(i);
            paramMap.put(arg, param);
        }
        return body.execute(paramMap);
    }


    private static class PotentialValue {
        List<Expression> expression;
        List<Expression> dependencies;

        public PotentialValue(List<Expression> expression, List<Expression> dependencies) {
            this.expression = expression;
            this.dependencies = dependencies;
        }
    }
}
