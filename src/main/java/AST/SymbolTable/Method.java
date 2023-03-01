package AST.SymbolTable;

import AST.Statements.Statement;
import AST.StringUtils;
import AST.SymbolTable.PrimitiveTypes.Void;
import AST.SymbolTable.SymbolTable.SymbolTable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Method implements Identifier {

    private final String name;
    private final List<Type> returnTypes;
    private final List<Variable> args;
    private final SymbolTable symbolTable;
    private List<Statement> body;

    public Method(List<Type> returnTypes, String name, SymbolTable symbolTable, List<Variable> args) {
        this.returnTypes = returnTypes;
        this.name = name;
        this.symbolTable = symbolTable;
        this.args = args;
        this.body = new ArrayList<>();
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
    public void addArgument(String name, Type type) {
        addArgument(new Variable(name, type));
    }

    public void addArgument(Variable argument) {
        args.add(argument);
        symbolTable.addVariable(argument);
    }

    public List<Type> getReturnTypes() {
        return returnTypes;
    }

    public boolean hasReturn() {
        return (returnTypes.size() == 1 && !returnTypes.get(0).isSameType(new Void())) || returnTypes.size() > 1;
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
        this.body.add(body);
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
        for (Statement s : body) {
            code.addAll(StringUtils.indent(s.toCode()));
        }
        code.add("}\n");
        return code;
    }

    public String declarationLine() {
        String arguments = getArgs().stream()
            .map(Variable::toString)
            .collect(Collectors.joining(", "));
        String types = getReturnTypes().stream()
            .map(x -> x.getReturnTypeIndicator(getName()))
            .collect(Collectors.joining(", "));
        return String.format("method %s(%s) returns (%s) { \n", getName(), arguments, types);
    }

    public void addMethod(Method method) {
        symbolTable.addMethod(method);
    }

    @Override
    public String toString() {
        List<String> code = toCode(true);
        return code.stream().collect(Collectors.joining(""));
    }
}
