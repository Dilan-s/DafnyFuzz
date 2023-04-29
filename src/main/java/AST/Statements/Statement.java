package AST.Statements;

import AST.SymbolTable.Types.Variables.Variable;
import java.util.List;
import java.util.Map;

public interface Statement {

    default boolean isReturn() {
        return false;
    }

    List<Object> execute(Map<Variable, Variable> paramMap, StringBuilder s);

    List<Statement> expand();

    List<String> toOutput();

    void incrementUse();

    int getNoOfUses();

    String minimizedTestCase();

    default boolean minimizedReturn() {
        return isReturn();
    }
}
