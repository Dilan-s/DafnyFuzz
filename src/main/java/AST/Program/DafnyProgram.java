package AST.Program;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomStatementGenerator;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Void;
import java.util.Random;

public class DafnyProgram {

    private final Random random;

    public DafnyProgram(long seed) {
        random = new Random(seed);
    }

    public DafnyProgram() {
        random = new Random();
    }

    public void generateProgram() {
        GeneratorConfig.setRandom(random);
        RandomStatementGenerator randomStatementGenerator = new RandomStatementGenerator();
        Method main = new Method(new Void(), "Main");

        Method safe_division = SafeMethods.safe_division();
        main.addMethod(safe_division);

        Method safe_modulus = SafeMethods.safe_modulus();
        main.addMethod(safe_modulus);

        Method safe_index_seq = SafeMethods.safe_index_seq();
        main.addMethod(safe_index_seq);

        Method safe_subsequence = SafeMethods.safe_subsequence();
        main.addMethod(safe_subsequence);

        Statement statement = randomStatementGenerator.generateBody(main);
        main.setBody(statement);
        System.out.println(main);
    }

}
