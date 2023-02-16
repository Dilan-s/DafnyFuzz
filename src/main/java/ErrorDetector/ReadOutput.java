package ErrorDetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadOutput {

    private final File directory;
    private final String directoryPath;
    private final Map<String, List<String>> outputs;

    public ReadOutput(String directory) {
        this.directory = new File(directory);
        directoryPath = directory;
        outputs = new HashMap<>();
    }

    public void readAllFiles() {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                readFile(file);
            }
        }
    }

    private void readFile(File file) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String st;
            while ((st = bufferedReader.readLine()) != null) {
                sb.append(st);
            }
            List<String> files = outputs.getOrDefault(sb.toString(), new ArrayList<>());
            files.add(file.getName());
            outputs.put(sb.toString(), files);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean containsDifference() {
        return outputs.keySet().size() > 1;
    }
}
