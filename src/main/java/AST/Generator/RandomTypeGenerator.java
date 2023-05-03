package AST.Generator;

import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Types.DCollectionTypes.DArray;
import AST.SymbolTable.Types.DMap.DMap;
import AST.SymbolTable.Types.PrimitiveTypes.BaseType;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.Types.PrimitiveTypes.Char;
import AST.SymbolTable.Types.DCollectionTypes.DSet;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.Types.DCollectionTypes.Multiset;
import AST.SymbolTable.Types.PrimitiveTypes.Real;
import AST.SymbolTable.Types.DCollectionTypes.Seq;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.Tuple;
import AST.SymbolTable.Types.UserDefinedTypes.UserDefinedType;
import java.util.ArrayList;
import java.util.List;

public class RandomTypeGenerator {

    public static final int MAX_TYPE_DEPTH = 2;
    public static final List<BaseType> PRIMITIVE_TYPES = List.of(new Int(), new Bool(), new Real()); //, new Char()
    public static double PROB_PRIMITIVE = 40.0;
    public static final List<UserDefinedType> USER_DEFINED_TYPES = List.of(new Tuple());
    public static double PROB_USER_DEFINED = 5.0;
    public static final List<DCollection> COLLECTION_TYPES = List.of(new DSet(), new Seq(), new Multiset(), new DArray());
    public static double PROB_COLLECTION = 20.0;
    public static final List<DMap> DMAPS = List.of(new DMap());
    public static double PROB_DMAP = 7.0;
    private static int typeDepth = 0;

    public List<Type> generateTypes(int noOfTypes, SymbolTable symbolTable) {
        typeDepth++;
        List<Type> types = new ArrayList<>();
        for (int i = 0; i < noOfTypes; i++) {
            Type t = generateType();
            Type concrete = t.concrete(symbolTable);
            types.add(concrete);
        }
        typeDepth--;
        return types;
    }

    private Type generateType() {
        Type t = null;
        while (t == null) {
            double ratioSum = PROB_COLLECTION + PROB_DMAP + PROB_PRIMITIVE + PROB_USER_DEFINED;
            double probType = GeneratorConfig.getRandom().nextDouble() * ratioSum;

            if (typeDepth > MAX_TYPE_DEPTH || (probType -= PROB_PRIMITIVE) < 0) {
                int index = GeneratorConfig.getRandom().nextInt(PRIMITIVE_TYPES.size());
                t = PRIMITIVE_TYPES.get(index);

            } else if ((probType -= PROB_DMAP) < 0) {
                PROB_DMAP *= GeneratorConfig.OPTION_DECAY_FACTOR;
                int index = GeneratorConfig.getRandom().nextInt(DMAPS.size());
                t = DMAPS.get(index);

            } else if ((probType -= PROB_COLLECTION) < 0) {
                PROB_COLLECTION *= GeneratorConfig.OPTION_DECAY_FACTOR;
                int index = GeneratorConfig.getRandom().nextInt(COLLECTION_TYPES.size());
                t = COLLECTION_TYPES.get(index);

            } else if ((probType -= PROB_USER_DEFINED) < 0) {
                PROB_USER_DEFINED *= GeneratorConfig.OPTION_DECAY_FACTOR;
                int index = GeneratorConfig.getRandom().nextInt(USER_DEFINED_TYPES.size());
                t = USER_DEFINED_TYPES.get(index);
            }
        }
        return t;
    }

    public List<Type> generateMethodTypes(int noOfArgs, SymbolTable symbolTable) {
        typeDepth += MAX_TYPE_DEPTH;
        List<Type> types = new ArrayList<>();

        int i = 0;
        while (i < noOfArgs) {
            Type t = generateTypes(1, symbolTable).get(0);

            if (t.validMethodType()) {
                types.add(t.concrete(symbolTable));
                i++;
            }
        }

        typeDepth -= MAX_TYPE_DEPTH;
        return types;
    }

    public Type generateMatchType(SymbolTable symbolTable) {
        Type t = null;
        while (t == null) {
            t = generateTypes(1, symbolTable).get(0);

            if (t.isCollection()) {
                t = null;
            }
        }

        return t;
    }
}
