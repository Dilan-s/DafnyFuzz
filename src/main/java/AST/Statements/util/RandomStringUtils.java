package AST.Statements.util;

import AST.Generator.GeneratorConfig;

public class RandomStringUtils {

  public static String options = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

  public static String generateRandomString(int length) {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < length; i++) {
      s.append(options.charAt(GeneratorConfig.getRandom().nextInt(options.length())));
    }
    return s.toString();
  }

}
