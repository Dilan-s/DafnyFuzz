package AST;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StringUtils {

  public static String indent(String s) {
    return "\t" + s.replace("\n", "\n\t");
  }

  public static List<String> indent(List<String> ss) {
    return ss.stream().map(s -> "\t" + s).collect(Collectors.toList());
  }

  public static String intersperse(String s, List<String> code) {
    return code.stream().filter(x -> !x.isEmpty()).collect(Collectors.joining(s));
  }

  public static String intersperse(String s, Set<String> code) {
    return code.stream().filter(x -> !x.isEmpty()).collect(Collectors.joining(s));
  }
}
