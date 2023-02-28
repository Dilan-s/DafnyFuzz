package AST.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class SymbolTable {

    private final HashMap<String, Variable> variables;
    private final HashMap<String, Method> methods;
    private SymbolTable enclosingSymbolTable;


    public SymbolTable() {
        this(null);
    }

    public SymbolTable(SymbolTable enclosingSymbolTable) {
        this.enclosingSymbolTable = enclosingSymbolTable;
        this.variables = new HashMap<>();
        this.methods = new HashMap<>();
    }

    public void setEnclosingSymbolTable(SymbolTable enclosingSymbolTable) {
        this.enclosingSymbolTable = enclosingSymbolTable;
    }

    public void addVariable(Variable variable) {
        variables.put(variable.getName(), variable);
    }

    public void addMethod(Method method) {
        if (enclosingSymbolTable == null) {
            methods.put(method.getName(), method);
        } else {
            enclosingSymbolTable.addMethod(method);
        }

    }

    public Variable getVariable(Variable variable) {
        return variables.getOrDefault(variable.getName(), null);
    }

    public Method getMethod(String methodName) {
        Method method = methods.getOrDefault(methodName, null);

        return method == null && enclosingSymbolTable != null
            ? enclosingSymbolTable.getMethod(methodName)
            : method;
    }

    public List<Method> getAllMethods() {
        List<Method> ms = new ArrayList<>(methods.values());

        if (enclosingSymbolTable != null) {
            ms.addAll(enclosingSymbolTable.getAllMethods());
        }
        return ms;
    }

    public List<Variable> getAllVariables(Type t) {
        List<Variable> vars = variables.values().stream()
            .filter(x -> x.getType().isSameType(t)).collect(Collectors.toList());

        if (enclosingSymbolTable != null) {
            vars.addAll(enclosingSymbolTable.getAllVariables(t));
        }

        return vars;
    }
}
