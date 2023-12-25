package Main;

import ErrorDetector.ReadOutput;

public class CompareOutputs {

  public static void main(String[] args) {
    ReadOutput readOutput = new ReadOutput(args[1]);
    readOutput.readAllFiles();
    if (readOutput.containsDifference()) {
      System.out.println("ERROR FOUND IN TEST CASE " + args[0]);
      System.exit(1);
    } else {
      System.exit(0);
    }
  }
}
