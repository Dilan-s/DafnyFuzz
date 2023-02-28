package AST;

import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {

    public static List<String> indent(List<String> ss) {
        return ss.stream().map(s -> "\t" + s).collect(Collectors.toList());
    }

}
