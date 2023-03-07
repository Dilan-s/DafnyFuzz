package AST.Generator;

import java.util.Random;

public class GeneratorConfig {

    private static Random random;
    public static final double DECAY_FACTOR = 0.95;

    public static void setRandom(Random random) {
        GeneratorConfig.random = random;
    }

    public static Random getRandom() {
        return random;
    }
}
