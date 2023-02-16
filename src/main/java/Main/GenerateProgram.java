package Main;

import AST.Method.Method;
import AST.Program.DafnyProgram;
import java.util.Random;

public class GenerateProgram {

    public static void main(String[] args) {
        DafnyProgram dafnyProgram = new DafnyProgram();
        dafnyProgram.generateProgram();

    }

}
