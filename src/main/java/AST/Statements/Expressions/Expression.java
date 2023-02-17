package AST.Statements.Expressions;

import AST.Statements.Statement;
import java.util.List;

public abstract class Expression<T> {

    public abstract List<Statement> getStatements();

    public abstract T getValue();
}
