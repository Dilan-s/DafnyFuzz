package Main;

import AST.Program.DafnyProgram;
import java.util.Random;

public class GenerateProgram {

    public static void main(String[] args) {
        DafnyProgram dafnyProgram = new DafnyProgram(Long.parseLong(args[0]));
        dafnyProgram.generateProgram();
    }

}
