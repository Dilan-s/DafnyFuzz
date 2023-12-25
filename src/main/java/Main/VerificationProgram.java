package Main;

import AST.Program.DafnyProgram;
import AST.SymbolTable.Method.Method;
import java.io.File;

public class VerificationProgram {

  public static void main(String[] args) {
    DafnyProgram dafnyProgram;
    if (args.length == 1) {
      dafnyProgram = new DafnyProgram(Long.parseLong(args[0]));
    } else {
      dafnyProgram = new DafnyProgram();
    }
    new File("./tests-minimized").mkdirs();
    new File("./tests-incorrect").mkdirs();
    Method main = dafnyProgram.generateProgram();
    dafnyProgram.minimizedTestCase(main);
    dafnyProgram.incorrectValidationTestCase(main);
  }

}
