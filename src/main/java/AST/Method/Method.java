package AST.Method;

import AST.StringUtils.Constants;
import AST.Statements.Statement;
import AST.Statements.Type.PrimitiveTypes;
import AST.Statements.Type.Type;
import AST.Statements.Type.TypeGenerator;
import AST.StringUtils.IndentationLevelException;
import AST.StringUtils.IndentedStringBuilder;
import AST.Variables.VariableAssigner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Method {

    private final String name;
    private final Type returnType;
    private final List<Type> args;
    private final List<Statement> body;
    private final Map<String, Statement> variables;

    private Method(String name, Type returnType, List<Type> args) {
        this.name = name;
        this.returnType = returnType;
        this.args = args;

        this.body = new ArrayList<>();
        this.variables = new HashMap<>();
    }

    public Method() {
        this(VariableAssigner.generateMethodName(), TypeGenerator.generateType(), Collections.emptyList());
    }

    public static Method getMain() {
        return new Method("Main", PrimitiveTypes.VOID, Collections.emptyList());
    }

    public Map<String, Statement> getSymbolTable() {
        return variables;
    }

    public void addStatement(Statement statement) {
        this.body.add(statement);
    }

    public void addStatement(List<Statement> statements) {
        this.body.addAll(statements);
    }

    public String generateCode() throws IndentationLevelException {
        IndentedStringBuilder code = new IndentedStringBuilder();
        code.append("method ");
        code.append(name);
        code.append(Constants.OPENING_ARGS);
        code.append(args.stream().map(Type::toString).collect(Collectors.joining(", ")));
        code.append(Constants.CLOSING_ARGS);
        code.append(returnType.getTypeIndicatorString());
        code.append(Constants.OPENING_SCOPE);
        code.indent();
        body.forEach(statement -> {
            code.append(statement.generateCode());
            code.append(statement.printResult());
        });
        code.unindent();
        code.append(Constants.CLOSING_SCOPE);
        return code.toString();
    }
}
