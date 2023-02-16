package AST.Program;

import AST.Method.Method;
import AST.Statements.RandomStatementGenerator;
import AST.Statements.StatementGenerator;
import AST.Statements.StatementType;
import AST.StringUtils.IndentationLevelException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DafnyProgram {

    private final List<Method> methods;
    private final Method main;
    private final int seed;

    public DafnyProgram(int seed) {
        this.seed = seed;
        this.methods = new ArrayList<>();
        this.main = Method.getMain();
    }

    public DafnyProgram() {
        this(new Random().nextInt());
    }

    public void generateProgram() {
        RandomStatementGenerator randomStatementGenerator = new RandomStatementGenerator(seed);
        for (int i = 0; i < 10; i++) {
            StatementType type = randomStatementGenerator.getNextStatementType();
            addStatementToMethod(type, randomStatementGenerator, main);
        }
        try {

            String program = main.generateCode();
//            BufferedWriter writer = new BufferedWriter(new FileWriter("test.dfy"));
//            writer.append(program);
//            writer.close();
            System.out.println(program);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addStatementToMethod(StatementType type, StatementGenerator statementGenerator, Method method) {
        switch (type) {
            case BOOL_ASSIGNMENT:
                method.addStatement(statementGenerator.generateBoolStatement(method.getSymbolTable()));
                break;
            case INT_ASSIGNMENT:
                method.addStatement(statementGenerator.generateBoolStatement(method.getSymbolTable()));
//                method.addStatement(statementGenerator.generateIntStatement(method.getSymbolTable()));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }
}
