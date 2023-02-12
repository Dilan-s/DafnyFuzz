package Main;

import AST.Method.Method;
import AST.Program.DafnyProgram;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        DafnyProgram dafnyProgram = new DafnyProgram(1);
        dafnyProgram.generateProgram();

    }

}
