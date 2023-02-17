package AST.Statements;

import java.util.List;
import java.util.Map;
import java.util.Random;

public interface Statement {

    public List<String> generateCode();

    public List<Statement> generateValue(Map<String, Statement> symbolTable);


}
