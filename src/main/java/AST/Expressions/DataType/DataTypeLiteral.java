package AST.Expressions.DataType;

import AST.Generator.GeneratorConfig;
import AST.Generator.VariableNameGenerator;
import AST.Statements.AssignmentStatement;
import AST.Expressions.BaseExpression;
import AST.Expressions.Expression;
import AST.Statements.Statement;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.DataType.DataTypeRule;
import AST.SymbolTable.Types.Variables.Variable;
import AST.SymbolTable.Types.Variables.VariableDataTypeIndex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DataTypeLiteral extends BaseExpression {

    private SymbolTable symbolTable;
    private Type type;
    private List<Expression> fields;

    private final Variable variable;
    private Statement statement;

    private List<List<Statement>> expanded;

    public DataTypeLiteral(SymbolTable symbolTable, Type type, List<Expression> fields) {
        super();
        this.symbolTable = symbolTable;
        this.type = type;
        this.fields = fields;

        this.variable = new Variable(VariableNameGenerator.generateVariableValueName(type, symbolTable), type);
        this.statement = new AssignmentStatement(symbolTable, List.of(variable), new DataTypeLiteralValues(fields));

        this.expanded = new ArrayList<>();
        fields.forEach(f -> expanded.add(f.expand()));
        expanded.add(statement.expand());

        generateAssignments();
    }

    private void generateAssignments() {
        DataTypeRule t = (DataTypeRule) type;
        List<Type> fieldTypes = t.getFieldTypes();
        List<String> fieldNames = t.getFieldNames();
        for (int i = 0; i < fieldTypes.size(); i++) {
            VariableDataTypeIndex v = new VariableDataTypeIndex(variable, fieldTypes.get(i), fieldNames.get(i), i);
            v.setDeclared();
            v.setConstant();
            symbolTable.addVariable(v);
        }
    }

    @Override
    protected List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s, boolean unused) {
        return variable.getValue(paramsMap);
    }

    @Override
    public List<Type> getTypes() {
        return List.of(type);
    }

    @Override
    public boolean requireUpdate() {
        return fields.stream().anyMatch(Expression::requireUpdate) || statement.requireUpdate();
    }

    @Override
    public List<Statement> expand() {
        int i;
        for (i = 0; i < fields.size(); i++) {
            Expression field = fields.get(i);
            if (field.requireUpdate()) {
                expanded.set(i, field.expand());
            }
        }
        if (statement.requireUpdate()) {
            expanded.set(i, statement.expand());
        }
        return expanded.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public boolean validForFunction() {
        return fields.stream().anyMatch(Expression::validForFunction) || statement.validForFunction();
    }

    @Override
    public String toString() {
        return variable.getName();
    }

    private class DataTypeLiteralValues extends BaseExpression {

        private final List<Expression> fields;

        private DataTypeLiteralValues(List<Expression> fields) {
            super();
            this.fields = fields;
        }


        @Override
        protected List<Object> getValue(Map<Variable, Variable> paramsMap, StringBuilder s, boolean unused) {
            List<Object> r = new ArrayList<>();
            List<Object> l = new ArrayList<>();
            for (int i = 0; i < fields.size(); i++) {
                Expression exp = fields.get(i);
                List<Object> value = exp.getValue(paramsMap, s);
                for (Object v : value) {
                    if (v == null) {
                        r.add(null);
                        return r;
                    }
                    l.add(v);
                }
            }
            r.add(new DataTypeValue(type, l));
            return r;
        }

        @Override
        public String minimizedTestCase() {
            if (fields.isEmpty()) {
                return type.getName();
            }
            String values = fields.stream()
                .map(Expression::minimizedTestCase)
                .collect(Collectors.joining(", "));
            return String.format("%s(%s)", type.getName(), values);
        }

        @Override
        public String toString() {
            if (fields.isEmpty()) {
                return type.getName();
            }
            String values = fields.stream()
                .map(Expression::toString)
                .collect(Collectors.joining(", "));
            return String.format("%s(%s)", type.getName(), values);
        }

        @Override
        public List<String> toOutput() {
            Set<String> res = new HashSet<>();
            if (fields.isEmpty()) {
                res.add(type.getName());
                return new ArrayList<>(res);
            }
            res.add(String.format("%s(", type.getName()));
            boolean first = true;

            List<String> temp = new ArrayList<>();
            for (Expression e : fields) {
                temp = new ArrayList<>();

                List<String> expOptions = e.toOutput();
                for (String expOption : expOptions) {
                    for (String f : res) {
                        String curr = f;
                        if (!first) {
                            curr = curr + ", ";
                        }
                        curr = curr + expOption;
                        temp.add(curr);
                    }
                }
                if (expOptions.isEmpty()) {
                    temp.addAll(res);
                }
                Collections.shuffle(temp, GeneratorConfig.getRandom());
                res = new HashSet<>(temp.subList(0, Math.min(5, temp.size())));
                first = false;
            }

            temp = new ArrayList<>();
            for (String f : res) {
                String curr = f + ")";
                temp.add(curr);
            }
            res = new HashSet<>(temp);

            List<String> r = new ArrayList<>(res);
            Collections.shuffle(r, GeneratorConfig.getRandom());
            return r.subList(0, Math.min(5, r.size()));
        }

        @Override
        public List<Type> getTypes() {
            return List.of(type);
        }

        @Override
        public List<Statement> expand() {
            return new ArrayList<>();
        }

        @Override
        public boolean requireUpdate() {
            return false;
        }
    }
}
