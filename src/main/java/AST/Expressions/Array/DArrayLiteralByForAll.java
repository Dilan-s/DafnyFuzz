package AST.Expressions.Array;

import AST.Expressions.BaseExpression;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.ForAllStatement;
import AST.Statements.Statement;
import AST.SymbolTable.Function.Function;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import AST.SymbolTable.Types.Variables.VariableArrayIndex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DArrayLiteralByForAll extends BaseExpression {

    private SymbolTable symbolTable;
    private Type type;
    private int length;
    private Function func;
    private Variable variable;
    private List<List<Statement>> expanded;
    private Statement arrStatAssign;
    private Statement forAllAssign;

    public DArrayLiteralByForAll(SymbolTable symbolTable, Type type, int length,
        Function func) {
        super();
        this.symbolTable = symbolTable;
        this.type = type;
        this.length = length;
        this.func = func;
        func.incrementUse();

        DCollection t = type.asDCollection();
        Type innerType = t.getInnerType();
        this.variable = new Variable(VariableNameGenerator.generateVariableValueName(type, symbolTable), type);
        this.variable.setConstant();
        this.arrStatAssign = new AssignmentStatement(symbolTable, List.of(variable), new ArrayInitValues());
        this.forAllAssign = new ForAllStatement(symbolTable, innerType, length, func, variable);

        this.expanded = new ArrayList<>();
        expanded.add(arrStatAssign.expand());
        expanded.add(forAllAssign.expand());
        generateAssignments();
    }

    private void generateAssignments() {
        DCollection t = this.type.asDCollection();
        Type valType = t.getInnerType();

        for (int i = 0; i < length; i++) {
            VariableArrayIndex v = new VariableArrayIndex(variable, valType, i);
            v.setDeclared();
            symbolTable.addVariable(v);
        }
    }

    @Override
    public boolean requireUpdate() {
        return arrStatAssign.requireUpdate();
    }

    @Override
    public String toString() {
        return variable.getName();
    }

    @Override
    protected List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s,
        boolean unused) {
        return variable.getValue(paramsMap);
    }

    @Override
    public boolean validForFunction() {
        return true;
    }


    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    @Override
    public List<Statement> expand() {
        if (arrStatAssign.requireUpdate()) {
            expanded.set(0, arrStatAssign.expand());
        }
        if (forAllAssign.requireUpdate()) {
            expanded.set(1, forAllAssign.expand());
        }
        return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    private class ArrayInitValues extends BaseExpression {

        public ArrayInitValues() {
            super();
        }

        @Override
        public List<Type> getTypes() {
            return List.of(type);
        }

        @Override
        public List<Statement> expand() {
            return new ArrayList<>();
        }

        @Override
        public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s,
            boolean unused) {
            List<Object> r = new ArrayList<>();

            List<Object> l = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                l.add(null);
            }
            r.add(new ArrayValue(variable, l));
            return r;
        }

        @Override
        public String toString() {
            DCollection t = type.asDCollection();
            return String.format("new %s[%d]", t.getInnerType().getVariableType(),
                length, func.getName());
        }

        @Override
        public boolean requireUpdate() {
            return false;
        }

        @Override
        public boolean validForFunction() {
            return true;
        }
    }
}
