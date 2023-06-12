package Main;

import AST.Program.DafnyProgram;
import AST.SymbolTable.Method;
import java.io.File;

public class MetamorphicProgramGeneration {

    public static void main(String[] args) {
        DafnyProgram dafnyProgram;
        if (args.length == 1) {
            dafnyProgram = new DafnyProgram(Long.parseLong(args[0]));
        } else {
            dafnyProgram = new DafnyProgram();
        }
        new File("./tests").mkdirs();
        Method main = dafnyProgram.generateProgram();
        dafnyProgram.EMIProgramGeneration(main);
    }

}
