package AST.Expressions.Operator;

import AST.Errors.SemanticException;
import AST.Generator.GeneratorConfig;
import AST.Expressions.Array.ArrayValue;
import AST.Expressions.Expression;
import AST.SymbolTable.Method.Method;
import AST.SymbolTable.Types.DCollectionTypes.DSet;
import AST.SymbolTable.Types.DCollectionTypes.Multiset;
import AST.SymbolTable.Types.DCollectionTypes.Seq;
import AST.SymbolTable.Types.DMap.DMap;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.Tuple;
import AST.SymbolTable.Types.Variables.Variable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public enum UnaryOperator implements Operator {
    Cardinality("|%s|", List.of(Args.SEQ, Args.DSET, Args.MULTISET, Args.DMAP, Args.DSTRING), new Int()) {
        @Override
        public String formExpression(List<Expression> args) {
            return String.format("|%s|", args.get(0).toString());
        }

        @Override
        public String formMinimizedExpression(List<Expression> args) {
            return String.format("|%s|", args.get(0).minimizedTestCase());
        }

        @Override
        public List<String> formOutput(List<Expression> args) {
            Set<String> res = new HashSet<>();
            for (String arg : args.get(0).toOutput()) {
                res.add(String.format("|%s|", arg));
            }

            List<String> r = new ArrayList<>(res);
            Collections.shuffle(r, GeneratorConfig.getRandom());
            return r.subList(0, Math.min(5, res.size()));
        }

        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable,
            Type expected) {
            return types.stream()
                .map(x -> x.concrete(symbolTable))
                .collect(Collectors.toList());
        }

        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression exp = args.get(0);
            List<Object> expValue = exp.getValue(paramsMap);
            Object value = expValue.get(0);
            if (value != null) {
                Type type = exp.getTypes().get(0);

                return type.cardinality(value);
            }
            return null;
        }

        @Override
        public int restrictions() {
            int prev = Seq.MIN_SIZE_OF_SEQ;
            Seq.MIN_SIZE_OF_SEQ = 1;
            DSet.MIN_SIZE_OF_SET = 1;
            Multiset.MIN_SIZE_OF_MULTISET = 1;
            return prev;
        }

        @Override
        public void restore(int v) {
            Seq.MIN_SIZE_OF_SEQ = v;
            DSet.MIN_SIZE_OF_SET = v;
            Multiset.MIN_SIZE_OF_MULTISET = v;
        }
    },
    ArrayLength("Length", List.of(Args.DARRAY), new Int()) {
        @Override
        public String formExpression(List<Expression> args) {
            return String.format("%s.Length", args.get(0).toString());
        }

        @Override
        public String formMinimizedExpression(List<Expression> args) {
            return String.format("%s.Length", args.get(0).minimizedTestCase());
        }

        @Override
        public List<String> formOutput(List<Expression> args) {
            Set<String> res = new HashSet<>();
            for (String arg : args.get(0).toOutput()) {
                res.add(String.format("%s.Length", arg));
            }

            List<String> r = new ArrayList<>(res);
            Collections.shuffle(r, GeneratorConfig.getRandom());
            return r.subList(0, Math.min(5, res.size()));
        }

        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable, Type expected) {
            return types.stream()
                .map(x -> x.concrete(symbolTable))
                .collect(Collectors.toList());
        }

        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression exp = args.get(0);
            Object val = exp.getValue(paramsMap).get(0);
            if (val != null) {
                ArrayValue valL = (ArrayValue) val;
                return BigInteger.valueOf(valL.size());
            }
            return null;
        }
    },
    RealFloor("Floor", List.of(Args.REAL), new Int()) {
        @Override
        public String formExpression(List<Expression> args) {
            return String.format("(%s).Floor", args.get(0).toString());
        }

        @Override
        public String formMinimizedExpression(List<Expression> args) {
            return String.format("(%s).Floor", args.get(0).minimizedTestCase());
        }

        @Override
        public List<String> formOutput(List<Expression> args) {
            Set<String> res = new HashSet<>();
            for (String arg : args.get(0).toOutput()) {
                res.add(String.format("(%s).Floor", arg));
            }

            List<String> r = new ArrayList<>(res);
            Collections.shuffle(r, GeneratorConfig.getRandom());
            return r.subList(0, Math.min(5, res.size()));
        }

        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable, Type expected) {
            return types.stream()
                .map(x -> x.concrete(symbolTable))
                .collect(Collectors.toList());
        }

        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression exp = args.get(0);
            Object val = exp.getValue(paramsMap).get(0);
            if (val != null) {
                String d = (String) val;
                return BigInteger.valueOf((int) Math.floor(Double.parseDouble(d)));
            }
            return null;
        }
    },
    Negate("!", List.of(Args.BOOL), new Bool()) {
        @Override
        public String formExpression(List<Expression> args) {
            return String.format("!(%s)", args.get(0).toString());
        }

        @Override
        public String formMinimizedExpression(List<Expression> args) {
            return String.format("!(%s)", args.get(0).minimizedTestCase());
        }

        @Override
        public List<String> formOutput(List<Expression> args) {
            Set<String> res = new HashSet<>();
            for (String arg : args.get(0).toOutput()) {
                res.add(String.format("!(%s)", arg));
            }
            List<String> r = new ArrayList<>(res);
            Collections.shuffle(r, GeneratorConfig.getRandom());
            return r.subList(0, Math.min(5, res.size()));
        }

        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression exp = args.get(0);
            Object val = exp.getValue(paramsMap).get(0);
            if (val != null) {
                Boolean bool = (Boolean) val;
                return !bool;
            }
            return null;
        }
    },
    KeysMap("Keys", List.of(Args.DMAP), new DSet()) {
        @Override
        public String formExpression(List<Expression> args) {
            return String.format("(%s).Keys", args.get(0).toString());
        }

        @Override
        public String formMinimizedExpression(List<Expression> args) {
            return String.format("(%s).Keys", args.get(0).minimizedTestCase());
        }

        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable, Type expected) {
            DSet set = expected.asDSet();
            Type innerType = set.getInnerType();

            List<Type> ret = new ArrayList<>();
            ret.add(new DMap().setKeyType(innerType).concrete(symbolTable));
            return ret;
        }

        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression exp = args.get(0);
            Object val = exp.getValue(paramsMap).get(0);
            if (val != null) {
                Map<Object, Object> m = (Map<Object, Object>) val;
                return m.keySet();
            }
            return null;
        }

        @Override
        public List<String> formOutput(List<Expression> args) {
            Set<String> res = new HashSet<>();
            for (String arg : args.get(0).toOutput()) {
                res.add(String.format("(%s).Keys", arg));
            }
            List<String> r = new ArrayList<>(res);
            Collections.shuffle(r, GeneratorConfig.getRandom());
            return r.subList(0, Math.min(5, res.size()));
        }
    },
    ValuesMap("Values", List.of(Args.DMAP), new DSet()) {
        @Override
        public String formExpression(List<Expression> args) {
            return String.format("(%s).Values", args.get(0).toString());
        }

        @Override
        public String formMinimizedExpression(List<Expression> args) {
            return String.format("(%s).Values", args.get(0).minimizedTestCase());
        }

        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable, Type expected) {
            DSet set = expected.asDSet();
            Type innerType = set.getInnerType();

            List<Type> ret = new ArrayList<>();
            ret.add(new DMap().setValueType(innerType).concrete(symbolTable));
            return ret;
        }

        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression exp = args.get(0);
            Object val = exp.getValue(paramsMap).get(0);
            if (val != null) {
                Map<Object, Object> m = (Map<Object, Object>) val;
                return new HashSet<>(m.values());
            }
            return null;
        }

        @Override
        public List<String> formOutput(List<Expression> args) {
            Set<String> res = new HashSet<>();
            for (String arg : args.get(0).toOutput()) {
                res.add(String.format("(%s).Values", arg));
            }
            List<String> r = new ArrayList<>(res);
            Collections.shuffle(r, GeneratorConfig.getRandom());
            return r.subList(0, Math.min(5, res.size()));
        }
    },
    ItemsMap("Items", List.of(Args.DMAP), new DSet(new Tuple(Args.PAIR_NULL))) {
        @Override
        public String formExpression(List<Expression> args) {
            return String.format("(%s).Items", args.get(0).toString());
        }

        @Override
        public String formMinimizedExpression(List<Expression> args) {
            return String.format("(%s).Items", args.get(0).minimizedTestCase());
        }

        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable, Type expected) {
            DSet set = expected.asDSet();
            Type innerType = set.getInnerType();
            Tuple innerTuple = innerType.asTuple();
            Type keyT = innerTuple.getType(0);
            Type valueT = innerTuple.getType(1);

            List<Type> ret = new ArrayList<>();
            ret.add(new DMap(keyT, valueT).concrete(symbolTable));
            return ret;
        }

        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression exp = args.get(0);
            Object val = exp.getValue(paramsMap).get(0);
            if (val != null) {
                Map<Object, Object> m = (Map<Object, Object>) val;
                Set<Object> r = new HashSet<>();
                for (Entry<Object, Object> entry : m.entrySet()) {
                    List<Object> l = new ArrayList<>();
                    l.add(entry.getKey());
                    l.add(entry.getValue());
                    r.add(l);
                }
                return r;
            }
            return null;
        }

        @Override
        public boolean returnType(Type type) {
            for (Type t : getType()) {
                if (t.equals(type)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<String> formOutput(List<Expression> args) {
            Set<String> res = new HashSet<>();
            for (String arg : args.get(0).toOutput()) {
                res.add(String.format("(%s).Items", arg));
            }
            List<String> r = new ArrayList<>(res);
            Collections.shuffle(r, GeneratorConfig.getRandom());
            return r.subList(0, Math.min(5, res.size()));
        }
    },
    Is("is", List.of(Args.INT, Args.CHAR, Args.BOOL, Args.REAL, Args.DSTRING, Args.SEQ, Args.DSET, Args.MULTISET, Args.DMAP, Args.DARRAY, Args.TUPLE, Args.DATATYPE), new Bool()) {
        @Override
        public String formExpression(List<Expression> args) {
            String variableType = args.get(0).getTypes().get(0).getVariableType();
            return String.format("(%s) is %s", args.get(0).toString(), variableType);
        }

        @Override
        public String formMinimizedExpression(List<Expression> args) {
            String variableType = args.get(0).getTypes().get(0).getVariableType();
            return String.format("(%s) is %s", args.get(0).minimizedTestCase(), variableType);
        }

        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            return true;
        }

        @Override
        public List<String> formOutput(List<Expression> args) {
            String variableType = args.get(0).getTypes().get(0).getVariableType();
            Set<String> res = new HashSet<>();
            for (String arg : args.get(0).toOutput()) {
                res.add(String.format("(%s) is %s", arg, variableType));
            }
            List<String> r = new ArrayList<>(res);
            Collections.shuffle(r, GeneratorConfig.getRandom());
            return r.subList(0, Math.min(5, res.size()));
        }
    }
    ;

    private final String operator;
    private final List<List<Type>> typeArgs;
    private final List<Type> retTypes;

    UnaryOperator(String operator, List<List<Type>> typeArgs, Type retTypes) {
        this.operator = operator;
        this.typeArgs = typeArgs;
        this.retTypes = List.of(retTypes);
    }

    UnaryOperator(String operator, List<List<Type>> typeArgs, List<Type> retTypes) {
        this.operator = operator;
        this.typeArgs = typeArgs;
        this.retTypes = retTypes;
    }

    @Override
    public List<Type> getType() {
        return retTypes;
    }

    @Override
    public void semanticCheck(Method method, List<Expression> expressions)
        throws SemanticException {
    }

    @Override
    public List<List<Type>> getTypeArgs() {
        return typeArgs;
    }

    public int getNumberOfArgs() {
        return 1;
    }
}
