package Main;

import AST.Program.DafnyProgram;
import AST.SymbolTable.Method;
import java.util.Random;

public class GenerateProgram {

    public static void main(String[] args) {
        DafnyProgram dafnyProgram;
        if (args.length == 1) {
            dafnyProgram = new DafnyProgram(Long.parseLong(args[0]));
        } else {
            dafnyProgram = new DafnyProgram();
        }
        Method main = dafnyProgram.generateProgram();
        dafnyProgram.expectedOutput(main);
        dafnyProgram.EMIProgramGeneration(main);
        dafnyProgram.baseTestCase(main);
        dafnyProgram.minimizedTestCase(main);
        dafnyProgram.incorrectValidationTestCase(main);
    }
}
