package AST.SymbolTable;

import AST.Generator.VariableNameGenerator;
import AST.Statements.Expressions.Expression;
import java.util.Random;

public interface Type extends Identifier {

    boolean isSameType(Type other);

    Expression generateLiteral(Random random);

    default String getTypeIndicatorString() {
        return String.format(": %s", getName());
    }

    default String getReturnTypeIndicator(String method) {
        String returnParameter = VariableNameGenerator.generateReturnVariableName(method);
        return String.format("%s%s", returnParameter, getTypeIndicatorString());
    }

    boolean operatorExists();
}
