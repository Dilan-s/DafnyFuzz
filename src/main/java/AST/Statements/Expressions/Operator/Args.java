package AST.Statements.Expressions.Operator;

import AST.SymbolTable.PrimitiveTypes.Bool;
import AST.SymbolTable.PrimitiveTypes.Char;
import AST.SymbolTable.DCollectionTypes.DSet;
import AST.SymbolTable.PrimitiveTypes.Int;
import AST.SymbolTable.DCollectionTypes.Multiset;
import AST.SymbolTable.PrimitiveTypes.Real;
import AST.SymbolTable.DCollectionTypes.Seq;
import AST.SymbolTable.Type;
import java.util.List;

public class Args {

    public static final List<Type> BOOL_BOOL = List.of(new Bool(), new Bool());
    public static final List<Type> INT_INT = List.of(new Int(), new Int());
    public static final List<Type> CHAR_CHAR = List.of(new Char(), new Char());
    public static final List<Type> DSET_DSET = List.of(new DSet(), new DSet());
    public static final List<Type> MULTISET_MULTISET = List.of(new Multiset(), new Multiset());
    public static final List<Type> REAL_REAL = List.of(new Real(), new Real());
    public static final List<Type> SEQ_SEQ = List.of(new Seq(), new Seq());
    public static final List<Type> SEQ = List.of(new Seq());
    public static final List<Type> DSET = List.of(new DSet());
    public static final List<Type> MULTISET = List.of(new Multiset());
}
