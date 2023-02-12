package AST.Statements;

import AST.Statements.Type.Type;
import java.util.List;
import java.util.Map;

public interface StatementGenerator {

    Statement generateStatement();

    Statement endProgram();

    List<Statement> generateIntStatement(Map<String, Statement> symbolTable);

    List<Statement> generateBoolStatement(Map<String, Statement> symbolTable);

    StatementType getNextStatementType();
}
