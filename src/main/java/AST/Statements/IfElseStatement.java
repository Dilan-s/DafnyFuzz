package AST.Statements;

import AST.Errors.SemanticException;
import AST.Statements.Expressions.Expression;
import AST.Statements.Expressions.Operator.UnaryOperator;
import AST.Statements.Expressions.OperatorExpression;
import AST.Statements.util.ReturnStatus;
import AST.StringUtils;
import AST.SymbolTable.Identifier;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public boolean couldReturn() {
        return ifStat.couldReturn() || (elseStat.isPresent() && elseStat.get().couldReturn());
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

    @Override
    public ReturnStatus assignReturnIfPossible(Method method, ReturnStatus currStatus, List<Expression> dependencies) {

        Object testV = test.getValue().get(0);


        if (testV != null) {
            Boolean testVB = (Boolean) testV;
            if (testVB && ifStat.couldReturn()) {
                return ifStat.assignReturnIfPossible(method, currStatus, dependencies);
            } else if (!testVB && elseStat.isPresent() && elseStat.get().couldReturn()) {
                return elseStat.get().assignReturnIfPossible(method, currStatus, dependencies);
            }
        } else {
            ReturnStatus rIf = currStatus;
            ReturnStatus rElse = currStatus;
            if (ifStat.couldReturn()) {

                List<Expression> trueDep = new ArrayList<>(dependencies);
                trueDep.add(test);
                rIf = ifStat.assignReturnIfPossible(method, currStatus, trueDep);
            }
            if (elseStat.isPresent() && elseStat.get().couldReturn()) {

                List<Expression> falseDep = new ArrayList<>(dependencies);

                Bool bool = new Bool();
                OperatorExpression op = new OperatorExpression(symbolTable, bool,
                    UnaryOperator.Negate, List.of(test));
                falseDep.add(op);
                rElse = elseStat.get()
                    .assignReturnIfPossible(method, currStatus, falseDep);
            }
            if (rIf == ReturnStatus.ASSIGNED && rElse == ReturnStatus.ASSIGNED) {
                return ReturnStatus.ASSIGNED;
            }
        }
        return currStatus;
    }

    @Override
    public List<Object> execute(Map<Variable, Variable> paramMap) {
        return null;
    }

    @Override
    public List<Statement> expand() {
        List<Statement> r = new ArrayList<>();
        r.addAll(test.expand());
        r.addAll(ifStat.expand());
        r.addAll(elseStat.isPresent() ? elseStat.get().expand() : new ArrayList<>());
        return r;
    }
}
