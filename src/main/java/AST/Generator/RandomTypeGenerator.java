package AST.Generator;

import AST.SymbolTable.Types.DCollectionTypes.DArray;
import AST.SymbolTable.Types.DMap.DMap;
import AST.SymbolTable.Types.PrimitiveTypes.BaseType;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.Types.DCollectionTypes.DSet;
import AST.SymbolTable.Types.PrimitiveTypes.Char;
import AST.SymbolTable.Types.PrimitiveTypes.DString;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.Types.DCollectionTypes.Multiset;
import AST.SymbolTable.Types.PrimitiveTypes.Real;
import AST.SymbolTable.Types.DCollectionTypes.Seq;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.ArrowType;
import AST.SymbolTable.Types.UserDefinedTypes.DClass;
import AST.SymbolTable.Types.UserDefinedTypes.DataType.DataType;
import AST.SymbolTable.Types.UserDefinedTypes.DataType.DataTypeRule;
import AST.SymbolTable.Types.UserDefinedTypes.Tuple;
import AST.SymbolTable.Types.UserDefinedTypes.TypeAlias;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RandomTypeGenerator {

    public static final int MAX_TYPE_DEPTH = 2;
    public static final List<BaseType> PRIMITIVE_TYPES = List.of(new Int(), new Bool(), new Real(), new Char());
    public static final List<DataType> DEFINED_DATA_TYPES = new ArrayList<>();
    public static final List<TypeAlias> DEFINED_TYPE_ALIAS = new ArrayList<>();
    public static final List<DClass> DEFINED_DCLASS = new ArrayList<>();
    private static final double PROB_REUSE = 0.75;

    public static double PROB_INT = 40.0;
    public static double PROB_BOOL = 40.0;
    public static double PROB_CHAR = 40.0;
    public static double PROB_DSTRING = 20.0;
    public static double PROB_REAL = 10.0;
    public static double PROB_DMAP = 10.0;
    public static double PROB_DARRAY = 10.0;
    public static double PROB_DSET = 10.0;
    public static double PROB_SEQ = 10.0;
    public static double PROB_MULTISET = 10.0;
    public static double PROB_TUPLE = 20.0;
    public static double PROB_DATATYPE = 20.0;
    public static double PROB_TYPE_ALIAS = 20.0;
    public static double PROB_DCLASS = 20.0;
    public static double PROB_ARROW_TYPE = 10.0;

    public static double PROB_SWARM = 0.05;
    private static int typeDepth = 0;


    private Type generateType() {
        Type t = null;
        boolean swarm = GeneratorConfig.getRandom().nextDouble() < PROB_SWARM;
        while (t == null) {


            if (typeDepth > MAX_TYPE_DEPTH) {
                int index = GeneratorConfig.getRandom()
                    .nextInt(List.of(new Int(), new Bool(), new Real()).size());
                t = PRIMITIVE_TYPES.get(index);

            } else {
                double ratioSum = PROB_INT + PROB_BOOL + PROB_CHAR + PROB_REAL + PROB_DSTRING +
                    PROB_DMAP + PROB_DARRAY + PROB_DSET + PROB_SEQ + PROB_MULTISET + PROB_TUPLE +
                    PROB_DATATYPE + PROB_TYPE_ALIAS + PROB_DCLASS + PROB_ARROW_TYPE;
                double probType = GeneratorConfig.getRandom().nextDouble() * ratioSum;

                if (swarm) {
                    reset();
                }

                if ((probType -= PROB_INT) < 0) {
                    // int
                    PROB_INT *= GeneratorConfig.OPTION_DECAY_FACTOR;
                    t = new Int();
                    if (swarm) {
                        PROB_INT *= GeneratorConfig.SWARM_MULTIPLIER_LARGE;
                    }

                } else if ((probType -= PROB_BOOL) < 0) {
                    // bool
                    PROB_BOOL *= GeneratorConfig.OPTION_DECAY_FACTOR;
                    t = new Bool();
                    if (swarm) {
                        PROB_BOOL *= GeneratorConfig.SWARM_MULTIPLIER_LARGE;
                    }

                } else if ((probType -= PROB_CHAR) < 0) {
                    // bool
                    PROB_CHAR *= GeneratorConfig.OPTION_DECAY_FACTOR;
                    t = new Char();
                    if (swarm) {
                        PROB_CHAR *= GeneratorConfig.SWARM_MULTIPLIER_LARGE;
                    }

                } else if ((probType -= PROB_REAL) < 0) {
                    // real
                    PROB_REAL *= GeneratorConfig.OPTION_DECAY_FACTOR;
                    t = new Real();
                    if (swarm) {
                        PROB_REAL *= GeneratorConfig.SWARM_MULTIPLIER_LARGE;
                    }

                } else if ((probType -= PROB_DSTRING) < 0) {
                    // string
                    PROB_DSTRING *= GeneratorConfig.OPTION_DECAY_FACTOR;
                    t = new DString();
                    if (swarm) {
                        PROB_DSTRING *= GeneratorConfig.SWARM_MULTIPLIER_LARGE;
                    }

                } else if ((probType -= PROB_DMAP) < 0) {
                    // map
                    PROB_DMAP *= GeneratorConfig.OPTION_DECAY_FACTOR;
                    t = new DMap();
                    if (swarm) {
                        PROB_DMAP *= GeneratorConfig.SWARM_MULTIPLIER_SMALL;
                    }

                } else if ((probType -= PROB_DARRAY) < 0) {
                    // array
                    PROB_DARRAY *= GeneratorConfig.OPTION_DECAY_FACTOR;
                    t = new DArray();
                    if (swarm) {
                        PROB_DARRAY *= GeneratorConfig.SWARM_MULTIPLIER_SMALL;
                    }

                } else if ((probType -= PROB_DSET) < 0) {
                    // set
                    PROB_DSET *= GeneratorConfig.OPTION_DECAY_FACTOR;
                    t = new DSet();
                    if (swarm) {
                        PROB_DSET *= GeneratorConfig.SWARM_MULTIPLIER_SMALL;
                    }

                } else if ((probType -= PROB_SEQ) < 0) {
                    // seq
                    PROB_SEQ *= GeneratorConfig.OPTION_DECAY_FACTOR;
                    t = new Seq();
                    if (swarm) {
                        PROB_SEQ *= GeneratorConfig.SWARM_MULTIPLIER_SMALL;
                    }

                } else if ((probType -= PROB_MULTISET) < 0) {
                    // multiset
                    PROB_MULTISET *= GeneratorConfig.OPTION_DECAY_FACTOR;
                    t = new Multiset();
                    if (swarm) {
                        PROB_MULTISET *= GeneratorConfig.SWARM_MULTIPLIER_SMALL;
                    }

                } else if ((probType -= PROB_TUPLE) < 0) {
                    // tuple
                    PROB_TUPLE *= GeneratorConfig.OPTION_DECAY_FACTOR;
                    t = new Tuple();
                    if (swarm) {
                        PROB_TUPLE *= GeneratorConfig.SWARM_MULTIPLIER_SMALL;
                    }
                } else if ((probType -= PROB_DATATYPE) < 0) {
                    PROB_DATATYPE *= GeneratorConfig.OPTION_DECAY_FACTOR;
                    double prob_reuse = GeneratorConfig.getRandom().nextDouble();
                    if (!DEFINED_DATA_TYPES.isEmpty() && prob_reuse < PROB_REUSE) {
                        int ind = GeneratorConfig.getRandom().nextInt(DEFINED_DATA_TYPES.size());
                        t = DEFINED_DATA_TYPES.get(ind);
                    } else {
                        DataType dataType = new DataType();
                        t = dataType;
                        DEFINED_DATA_TYPES.add(dataType);
                    }

                    if (swarm) {
                        PROB_DATATYPE *= GeneratorConfig.SWARM_MULTIPLIER_SMALL;
                    }
                } else if ((probType -= PROB_TYPE_ALIAS) < 0) {
                    PROB_TYPE_ALIAS *= GeneratorConfig.OPTION_DECAY_FACTOR;
                    double prob_reuse = GeneratorConfig.getRandom().nextDouble();
                    if (!DEFINED_TYPE_ALIAS.isEmpty() && prob_reuse < PROB_REUSE) {
                        int ind = GeneratorConfig.getRandom().nextInt(DEFINED_TYPE_ALIAS.size());
                        t = DEFINED_TYPE_ALIAS.get(ind);
                    } else {
                        TypeAlias typeAlias = new TypeAlias();
                        t = typeAlias;
                        DEFINED_TYPE_ALIAS.add(typeAlias);
                    }

                    if (swarm) {
                        PROB_TYPE_ALIAS *= GeneratorConfig.SWARM_MULTIPLIER_SMALL;
                    }
                } else if ((probType -= PROB_DCLASS) < 0) {
                    PROB_DCLASS *= GeneratorConfig.OPTION_DECAY_FACTOR;
                    t = generateClass();

                    if (swarm) {
                        PROB_DCLASS *= GeneratorConfig.SWARM_MULTIPLIER_SMALL;
                    }
                } else if ((probType -= PROB_ARROW_TYPE) < 0) {
                    PROB_ARROW_TYPE *= GeneratorConfig.OPTION_DECAY_FACTOR;
                    t = new ArrowType();

                    if (swarm) {
                        PROB_ARROW_TYPE *= GeneratorConfig.SWARM_MULTIPLIER_SMALL;
                    }
                }
            }
        }
        return t;
    }

    private void reset() {
        PROB_INT = 40.0;
        PROB_BOOL = 40.0;
        PROB_CHAR = 40.0;
        PROB_DSTRING = 20.0;
        PROB_REAL = 10.0;
        PROB_DMAP = 10.0;
        PROB_DARRAY = 10.0;
        PROB_DSET = 10.0;
        PROB_SEQ = 10.0;
        PROB_MULTISET = 10.0;
        PROB_TUPLE = 20.0;
        PROB_DATATYPE = 20.0;
        PROB_TYPE_ALIAS = 20.0;
        PROB_DCLASS = 20.0;
        PROB_ARROW_TYPE = 10.0;
    }

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

    public List<Type> generateMapTypes(int noOfTypes, SymbolTable symbolTable) {
        typeDepth++;
        List<Type> types = new ArrayList<>();
        int i = 0;
        while (i < noOfTypes) {
            Type t = generateTypes(1, symbolTable).get(0);
            if (!t.equals(new DMap()) && !t.equals(new ArrowType())) {
                Type concrete = t.concrete(symbolTable);
                types.add(concrete);
                i++;
            }
        }

        typeDepth--;
        return types;
    }

    public List<Type> generateMethodTypes(int noOfArgs, SymbolTable symbolTable) {
        typeDepth++;
        List<Type> types = new ArrayList<>();

        int i = 0;
        while (i < noOfArgs) {
            Type t = generateTypes(1, symbolTable).get(0);
            Type concrete = t.concrete(symbolTable);

            if (concrete.validMethodType()) {
                types.add(concrete);
                i++;
            }
        }

        typeDepth--;
        return types;
    }

    public List<Type> generateFunctionTypes(int noOfArgs, SymbolTable symbolTable) {
        typeDepth++;
        List<Type> types = new ArrayList<>();

        int i = 0;
        while (i < noOfArgs) {
            Type t = generateTypes(1, symbolTable).get(0);
            Type concrete = t.concrete(symbolTable);

            if (concrete.validForFunctionBody()) {
                types.add(concrete);
                i++;
            }
        }

        typeDepth--;
        return types;
    }

    public List<Type> generateTypesWithoutCurrent(int noTypes, SymbolTable symbolTable, Type current) {
        typeDepth++;
        List<Type> ret = new ArrayList<>();
        while (ret.size() != noTypes) {
            List<Type> types = generateTypes(noTypes - ret.size(), symbolTable);
            ret.addAll(types.stream().filter(t -> t != current).collect(Collectors.toList()));
        }

        typeDepth--;
        return ret;
    }

    public Type generateMatchType(SymbolTable symbolTable) {
        typeDepth++;
        Type t = null;
        while (t == null) {
            t = generateTypes(1, symbolTable).get(0);

            if (t.isCollection() || t.equals(new Tuple()) || t.equals(new DMap()) || t.equals(new DataType()) || t.equals(new DataTypeRule()) || t.equals(new DString()) || t.equals(new DClass()) || t.equals(new ArrowType())) {
                t = null;
            }
        }

        typeDepth--;
        return t;
    }

    public Type generateTypeAliasType(SymbolTable symbolTable) {
        typeDepth++;

        Type t = null;
        while (t == null) {
            t = generateTypes(1, symbolTable).get(0);

            if (t.equals(new TypeAlias())) {
                t = null;
            }
        }

        typeDepth--;
        return t;
    }

    public DClass generateClass() {
        double prob_reuse = GeneratorConfig.getRandom().nextDouble();
        if (!DEFINED_DCLASS.isEmpty() && prob_reuse < PROB_REUSE) {
            int ind = GeneratorConfig.getRandom().nextInt(DEFINED_DCLASS.size());
            return DEFINED_DCLASS.get(ind);
        } else {
            DClass dClass = new DClass();
            DEFINED_DCLASS.add(dClass);
            return dClass;
        }
    }
}
