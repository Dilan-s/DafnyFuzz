package AST.Statements;


import AST.Statements.Assignments.BoolAssignment;
import AST.Statements.Assignments.IntAssignment;
import AST.Statements.Type.PrimitiveTypes;
import AST.Statements.Type.Type;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomStatementGenerator implements StatementGenerator {
    private final Random random;

    public RandomStatementGenerator(Random random) {
        this.random = random;
    }

    public RandomStatementGenerator(long seed) {
        this(new Random(seed));
    }

    public RandomStatementGenerator() {
        this(new Random());
    }

    @Override
    public Statement generateStatement() {
        return null;
    }

    @Override
    public Statement endProgram() {
        return null;
    }

    @Override
    public List<Statement> generateIntStatement(Map<String, Statement> symbolTable) {
        IntAssignment intAssignment = new IntAssignment(random);
        List<Statement> statements = intAssignment.generateValue(symbolTable);
        symbolTable.put(intAssignment.getVariableName(), intAssignment);
        statements.add(intAssignment);
        return statements;
    }

    @Override
    public List<Statement> generateBoolStatement(Map<String, Statement> symbolTable) {
        BoolAssignment boolAssignment = new BoolAssignment(random);
        List<Statement> statements = boolAssignment.generateValue(symbolTable);
        symbolTable.put(boolAssignment.getVariableName(), boolAssignment);
        statements.add(boolAssignment);
        return statements;
    }

    @Override
    public StatementType getNextStatementType() {
        StatementType[] types = StatementType.values();
        int i = random.nextInt(types.length);
        return types[i];
    }
}
