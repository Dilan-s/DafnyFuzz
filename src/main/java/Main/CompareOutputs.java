package Main;

import ErrorDetector.ReadOutput;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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
            copyTest(args[0]);
        }
    }

    private static void copyTest(String arg) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("test.dfy"));
            StringBuilder sb = new StringBuilder();
            String st;
            while ((st = bufferedReader.readLine()) != null) {
                sb.append(st);
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter("errors/testCase" + arg, true));
            writer.append(sb);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
