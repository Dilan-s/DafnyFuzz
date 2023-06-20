package Main;

import AST.Program.DafnyProgram;
import AST.SymbolTable.Method;
import java.io.File;

public class ExpectedProgram {

    public static void main(String[] args) {
        DafnyProgram dafnyProgram;
        if (args.length == 1) {
            dafnyProgram = new DafnyProgram(Long.parseLong(args[0]));
        } else {
            dafnyProgram = new DafnyProgram();
        }
        new File("./tests").mkdirs();
        new File("./outputs").mkdirs();
        Method main = dafnyProgram.generateProgram();
        dafnyProgram.expectedOutput(main);
        dafnyProgram.baseTestCase(main);
    }

}
