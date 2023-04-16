package Main;

import ErrorDetector.ReadOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
