package AST.Generator;

import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Types.DCollectionTypes.DArray;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.Types.PrimitiveTypes.Char;
import AST.SymbolTable.Types.DCollectionTypes.DSet;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.Types.DCollectionTypes.Multiset;
import AST.SymbolTable.Types.PrimitiveTypes.Real;
import AST.SymbolTable.Types.DCollectionTypes.Seq;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import java.util.ArrayList;
import java.util.List;

public class RandomTypeGenerator {

    public static final int MAX_TYPE_DEPTH = 3;
    public static final ArrayList<Type> PRIMITIVE_TYPES = new ArrayList<>(List.of(new Int(), new Bool(), new Char(), new Real()));
    public static final List<DCollection> COLLECTION_TYPES = List.of(new DSet(), new Seq(), new Multiset(), new DArray());
    private static int typeDepth = 0;

    public List<Type> generateTypes(int noOfTypes, SymbolTable symbolTable) {
        typeDepth++;
        List<Type> types = new ArrayList<>();
        List<Type> option = new ArrayList<>();
        option.addAll(PRIMITIVE_TYPES);
        if (typeDepth < MAX_TYPE_DEPTH) {
            option.addAll(COLLECTION_TYPES);
        }

//        if (typeDepth < MAX_TYPE_DEPTH) {
//            List<Type> collections = new ArrayList<>();
//            collections.addAll(option.stream().map(DSet::new).collect(Collectors.toList()));
//            collections.addAll(option.stream().map(Seq::new).collect(Collectors.toList()));
//            collections.addAll(option.stream().map(Multiset::new).collect(Collectors.toList()));
//            collections.addAll(option.stream().map(DArray::new).collect(Collectors.toList()));
//            option.addAll(collections);
//        }
        for (int i = 0; i < noOfTypes; i++) {
            int randType = GeneratorConfig.getRandom().nextInt(option.size());
            Type t = option.get(randType);
            Type concrete = t.concrete(symbolTable);
            types.add(concrete);
        }

        typeDepth--;
        return types;
    }
}
