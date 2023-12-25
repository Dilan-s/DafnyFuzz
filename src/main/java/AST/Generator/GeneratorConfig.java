package AST.Generator;

import java.util.Random;

public class GeneratorConfig {

  public static final double SWARM_MULTIPLIER_LARGE = 250;
  public static final double SWARM_MULTIPLIER_SMALL = 10;
  public static final double OPTION_DECAY_FACTOR = 0.95;
  public static final double CONTINUE_DECAY_FACTOR = 0.95;
  private static Random random;

  public static Random getRandom() {
    return random;
  }

  public static void setRandom(Random random) {
    GeneratorConfig.random = random;
  }
}
