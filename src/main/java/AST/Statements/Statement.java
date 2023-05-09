package AST.Statements;

import AST.Statements.util.ReturnStatus;
import AST.SymbolTable.Types.Variables.Variable;
import java.util.List;
import java.util.Map;

public interface Statement {

    default boolean isReturn() {
        return false;
    }

    ReturnStatus execute(Map<Variable, Variable> paramMap, StringBuilder s);

    List<Statement> expand();

    List<String> toOutput();

    void incrementUse();

    int getNoOfUses();

    String minimizedTestCase();

    default boolean minimizedReturn() {
        return isReturn();
    }

    default boolean requireUpdate() {
        return false;
    }
}
