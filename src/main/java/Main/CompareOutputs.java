package Main;

import ErrorDetector.ReadOutput;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CompareOutputs {

    public static void main(String[] args) {
        ReadOutput readOutput = new ReadOutput("outputs/");
        readOutput.readAllFiles();
        if (readOutput.containsDifference()) {
            System.out.println("ERROR FOUND IN TEST CASE " + args[0]);
            copyTest(args[0], readOutput);
        } else {
            deleteTest(args[0], readOutput);
        }
    }

    private static void deleteTest(String arg, ReadOutput readOutput) {

    }

    private static void copyTest(String arg, ReadOutput readOutput) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("test.dfy"));
            StringBuilder sb = new StringBuilder();
            String st;
            while ((st = bufferedReader.readLine()) != null) {
                sb.append(st);
                sb.append("\n");
            }
            String dir = "errors/" + arg;
            new File(dir).mkdir();
            BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/testCase" + arg, true));
            readOutput.copyFiles(dir);
            writer.append(sb);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
