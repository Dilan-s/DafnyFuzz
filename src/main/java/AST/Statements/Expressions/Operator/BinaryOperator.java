package AST.Statements.Expressions.Operator;

import AST.Errors.SemanticException;
import AST.Generator.RandomTypeGenerator;
import AST.Statements.Expressions.Expression;
import AST.SymbolTable.Types.DCollectionTypes.DCollection;
import AST.SymbolTable.Method;
import AST.SymbolTable.Types.PrimitiveTypes.Bool;
import AST.SymbolTable.Types.DCollectionTypes.DSet;
import AST.SymbolTable.Types.PrimitiveTypes.Int;
import AST.SymbolTable.Types.DCollectionTypes.Multiset;
import AST.SymbolTable.Types.DCollectionTypes.Seq;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum BinaryOperator implements Operator {
    Equivalence("<==>", List.of(Args.BOOL_BOOL), new Bool()) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);

            if (lhsV != null && rhsV != null) {
                Boolean lhsVB = (Boolean) lhsV;
                Boolean rhsVB = (Boolean) rhsV;
                return lhsVB == rhsVB;
            }
            return null;
        }
    },
    Implies("==>", List.of(Args.BOOL_BOOL), new Bool()) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);

            if (lhsV != null && rhsV != null) {
                Boolean lhsVB = (Boolean) lhsV;
                Boolean rhsVB = (Boolean) rhsV;
                return !(lhsVB && !rhsVB);
            }
            return null;
        }
    },
    ReverseImplies("<==", List.of(Args.BOOL_BOOL), new Bool()) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);

            if (lhsV != null && rhsV != null) {
                Boolean lhsVB = (Boolean) lhsV;
                Boolean rhsVB = (Boolean) rhsV;
                return !(!lhsVB && rhsVB);
            }
            return null;
        }
    },
    And("&&", List.of(Args.BOOL_BOOL), new Bool()) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);

            if (lhsV != null && rhsV != null) {
                Boolean lhsVB = (Boolean) lhsV;
                Boolean rhsVB = (Boolean) rhsV;
                return lhsVB && rhsVB;
            }
            return null;
        }
    },
    Or("||", List.of(Args.BOOL_BOOL), new Bool()) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);

            if (lhsV != null && rhsV != null) {
                Boolean lhsVB = (Boolean) lhsV;
                Boolean rhsVB = (Boolean) rhsV;
                return lhsVB || rhsVB;
            }
            return null;
        }
    },
    Equals("==", List.of(Args.INT_INT, Args.BOOL_BOOL, Args.CHAR_CHAR, Args.DSET_DSET, Args.MULTISET_MULTISET, Args.SEQ_SEQ), new Bool()) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);
            Type type = lhsE.getTypes().get(0);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);
            if (lhsV != null && rhsV != null) {
                return type.equal(lhsV, rhsV);
            }
            return null;
        }
    },
    Not_Equals("!=", List.of(Args.INT_INT, Args.BOOL_BOOL, Args.CHAR_CHAR, Args.DSET_DSET, Args.MULTISET_MULTISET, Args.SEQ_SEQ), new Bool()) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);
            Type type = lhsE.getTypes().get(0);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);
            if (lhsV != null && rhsV != null) {
                return !type.equal(lhsV, rhsV);
            }
            return null;
        }
    },
    Less_Than("<", List.of(Args.INT_INT, Args.REAL_REAL, Args.CHAR_CHAR, Args.DSET_DSET, Args.MULTISET_MULTISET, Args.SEQ_SEQ), new Bool()) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);
            Type type = lhsE.getTypes().get(0);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);


            if (lhsV != null && rhsV != null) {
                return type.lessThan(lhsV, rhsV);
            }
            return null;
        }
    },
    Less_Than_Or_Equal("<=", List.of(Args.INT_INT, Args.REAL_REAL, Args.CHAR_CHAR, Args.DSET_DSET, Args.MULTISET_MULTISET, Args.SEQ_SEQ), new Bool()) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);
            Type type = lhsE.getTypes().get(0);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);


            if (lhsV != null && rhsV != null) {
                return type.lessThanOrEqual(lhsV, rhsV);
            }
            return null;
        }
    },
    Greater_Than(">", List.of(Args.INT_INT, Args.REAL_REAL, Args.CHAR_CHAR, Args.DSET_DSET, Args.MULTISET_MULTISET), new Bool()) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);
            Type type = lhsE.getTypes().get(0);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);


            if (lhsV != null && rhsV != null) {
                return type.greaterThan(lhsV, rhsV);
            }
            return null;
        }
    },
    Greater_Than_Or_Equal(">=", List.of(Args.INT_INT, Args.REAL_REAL, Args.CHAR_CHAR, Args.DSET_DSET, Args.MULTISET_MULTISET), new Bool()) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);
            Type type = lhsE.getTypes().get(0);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);


            if (lhsV != null && rhsV != null) {
                return type.greaterThanOrEqual(lhsV, rhsV);
            }
            return null;
        }
    },
    Plus("+", List.of(Args.INT_INT), new Int()) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);

            if (lhsV != null && rhsV != null) {
                Integer lhsVI = (Integer) lhsV;
                Integer rhsVI = (Integer) rhsV;
                return lhsVI + rhsVI;
            }
            return null;
        }
    },
    Minus("-", List.of(Args.INT_INT), new Int()) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);

            if (lhsV != null && rhsV != null) {
                Integer lhsVI = (Integer) lhsV;
                Integer rhsVI = (Integer) rhsV;
                return lhsVI - rhsVI;
            }
            return null;
        }
    },
    Times("*", List.of(Args.INT_INT), new Int()) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);

            if (lhsV != null && rhsV != null) {
                Integer lhsVI = (Integer) lhsV;
                Integer rhsVI = (Integer) rhsV;
                return lhsVI * rhsVI;
            }
            return null;
        }
    },
    Divide("/", List.of(Args.INT_INT), new Int()) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);

            if (lhsV != null && rhsV != null) {
                Integer lhsVI = (Integer) lhsV;
                Integer rhsVI = (Integer) rhsV;

                int x = lhsVI;
                int y = rhsVI;

                int r = x / y;

                if (x < 0 && r * y != x) {
                    r -= y > 0 ? 1 : -1;
                }
                return r;

            }
            return null;
        }
    },
    Modulus("%", List.of(Args.INT_INT), new Int()) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);

            if (lhsV != null && rhsV != null) {
                Integer lhsVI = (Integer) lhsV;
                Integer rhsVI = (Integer) rhsV;

                Integer r = (Integer) Divide.apply(args, paramsMap);
                return lhsVI - r * rhsVI;
//                return lhsVI % rhsVI;
            }
            return null;
        }
    },
    Disjoint("!!", List.of(Args.DSET_DSET, Args.MULTISET_MULTISET), new Bool()) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);
            DCollection type = (DCollection) lhsE.getTypes().get(0);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);


            if (lhsV != null && rhsV != null) {
                return type.disjoint(lhsV, rhsV);
            }
            return null;
        }
    },
    Union("+", List.of(Args.DSET_DSET, Args.MULTISET_MULTISET, Args.SEQ_SEQ), List.of(new DSet(), new Seq(), new Multiset())) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);
            DCollection type = (DCollection) lhsE.getTypes().get(0);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);


            if (lhsV != null && rhsV != null) {
                return type.union(lhsV, rhsV);
            }
            return null;
        }

        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable,
            Type expected) {
            List<Type> ret = new ArrayList<>();
            for (Type ignored : types) {
                ret.add(expected.concrete(symbolTable));
            }
            return ret;
        }
    },
    Difference("-", List.of(Args.DSET_DSET, Args.MULTISET_MULTISET), List.of(new DSet(), new Multiset())) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);
            DCollection type = (DCollection) lhsE.getTypes().get(0);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);


            if (lhsV != null && rhsV != null) {
                return type.difference(lhsV, rhsV);
            }
            return null;
        }

        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable,
            Type expected) {
            List<Type> ret = new ArrayList<>();
            for (Type ignored : types) {
                ret.add(expected.concrete(symbolTable));
            }
            return ret;
        }
    },
    Intersection("*", List.of(Args.DSET_DSET, Args.MULTISET_MULTISET), List.of(new DSet(), new Multiset())) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);
            DCollection type = (DCollection) lhsE.getTypes().get(0);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);


            if (lhsV != null && rhsV != null) {
                return type.intersection(lhsV, rhsV);
            }
            return null;
        }

        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable,
            Type expected) {
            List<Type> ret = new ArrayList<>();
            for (Type ignored : types) {
                ret.add(expected.concrete(symbolTable));
            }
            return ret;
        }
    },
    Membership("in", List.of(Args.SEQ, Args.DSET, Args.MULTISET), new Bool()) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);
            DCollection type = (DCollection) rhsE.getTypes().get(0);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);

            if (lhsV != null && rhsV != null) {
                return type.contains(lhsV, rhsV);
            }
            return null;
        }

        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable,
            Type expected) {
            RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
            Type t = typeGenerator.generateTypes(1, symbolTable).get(0);
            List<Type> ret = new ArrayList<>();
            ret.add(t);
            DCollection collection = (DCollection) types.get(0);
            ret.add(collection.setInnerType(t));
            return ret;
        }
    },
    NotMembership("!in", List.of(Args.SEQ, Args.DSET, Args.MULTISET), new Bool()) {
        @Override
        public Object apply(List<Expression> args, Map<Variable, Variable> paramsMap) {
            Expression lhsE = args.get(0);
            Expression rhsE = args.get(1);
            DCollection type = (DCollection) rhsE.getTypes().get(0);

            Object lhsV = lhsE.getValue(paramsMap).get(0);
            Object rhsV = rhsE.getValue(paramsMap).get(0);

            if (lhsV != null && rhsV != null) {
                return !type.contains(lhsV, rhsV);
            }
            return null;
        }
        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable,
            Type expected) {
            RandomTypeGenerator typeGenerator = new RandomTypeGenerator();
            Type t = typeGenerator.generateTypes(1, symbolTable).get(0);
            List<Type> ret = new ArrayList<>();
            ret.add(t);
            DCollection collection = (DCollection) types.get(0);
            ret.add(collection.setInnerType(t));
            return ret;
        }
    },

    ;

    private final String operator;
    private final List<List<Type>> typeArgs;
    private final List<Type> retTypes;

    BinaryOperator(String operator, List<List<Type>> typeArgs, Type retType) {
        this.operator = operator;
        this.typeArgs = typeArgs;
        this.retTypes = List.of(retType);
    }

    BinaryOperator(String operator, List<List<Type>> typeArgs, List<Type> retType) {
        this.operator = operator;
        this.typeArgs = typeArgs;
        this.retTypes = retType;
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
        return 2;
    }
}
