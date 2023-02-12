package AST.Statements.Type;

import java.util.Random;

public class TypeGenerator {

    public static Type generateType(Type...types) {
        PrimitiveTypes[] primitiveTypes = PrimitiveTypes.values();
        Random random = new Random();
        int i = random.nextInt(primitiveTypes.length + types.length);
        if (i < primitiveTypes.length) {
            return primitiveTypes[i];
        }
        return types[i - primitiveTypes.length];
    }
}
