package AST.SymbolTable.SymbolTable;

import AST.SymbolTable.Method;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SymbolTable {

    private final HashMap<String, Variable> variables;
    private final HashMap<String, Method> methods;
    private SymbolTable enclosingSymbolTable;

    SymbolTable(boolean global) {
        this(null);
    }

    public SymbolTable() {
        this(GlobalSymbolTable.getGlobalSymbolTable());
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

    public List<Variable> getAllVariablesInCurrentScope() {
        return new ArrayList<>(variables.values());
    }

    public List<Variable> getAllVariables(Type t) {
        return getAllVariables(t, true);
    }
    public List<Variable> getAllVariables(Type t, boolean enclosing) {
        List<Variable> vars = variables.values()
            .stream()
            .filter(x -> x.getType().equals(t))
            .collect(Collectors.toList());

        if (enclosing) {
            if (enclosingSymbolTable != null) {
                vars.addAll(enclosingSymbolTable.getAllVariables(t));
            }
        }

        return vars;
    }

    public List<Method> getMethodWithTypes(List<Type> returnTypes) {
        return getAllMethods().stream().filter(m -> sameReturnType(m.getReturnTypes(), returnTypes)).collect(Collectors.toList());
    }

    private boolean sameReturnType(List<Type> methodReturnTypes, List<Type> wantedReturnTypes) {
        if (methodReturnTypes.size() != wantedReturnTypes.size()) {
            return false;
        }

        for (int i = 0; i < methodReturnTypes.size(); i++) {
            Type rt = methodReturnTypes.get(i);
            Type wt = wantedReturnTypes.get(i);
            if (!rt.equals(wt)) {
                return false;
            }
        }
        return true;
    }
}
