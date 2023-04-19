package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.Generator.GeneratorConfig;
import AST.Statements.Statement;
import AST.SymbolTable.Identifier;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class IfElseExpression implements Expression {

    private Type type;
    private final Expression test;
    private final Expression ifExp;
    private final Expression elseExp;
    private SymbolTable symbolTable;

    public IfElseExpression(SymbolTable symbolTable, Type type, Expression test, Expression ifExp, Expression elseExp) {
        this.symbolTable = symbolTable;
        this.type = type;
        this.test = test;
        this.ifExp = ifExp;
        this.elseExp = elseExp;
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    @Override
    public String toString() {
        return String.format("(if (%s) then (%s) else (%s))", test, ifExp, elseExp);
    }

    @Override
    public List<String> toOutput() {
        Set<String> res = new HashSet<>();
        List<String> temp = new ArrayList<>();

        res.add("(if (");

        List<String> testOptions = test.toOutput();
        temp = new ArrayList<>();
        for (String f : res) {
            for (String testOption : testOptions) {
                String curr = f + testOption;
                temp.add(curr);
            }
        }
        if (testOptions.isEmpty()) {
            temp.addAll(res);
        }
        res = new HashSet(temp);

        temp = new ArrayList<>();
        for (String f : res) {
            temp.add(f + ") then (");
        }
        res = new HashSet(temp);

        List<String> ifOptions = ifExp.toOutput();
        temp = new ArrayList<>();
        for (String f : res) {
            for (String ifOption : ifOptions) {
                String curr = f + ifOption;
                temp.add(curr);
            }
        }
        if (ifOptions.isEmpty()) {
            temp.addAll(res);
        }
        res = new HashSet(temp);

        temp = new ArrayList<>();
        for (String f : res) {
            temp.add(f + ") else (");
        }
        res = new HashSet(temp);

        List<String> elseOptions = elseExp.toOutput();
        temp = new ArrayList<>();
        for (String f : res) {
            for (String testOption : elseOptions) {
                String curr = f + testOption;
                temp.add(curr);
            }
        }
        if (elseOptions.isEmpty()) {
            temp.addAll(res);
        }
        res = new HashSet(temp);

        temp = new ArrayList<>();
        for (String f : res) {
            temp.add(f + "))");
        }
        res = new HashSet(temp);

        List<String> r = new ArrayList<>(res);
        Collections.shuffle(r, GeneratorConfig.getRandom());
        return r.subList(0, Math.min(5, res.size()));
    }

    @Override
    public boolean isValidReturn() {
        return ifExp.isValidReturn() && elseExp.isValidReturn();
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s) {
        List<Object> r = new ArrayList<>();

        Object testValue = test.getValue(paramsMap, s).get(0);

        if (testValue != null) {
            Boolean testB = (Boolean) testValue;
            if (testB) {
                return ifExp.getValue(paramsMap, s);
            } else {
                return elseExp.getValue(paramsMap, s);
            }
        }
        r.add(null);
        return r;
    }

    @Override
    public List<Statement> expand() {
        List<Statement> r = new ArrayList<>();
        r.addAll(test.expand());
        r.addAll(ifExp.expand());
        r.addAll(elseExp.expand());
        return r;
    }
}
