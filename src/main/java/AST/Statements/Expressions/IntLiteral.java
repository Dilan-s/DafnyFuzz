package AST.Statements.Expressions;

import AST.Errors.SemanticException;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class IntLiteral implements Expression {

    private final int value;
    private final Type type;
    private SymbolTable symbolTable;

    public IntLiteral(Type type, SymbolTable symbolTable, int value) {
        this.type = type;
        this.symbolTable = symbolTable;
        this.value = value;
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    @Override
    public void semanticCheck(Method method) throws SemanticException {
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public List<String> toOutput() {
        Set<String> res = new HashSet<>();
        res.add(String.valueOf(value));
        if (value > 0) {
            res.add(String.format("0x%X", value));
        }
        return new ArrayList<>(res);
    }

    @Override
    public List<Statement> expand() {
        return new ArrayList<>();
    }

    @Override
    public List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s) {
        List<Object> r = new ArrayList<>();
        r.add(value);
        return r;
    }
}
