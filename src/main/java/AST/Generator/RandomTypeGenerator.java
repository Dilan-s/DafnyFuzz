package AST.Generator;

import AST.SymbolTable.PrimitiveTypes.Bool;
import AST.SymbolTable.PrimitiveTypes.Char;
import AST.SymbolTable.PrimitiveTypes.DSet;
import AST.SymbolTable.PrimitiveTypes.Int;
import AST.SymbolTable.PrimitiveTypes.Multiset;
import AST.SymbolTable.PrimitiveTypes.Real;
import AST.SymbolTable.PrimitiveTypes.Seq;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RandomTypeGenerator {

    public static final int MAX_TYPE_DEPTH = 2;
    private static int typeDepth = 0;

    public Type generateNonCollectionType(int noOfTypes, SymbolTable symbolTable) {
        typeDepth += MAX_TYPE_DEPTH;
        List<Type> types = generateTypes(noOfTypes, symbolTable);
        typeDepth -= MAX_TYPE_DEPTH;
        return types.get(0);
    }

    public List<Type> generateTypes(int noOfTypes, SymbolTable symbolTable) {
        typeDepth++;
        List<Type> types = new ArrayList<>();
        List<Type> option = new ArrayList<>(List.of(new Int(), new Bool(), new Char(), new Real()));

        if (typeDepth < MAX_TYPE_DEPTH) {
            List<Type> collections = new ArrayList<>();
            collections.addAll(option.stream().map(DSet::new).collect(Collectors.toList()));
            collections.addAll(option.stream().map(Seq::new).collect(Collectors.toList()));
            collections.addAll(option.stream().map(Multiset::new).collect(Collectors.toList()));
            option.addAll(collections);
        }
        for (int i = 0; i < noOfTypes; i++) {
            int randType = GeneratorConfig.getRandom().nextInt(option.size());
            types.add(option.get(randType));
        }

        typeDepth--;
        return types;
    }
}
