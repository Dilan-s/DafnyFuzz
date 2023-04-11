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
        ReadOutput readOutput = new ReadOutput("outputs/");
        readOutput.readAllFiles();
        if (readOutput.containsDifference()) {
            System.out.println("ERROR FOUND IN TEST CASE " + args[0]);
            copyTest(readOutput, "tests/", "errors/" + args[0]);
            copyTest(readOutput, "outputs/", "errors/" + args[0]);
        } else {
            deleteTest(args[0], readOutput);
        }
    }

    private static void deleteTest(String arg, ReadOutput readOutput) {

    }

    private static void copyTest(ReadOutput readOutput, String s, String d) {
        try {
            File source = new File(s);
            File dest = new File(d);
            dest.mkdir();

            for (String f : source.list()) {
                File sourceFile = new File(source, f);
                File destinationFile = new File(dest, f);
                InputStream in = new FileInputStream(sourceFile);
                OutputStream out = new FileOutputStream(destinationFile);
                byte[] buf = new byte[1024];
                int length;
                while ((length = in.read(buf)) > 0) {
                    out.write(buf, 0, length);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
