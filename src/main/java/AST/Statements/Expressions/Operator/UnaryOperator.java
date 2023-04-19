package AST.Statements.Expressions.Operator;

import AST.Errors.SemanticException;
import AST.Generator.GeneratorConfig;
import AST.Statements.Expressions.Array.ArrayValue;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.DCollectionTypes.DSet;
import AST.SymbolTable.Types.DCollectionTypes.Multiset;
import AST.SymbolTable.Types.DCollectionTypes.Seq;
import AST.SymbolTable.Types.DMap.DMap;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.lang.invoke.MutableCallSite;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public enum UnaryOperator implements Operator {
    Cardinality("|%s|", List.of(Args.SEQ, Args.DSET, Args.MULTISET, Args.DMAP), new Int()) {
        @Override
        public String formExpression(List<Expression> args) {
            String res = args.get(0).toString();
            return String.format("|%s|", res);
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
            Object val = expValue.get(0);
            if (val != null) {
                Type type = exp.getTypes().get(0);
                if (type.equals(new Seq())) {
                    List<Object> valL = (List<Object>) val;
                    return valL.size();

                } else if (type.equals(new DSet())) {
                    Set<Object> valS = (Set<Object>) val;
                    return valS.size();

                } else if (type.equals(new Multiset())) {
                    Map<Object, Integer> valM = (Map<Object, Integer>) val;
                    return valM.values().stream().reduce(0, Integer::sum);

                } else if (type.equals(new DMap())) {
                    Map<Object, Object> valM = (Map<Object, Object>) val;
                    return valM.keySet().size();

                }
            }
            return null;
        }
    },
    ArrayLength("Length", List.of(Args.DARRAY), new Int()) {
        @Override
        public String formExpression(List<Expression> args) {
            String res = args.get(0).toString();
            return String.format("%s.Length", res);
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
                return valL.size();
            }
            return null;
        }
    },
    Negate("!", List.of(Args.BOOL), new Bool()) {
        @Override
        public String formExpression(List<Expression> args) {
            String res = args.get(0).toString();
            return String.format("!(%s)", res);
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
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable, Type expected) {
            DSet set = (DSet) expected;
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

        @Override
        public String formExpression(List<Expression> args) {
            Expression res = args.get(0);
            return String.format("(%s).Keys", res);
        }
    },
    ValuesMap("Values", List.of(Args.DMAP), new DSet()) {
        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable, Type expected) {
            DSet set = (DSet) expected;
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

        @Override
        public String formExpression(List<Expression> args) {
            Expression res = args.get(0);
            return String.format("(%s).Values", res);
        }
    },
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
    public String formExpression(List<Expression> args) {
        String res = args.get(0).toString();
        for (int i = 1; i < args.size(); i++) {
            Expression rhs = args.get(i);
            res = String.format("(%s %s %s)", res, operator, rhs);

        }
        return res;
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
