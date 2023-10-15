package AST.Expressions.Operator;

import AST.SymbolTable.Types.DCollectionTypes.DArray;
import AST.SymbolTable.Types.DMap.DMap;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.Types.PrimitiveTypes.Char;
import AST.SymbolTable.Types.DCollectionTypes.DSet;
import AST.SymbolTable.Types.PrimitiveTypes.DString;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.Types.DCollectionTypes.Multiset;
import AST.SymbolTable.Types.PrimitiveTypes.Real;
import AST.SymbolTable.Types.DCollectionTypes.Seq;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.DataType.DataType;
import AST.SymbolTable.Types.UserDefinedTypes.Tuple;
import java.util.ArrayList;
import java.util.List;

public class Args {

    public static final List<Type> INT_INT = List.of(new Int(), new Int());
    public static final List<Type> BOOL_BOOL = List.of(new Bool(), new Bool());
    public static final List<Type> CHAR_CHAR = List.of(new Char(), new Char());
    public static final List<Type> REAL_REAL = List.of(new Real(), new Real());
    public static final List<Type> DSET_DSET = List.of(new DSet(), new DSet());
    public static final List<Type> DSTRING_DSTRING = List.of(new DString(), new DString());
    public static final List<Type> SEQ_SEQ = List.of(new Seq(), new Seq());
    public static final List<Type> MULTISET_MULTISET = List.of(new Multiset(), new Multiset());
    public static final List<Type> DMAP_DMAP = List.of(new DMap(), new DMap());
    public static final List<Type> TUPLE_TUPLE = List.of(new Tuple(), new Tuple());
    public static final List<Type> DATATYPE_DATATYPE = List.of(new DataType(), new DataType());
    public static final List<Type> STRING_STRING = List.of(new DString(), new DString());

    public static final List<Type> DMAP_DSET = List.of(new DMap(), new DSet());

    public static final List<Type> INT = List.of(new Int());
    public static final List<Type> CHAR = List.of(new Char());
    public static final List<Type> BOOL = List.of(new Bool());
    public static final List<Type> REAL = List.of(new Real());
    public static final List<Type> DSTRING = List.of(new DString());
    public static final List<Type> SEQ = List.of(new Seq());
    public static final List<Type> DSET = List.of(new DSet());
    public static final List<Type> MULTISET = List.of(new Multiset());
    public static final List<Type> DMAP = List.of(new DMap());
    public static final List<Type> DARRAY = List.of(new DArray());
    public static final List<Type> TUPLE = List.of(new Tuple());
    public static final List<Type> DATATYPE = List.of(new DataType());

    public static List<Type> PAIR_NULL;
    static {
        PAIR_NULL = new ArrayList<>();
        PAIR_NULL.add(null);
        PAIR_NULL.add(null);
    }

}
