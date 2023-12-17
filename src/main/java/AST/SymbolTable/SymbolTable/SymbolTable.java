package AST.SymbolTable.SymbolTable;

import AST.SymbolTable.Function;
import AST.SymbolTable.Method.ClassMethod;
import AST.SymbolTable.Method.Method;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SymbolTable {

    private final HashMap<String, Variable> variables;
    private final HashMap<String, Method> methods;
    private final HashMap<String, ClassMethod> classMethods;
    private final HashMap<String, Function> functions;
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
        this.classMethods = new HashMap<>();
        this.functions = new HashMap<>();
    }

    public void setEnclosingSymbolTable(SymbolTable enclosingSymbolTable) {
        this.enclosingSymbolTable = enclosingSymbolTable;
    }

    public void addVariable(Variable variable) {
        variables.put(variable.getName(), variable);
    }

    public void addClassMethod(ClassMethod method) {
        if (enclosingSymbolTable == null) {
            classMethods.put(method.getName(), method);
        } else {
            enclosingSymbolTable.addClassMethod(method);
        }
    }

    public void addMethod(Method method) {
        if (enclosingSymbolTable == null) {
            methods.put(method.getName(), method);
        } else {
            enclosingSymbolTable.addMethod(method);
        }

    }

    public void addFunction(Function function) {
        if (enclosingSymbolTable == null) {
            functions.put(function.getName(), function);
        } else {
            enclosingSymbolTable.addFunction(function);
        }
    }

    public Variable getVariable(Variable variable) {
        return variables.getOrDefault(variable.getName(),
            enclosingSymbolTable == null ? null : enclosingSymbolTable.getVariable(variable));
    }

    public Method getMethod(String methodName) {
        Method method = methods.getOrDefault(methodName, null);
        if (method != null) {
            return method;
        }

        ClassMethod classMethod = classMethods.getOrDefault(methodName, null);
        if (classMethod != null) {
            return classMethod;
        }

        return enclosingSymbolTable != null
            ? enclosingSymbolTable.getMethod(methodName)
            : null;


    }

    public List<Function> getAllFunctions() {
        List<Function> fs = new ArrayList<>(functions.values());
        if (enclosingSymbolTable != null) {
            fs.addAll(enclosingSymbolTable.getAllFunctions());
        }
        return fs;
    }

    public List<Method> getAllBaseMethods() {
        List<Method> ms = new ArrayList<>(methods.values());

        if (enclosingSymbolTable != null) {
            ms.addAll(enclosingSymbolTable.getAllBaseMethods());
        }
        return ms;
    }

    public List<Method> getAllMethods() {
        List<Method> ms = new ArrayList<>(methods.values());
        ms.addAll(classMethods.values());
        if (enclosingSymbolTable != null) {
            ms.addAll(enclosingSymbolTable.getAllMethods());
        }
        return ms;
    }

    public List<Variable> getAllVariablesInCurrentScope() {
        List<Variable> currST = new ArrayList<>(this.variables.values());
        if (enclosingSymbolTable != GlobalSymbolTable.getGlobalSymbolTable()) {
            currST.addAll(enclosingSymbolTable.getAllVariablesInCurrentScope());
        }
        return currST;
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

    public List<Function> getFunctionWithType(Type returnType) {
        return getAllFunctions().stream()
            .filter(f -> f.getReturnType().equals(returnType))
            .collect(Collectors.toList());
    }

    public List<Method> getMethodWithTypes(List<Type> returnTypes) {
        return getAllMethods().stream()
            .filter(m -> sameReturnType(m.getReturnTypes(), returnTypes))
            .collect(Collectors.toList());
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

    public SymbolTable getEnclosingSymbolTable() {
        return enclosingSymbolTable;
    }

    public boolean variableInScope(Variable v) {
        return getAllVariablesInCurrentScope().contains(v);
    }

    public void replaceVariables(List<Variable> remove, List<Variable> replace) {
        if (variables.keySet().containsAll(remove.stream().map(Variable::getName).collect(Collectors.toList()))) {
            for (Variable v : remove) {
                variables.remove(v.getName(), v);
            }
            for (Variable v : replace) {
                addVariable(v);
            }
        } else if (enclosingSymbolTable != null) {
            enclosingSymbolTable.replaceVariables(remove, replace);
        }
    }
}
