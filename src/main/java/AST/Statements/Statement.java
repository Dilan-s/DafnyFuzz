package AST.Statements;

import java.util.List;
import java.util.Map;
import java.util.Random;

public interface Statement {

    public String generateCode();

    public String printResult();

    public List<Statement> generateValue(Map<String, Statement> symbolTable);


}
