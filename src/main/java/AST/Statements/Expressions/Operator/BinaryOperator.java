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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public enum BinaryOperator implements Operator {
    Equivalence("<==>", List.of(Args.BOOL_BOOL), new Bool()) {
        @Override
        public void apply(Type type, List<Expression> args) {
            Bool bool = (Bool) type;

            Expression lhs = args.get(0);
            Bool lhsT = (Bool) lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            Bool rhsT = (Bool) rhs.getTypes().get(0);

            Object lhsB = lhsT.getValue();
            Object rhsB = rhsT.getValue();
            bool.setValue(lhsB == null || rhsB == null ? null : Objects.equals(lhsB, rhsB));
        }
    },
    Implies("==>", List.of(Args.BOOL_BOOL), new Bool()) {
        @Override
        public void apply(Type type, List<Expression> args) {
            Bool bool = (Bool) type;

            Expression lhs = args.get(0);
            Bool lhsT = (Bool) lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            Bool rhsT = (Bool) rhs.getTypes().get(0);

            Boolean lhsB = (Boolean) lhsT.getValue();
            Boolean rhsB = (Boolean) rhsT.getValue();
            bool.setValue(lhsB == null || rhsB == null ? null : !(lhsB && !rhsB));
        }
    },
    ReverseImplies("<==", List.of(Args.BOOL_BOOL), new Bool()) {
        @Override
        public void apply(Type type, List<Expression> args) {
            Bool bool = (Bool) type;

            Expression lhs = args.get(0);
            Bool lhsT = (Bool) lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            Bool rhsT = (Bool) rhs.getTypes().get(0);

            Boolean lhsB = (Boolean) lhsT.getValue();
            Boolean rhsB = (Boolean) rhsT.getValue();
            bool.setValue(lhsB == null || rhsB == null ? null : !(!lhsB && rhsB));
        }
    },
    And("&&", List.of(Args.BOOL_BOOL), new Bool()) {
        @Override
        public void apply(Type type, List<Expression> args) {
            Bool bool = (Bool) type;

            Expression lhs = args.get(0);
            Bool lhsT = (Bool) lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            Bool rhsT = (Bool) rhs.getTypes().get(0);

            Boolean lhsB = (Boolean) lhsT.getValue();
            Boolean rhsB = (Boolean) rhsT.getValue();
            bool.setValue(lhsB == null || rhsB == null ? null : lhsB && rhsB);
        }
    },
    Or("||", List.of(Args.BOOL_BOOL), new Bool()) {
        @Override
        public void apply(Type type, List<Expression> args) {
            Bool bool = (Bool) type;

            Expression lhs = args.get(0);
            Bool lhsT = (Bool) lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            Bool rhsT = (Bool) rhs.getTypes().get(0);

            Boolean lhsB = (Boolean) lhsT.getValue();
            Boolean rhsB = (Boolean) rhsT.getValue();
            bool.setValue(lhsB == null || rhsB == null ? null : lhsB || rhsB);
        }
    },
    Equals("==", List.of(Args.INT_INT, Args.BOOL_BOOL, Args.CHAR_CHAR, Args.DSET_DSET, Args.MULTISET_MULTISET, Args.SEQ_SEQ), new Bool()) {
        @Override
        public void apply(Type type, List<Expression> args) {
            Bool bool = (Bool) type;

            Expression lhs = args.get(0);
            Type lhsT = lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            Type rhsT = rhs.getTypes().get(0);

            bool.setValue(lhsT.getValue() == null || rhsT.getValue() == null ? null : lhsT.equal(rhsT));
        }
    },
    Not_Equals("!=", List.of(Args.INT_INT, Args.BOOL_BOOL, Args.CHAR_CHAR, Args.DSET_DSET, Args.MULTISET_MULTISET, Args.SEQ_SEQ), new Bool()) {
        @Override
        public void apply(Type type, List<Expression> args) {
            Bool bool = (Bool) type;

            Expression lhs = args.get(0);
            Type lhsT = lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            Type rhsT = rhs.getTypes().get(0);

            bool.setValue(lhsT.getValue() == null || rhsT.getValue() == null ? null : !lhsT.equal(rhsT));
        }
    },
    Less_Than("<", List.of(Args.INT_INT, Args.REAL_REAL, Args.CHAR_CHAR, Args.DSET_DSET, Args.MULTISET_MULTISET, Args.SEQ_SEQ), new Bool()) {
        @Override
        public void apply(Type type, List<Expression> args) {
            Bool bool = (Bool) type;

            Expression lhs = args.get(0);
            Type lhsT = lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            Type rhsT = rhs.getTypes().get(0);

            bool.setValue(lhsT.getValue() == null || rhsT.getValue() == null ? null : lhsT.lessThan(rhsT));
        }
    },
    Less_Than_Or_Equal("<=", List.of(Args.INT_INT, Args.REAL_REAL, Args.CHAR_CHAR, Args.DSET_DSET, Args.MULTISET_MULTISET, Args.SEQ_SEQ), new Bool()) {
        @Override
        public void apply(Type type, List<Expression> args) {
            Bool bool = (Bool) type;

            Expression lhs = args.get(0);
            Type lhsT = lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            Type rhsT = rhs.getTypes().get(0);

            bool.setValue(lhsT.getValue() == null || rhsT.getValue() == null ? null : lhsT.lessThanOrEqual(rhsT));
        }
    },
    Greater_Than(">", List.of(Args.INT_INT, Args.REAL_REAL, Args.CHAR_CHAR, Args.DSET_DSET, Args.MULTISET_MULTISET), new Bool()) {
        @Override
        public void apply(Type type, List<Expression> args) {
            Bool bool = (Bool) type;

            Expression lhs = args.get(0);
            Type lhsT = lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            Type rhsT = rhs.getTypes().get(0);

            bool.setValue(lhsT.getValue() == null || rhsT.getValue() == null ? null : lhsT.greaterThan(rhsT));
        }
    },
    Greater_Than_Or_Equal(">=", List.of(Args.INT_INT, Args.REAL_REAL, Args.CHAR_CHAR, Args.DSET_DSET, Args.MULTISET_MULTISET), new Bool()) {
        @Override
        public void apply(Type type, List<Expression> args) {
            Bool bool = (Bool) type;

            Expression lhs = args.get(0);
            Type lhsT = lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            Type rhsT = rhs.getTypes().get(0);

            bool.setValue(lhsT.getValue() == null || rhsT.getValue() == null ? null : lhsT.greaterThanOrEqual(rhsT));
        }
    },
    Plus("+", List.of(Args.INT_INT), new Int()) {
        @Override
        public void apply(Type type, List<Expression> args) {
            Int i = (Int) type;

            Expression lhs = args.get(0);
            Int lhsT = (Int) lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            Int rhsT = (Int) rhs.getTypes().get(0);

            Integer lhsI = (Integer) lhsT.getValue();
            Integer rhsI = (Integer) rhsT.getValue();
            i.setValue(lhsI == null || rhsI == null ? null : lhsI + rhsI);
        }
    },
    Minus("-", List.of(Args.INT_INT), new Int()) {
        @Override
        public void apply(Type type, List<Expression> args) {
            Int i = (Int) type;

            Expression lhs = args.get(0);
            Int lhsT = (Int) lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            Int rhsT = (Int) rhs.getTypes().get(0);

            Integer lhsI = (Integer) lhsT.getValue();
            Integer rhsI = (Integer) rhsT.getValue();
            i.setValue(lhsI == null || rhsI == null ? null : lhsI - rhsI);
        }
    },
    Times("*", List.of(Args.INT_INT), new Int()) {
        @Override
        public void apply(Type type, List<Expression> args) {
            Int i = (Int) type;

            Expression lhs = args.get(0);
            Int lhsT = (Int) lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            Int rhsT = (Int) rhs.getTypes().get(0);

            Integer lhsI = (Integer) lhsT.getValue();
            Integer rhsI = (Integer) rhsT.getValue();
            i.setValue(lhsI == null || rhsI == null ? null : lhsI * rhsI);
        }
    },
    Divide("/", List.of(Args.INT_INT), new Int()) {
        @Override
        public void apply(Type type, List<Expression> args) {
            Int i = (Int) type;

            Expression lhs = args.get(0);
            Int lhsT = (Int) lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            Int rhsT = (Int) rhs.getTypes().get(0);

            Integer lhsI = (Integer) lhsT.getValue();
            Integer rhsI = (Integer) rhsT.getValue();
            i.setValue(lhsI == null || rhsI == null ? null : (rhsI != 0 ? lhsI / rhsI : lhsI));
        }
    },
    Modulus("%", List.of(Args.INT_INT), new Int()) {
        @Override
        public void apply(Type type, List<Expression> args) {
            Int i = (Int) type;

            Expression lhs = args.get(0);
            Int lhsT = (Int) lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            Int rhsT = (Int) rhs.getTypes().get(0);

            Integer lhsI = (Integer) lhsT.getValue();
            Integer rhsI = (Integer) rhsT.getValue();
            i.setValue(lhsI == null || rhsI == null ? null : rhsI != 0 ? lhsI % rhsI : lhsI);
        }
    },
    Disjoint("!!", List.of(Args.DSET_DSET, Args.MULTISET_MULTISET), new Bool()) {
        @Override
        public void apply(Type type, List<Expression> args) {
            Bool bool = (Bool) type;

            Expression lhs = args.get(0);
            DCollection lhsT = (DCollection) lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            DCollection rhsT = (DCollection) rhs.getTypes().get(0);

            bool.setValue(lhsT.getValue() == null || rhsT.getValue() == null ? null : lhsT.disjoint(rhsT));
        }
    },
    Union("+", List.of(Args.DSET_DSET, Args.MULTISET_MULTISET, Args.SEQ_SEQ), List.of(new DSet(), new Seq(), new Multiset())) {
        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable,
            Type expected) {
            List<Type> ret = new ArrayList<>();
            for (Type ignored : types) {
                ret.add(expected.concrete(symbolTable));
            }
            return ret;
        }

        @Override
        public void apply(Type type, List<Expression> args) {
            Expression lhs = args.get(0);
            DCollection lhsT = (DCollection) lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            DCollection rhsT = (DCollection) rhs.getTypes().get(0);

            if (type instanceof DCollection) {
                DCollection set = (DCollection) type;

                set.setValue(lhsT.getValue() == null || rhsT.getValue() == null ? null : lhsT.union(rhsT));
            }
        }
    },
    Difference("-", List.of(Args.DSET_DSET, Args.MULTISET_MULTISET), List.of(new DSet(), new Multiset())) {
        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable,
            Type expected) {
            List<Type> ret = new ArrayList<>();
            for (Type ignored : types) {
                ret.add(expected.concrete(symbolTable));
            }
            return ret;
        }

        @Override
        public void apply(Type type, List<Expression> args) {
            Expression lhs = args.get(0);
            DCollection lhsT = (DCollection) lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            DCollection rhsT = (DCollection) rhs.getTypes().get(0);

            if (type instanceof DSet) {
                DSet set = (DSet) type;

                DSet lhsSet = (DSet) lhsT;
                DSet rhsSet = (DSet) rhsT;
                set.setValue(lhsSet.getValue() == null || rhsSet.getValue() == null ? null : lhsSet.difference(rhsSet));
            } else if (type instanceof Multiset) {
                Multiset set = (Multiset) type;

                Multiset lhsMultiset = (Multiset) lhsT;
                Multiset rhsMultiset = (Multiset) rhsT;
                set.setValue(lhsMultiset.getValue() == null || rhsMultiset.getValue() == null ? null : lhsMultiset.difference(rhsMultiset));
            }
        }
    },
    Intersection("*", List.of(Args.DSET_DSET, Args.MULTISET_MULTISET), List.of(new DSet(), new Multiset())) {
        @Override
        public List<Type> concreteType(List<Type> types, SymbolTable symbolTable,
            Type expected) {
            List<Type> ret = new ArrayList<>();
            for (Type ignored : types) {
                ret.add(expected.concrete(symbolTable));
            }
            return ret;
        }

        @Override
        public void apply(Type type, List<Expression> args) {
            Expression lhs = args.get(0);
            DCollection lhsT = (DCollection) lhs.getTypes().get(0);

            Expression rhs = args.get(1);
            DCollection rhsT = (DCollection) rhs.getTypes().get(0);

            if (type instanceof DSet) {
                DSet set = (DSet) type;

                DSet lhsSet = (DSet) lhsT;
                DSet rhsSet = (DSet) rhsT;
                set.setValue(lhsSet.getValue() == null || rhsSet.getValue() == null ? null :lhsSet.intersection(rhsSet));
            } else if (type instanceof Multiset) {
                Multiset set = (Multiset) type;

                Multiset lhsMultiset = (Multiset) lhsT;
                Multiset rhsMultiset = (Multiset) rhsT;
                set.setValue(lhsMultiset.getValue() == null || rhsMultiset.getValue() == null ? null : lhsMultiset.intersection(rhsMultiset));
            }
        }
    },
    Membership("in", List.of(Args.SEQ, Args.DSET, Args.MULTISET), new Bool()) {
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

        @Override
        public void apply(Type type, List<Expression> args) {
            Bool bool = (Bool) type;

            Expression val = args.get(0);

            Expression col = args.get(1);
            DCollection colT = (DCollection) col.getTypes().get(0);

            bool.setValue(colT.getValue() == null ? null : colT.contains(val));
        }
    },
    NotMembership("!in", List.of(Args.SEQ, Args.DSET, Args.MULTISET), new Bool()) {
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

        @Override
        public void apply(Type type, List<Expression> args) {
            Bool bool = (Bool) type;

            Expression val = args.get(0);

            Expression col = args.get(1);
            DCollection colT = (DCollection) col.getTypes().get(0);

            bool.setValue(colT.getValue() == null ? null : !colT.contains(val));
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

    @Override
    public void apply(Type type, List<Expression> args) {
    }
}
