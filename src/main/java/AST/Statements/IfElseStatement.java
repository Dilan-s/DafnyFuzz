package AST.Statements;

import AST.Errors.SemanticException;
import AST.Statements.Expressions.Expression;
import AST.Statements.util.PrintAll;
import AST.StringUtils;
import AST.SymbolTable.Identifier;
import AST.SymbolTable.Method;
import AST.SymbolTable.PrimitiveTypes.Bool;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class IfElseStatement implements Statement {

    private final SymbolTable symbolTable;
    private Expression test;
    private Statement ifStat;
    private Optional<Statement> elseStat;

    public IfElseStatement(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.elseStat = Optional.empty();
    }

    public void setTest(Expression test) {
        this.test = test;
    }

    public void setIfStat(Statement ifStat) {
        this.ifStat = ifStat;
    }

    public void setElseStat(Statement elseStat) {
        this.elseStat = Optional.of(elseStat);
    }

    @Override
    public boolean isReturn() {
        return ifStat.isReturn() && elseStat.isPresent() && elseStat.get().isReturn();
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

        if (!testType.isSameType(new Bool())) {
            throw new SemanticException(String.format(
                "Test condition expected to be a bool but actually is %s", testType.getName()));
        }
        test.semanticCheck(method);
        ifStat.semanticCheck(method);
        if (elseStat.isPresent()) {
            elseStat.get().semanticCheck(method);
        }
    }

    @Override
    public List<String> toCode() {
        List<String> code = new ArrayList<>();
        code.addAll(test.toCode());

        code.add(String.format("if %s {\n", test));
        code.addAll(StringUtils.indent(ifStat.toCode()));

        if (elseStat.isPresent()) {
            code.add("} else {\n");
            code.addAll(StringUtils.indent(elseStat.get().toCode()));
        }

        code.add("}\n");
        return code;
    }
}
