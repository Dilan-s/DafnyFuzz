package AST.Program;

import AST.Generator.GeneratorConfig;
import AST.Generator.RandomStatementGenerator;
import AST.Statements.AssignmentStatement;
import AST.Statements.Statement;
import AST.SymbolTable.Method;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.PrimitiveTypes.Void;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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

        Statement statement = randomStatementGenerator.generateBody(main, main.getSymbolTable());
        main.setBody(statement);

        StringBuilder s = new StringBuilder();
        main.executeWithOutput(s);
        try {
            Path path = Paths.get("./outputs");
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            FileWriter p = new FileWriter(String.format("%s/expected.txt", path.toAbsolutePath()));
            p.write(s.toString());
            p.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> programOptions = main.toOutput();

        try {
            Path path = Paths.get("./tests");
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            for (int i = 0; i < programOptions.size(); i++) {
                FileWriter p = new FileWriter(String.format("%s/test%d.dfy", path.toAbsolutePath(), i));
                p.write(programOptions.get(i));
                p.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
