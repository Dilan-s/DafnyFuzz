package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.Statements.Statement;
import AST.SymbolTable.Identifier;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    public void semanticCheck(Method method) throws SemanticException {
        List<Type> testTypes = test.getTypes();

        if (testTypes.size() != 1) {
            throw new SemanticException(String.format("Test condition has multiple values: %s",
                testTypes.stream().map(Identifier::getName).collect(
                    Collectors.joining(", "))));
        }

        Type testType = testTypes.get(0);
        if (!testType.equals(new Bool())) {
            throw new SemanticException(String.format(
                "Test condition expected to be a bool but actually is %s", testType.getName()));
        }
        test.semanticCheck(method);

        List<Type> ifTypes = ifExp.getTypes();
        List<Type> elseTypes = elseExp.getTypes();

        if (ifTypes.size() != elseTypes.size()) {
            throw new SemanticException(String.format(
                "Expected same number of expressions in if and else, but actually got (%d, %d)",
                ifTypes.size(), elseTypes.size()));
        }

        for (int i = 0; i < ifTypes.size(); i++) {
            Type ifType = ifTypes.get(i);
            Type elseType = elseTypes.get(i);

            if (!ifType.equals(elseType)) {
                throw new SemanticException(
                    String.format("Expected %dth argument to be %s but actually go %s", i,
                        ifType.getName(), elseType.getName()));
            }
        }

        ifExp.semanticCheck(method);
        elseExp.semanticCheck(method);
    }

    @Override
    public String toString() {
        return String.format("(if (%s) then (%s) else (%s))", test, ifExp, elseExp);
    }

    @Override
    public boolean isValidReturn() {
        return ifExp.isValidReturn() && elseExp.isValidReturn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(test, ifExp, elseExp);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IfElseExpression)) {
            return false;
        }
        IfElseExpression other = (IfElseExpression) obj;
        return other.test.equals(test) && other.ifExp.equals(ifExp) && other.elseExp.equals(elseExp);
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
