package AST.Expressions.DSeq;

import AST.Expressions.BaseExpression;
import AST.Expressions.Expression;
import AST.Statements.Statement;
import AST.SymbolTable.Function;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.DCollectionTypes.Seq;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SeqFuncLiteral extends BaseExpression {

    private final SymbolTable symbolTable;
    private final Type type;
    private final int length;
    private final Function f;

    public SeqFuncLiteral(SymbolTable symbolTable, Type type, int length, Function f) {
        this.symbolTable = symbolTable;
        this.type = type;
        this.length = length;
        this.f = f;
        f.incrementUse();
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    @Override
    public String toString() {
        return String.format("seq(%d, i => %s(i))", length, f.getName());
    }

    @Override
    public List<String> toOutput() {
        return List.of(String.format("seq(%d, i => %s(i))", length, f.getName()), String.format("seq(%d, %s)", length, f.getName()));
    }

    @Override
    protected List<Object> getValue(Map<Variable, Variable> paramMap, StringBuilder s, boolean unused) {
        List<Object> r = new ArrayList<>();
        List<Object> l = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Variable arg = new Variable("TEMP", new Int());
            arg.setValue(symbolTable, paramMap, i);

            List<Object> value = f.execute(List.of(arg), s);
            for (Object v : value) {
                if (v == null) {
                    r.add(null);
                    return r;
                }
                l.add(v);
            }
        }
        r.add(l);
        return r;
    }

    @Override
    public List<Statement> expand() {
        return new ArrayList<>();
    }
}
