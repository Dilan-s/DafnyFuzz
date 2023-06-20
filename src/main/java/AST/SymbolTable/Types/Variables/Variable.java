package AST.SymbolTable.Types.Variables;

import AST.Expressions.Array.ArrayValue;
import AST.Expressions.DataType.DataTypeValue;
import AST.SymbolTable.Identifier;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.DCollectionTypes.DArray;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.DataType.DataTypeRule;
import AST.SymbolTable.Types.UserDefinedTypes.Tuple;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Variable implements Identifier {

    private final String name;
    protected Type type;
    private boolean isConstant;
    private boolean isDeclared;
    private Object value;


    public Variable(String name, Type type) {
        this.name = name;
        this.type = type;
        this.isConstant = false;
        this.isDeclared = false;
        this.value = null;
    }

    public void setConstant() {
        isConstant = true;
    }

    public boolean isConstant() {
        return isConstant;
    }

    @Override
    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public void setDeclared() {
        isDeclared = true;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", getName(), getType().getVariableType());
    }

    public boolean isDeclared() {
        return isDeclared;
    }

    public List<Object> getValue() {
        return getValue(new HashMap<>());
    }

    public List<Object> getValue(Map<Variable, Variable> paramsMap) {
        if (paramsMap.containsKey(this)) {
            return paramsMap.get(this).getValue(paramsMap);
        }
        List<Object> l = new ArrayList<>();
        l.add(value == null ? null : type.of(value));
        return l;
    }

    public void setValue(SymbolTable symbolTable, Map<Variable, Variable> paramMap, Object value) {
        if (paramMap.containsKey(this)) {
            paramMap.get(this).setValue(symbolTable, paramMap, value);
        }
        Object o = getValue(paramMap).get(0);
        if (o != null) {
            List<Variable> remove = new ArrayList<>();
            List<Variable> replace = new ArrayList<>();
            if (type.equals(new DArray())) {
                DArray dArray = (DArray) this.type;
                ArrayValue prevV = (ArrayValue) o;
                for (int i = 0; i < prevV.size(); i++) {
                    VariableArrayIndex variableArrayIndex = new VariableArrayIndex(this, dArray.getInnerType(), i);
                    remove.add(variableArrayIndex);
                }
                ArrayValue newV = (ArrayValue) value;
                for (int i = 0; i < newV.size(); i++) {
                    VariableArrayIndex variableArrayIndex = new VariableArrayIndex(this, dArray.getInnerType(), i);
                    replace.add(variableArrayIndex);
                }
            } else if (type.equals(new DataTypeRule())) {
                DataTypeValue newV = (DataTypeValue) value;
                DataTypeValue prevV = (DataTypeValue) o;

                DataTypeRule dataTypeRule = (DataTypeRule) prevV.getType();
                List<Type> fieldTypes = dataTypeRule.getFieldTypes();
                List<String> fieldNames = dataTypeRule.getFieldNames();

                for (int i = 0; i < fieldTypes.size(); i++) {
                    VariableDataTypeIndex variableDataTypeIndex = new VariableDataTypeIndex(this, fieldTypes.get(i), fieldNames.get(i), i);
                    remove.add(variableDataTypeIndex);
                }

                dataTypeRule = (DataTypeRule) newV.getType();
                this.type = dataTypeRule;
                fieldTypes = dataTypeRule.getFieldTypes();
                fieldNames = dataTypeRule.getFieldNames();

                for (int i = 0; i < fieldTypes.size(); i++) {
                    VariableDataTypeIndex variableDataTypeIndex = new VariableDataTypeIndex(this, fieldTypes.get(i), fieldNames.get(i), i);
                    replace.add(variableDataTypeIndex);
                }

            }
            symbolTable.replaceVariables(remove, replace);
        }
        if (type.equals(new DataTypeRule())) {
            DataTypeValue newV = (DataTypeValue) value;
            this.type = newV.getType();
        }

        this.value = value;
    }

    public List<Variable> getSymbolTableArgs() {
        List<Variable> vars = new ArrayList<>();
        if (type.equals(new DArray())) {
            DArray dArray = (DArray) this.type;
            for (int i = 0; i < DArray.MIN_SIZE_OF_ARRAY; i++) {
                vars.addAll(new VariableArrayIndex(this, dArray.getInnerType(), i).getSymbolTableArgs());
            }
        } else if (type.equals(new Tuple())) {
            Tuple tuple = (Tuple) this.type;
            for (int i = 0; i < tuple.getNoOfType(); i++) {
                vars.addAll(new VariableTupleIndex(this, tuple.getType(i), i).getSymbolTableArgs());
            }
        } else if (type.equals(new DataTypeRule())) {
            DataTypeRule dataTypeRule = (DataTypeRule) this.type;
            List<Type> fieldTypes = dataTypeRule.getFieldTypes();
            List<String> fieldNames = dataTypeRule.getFieldNames();
            for (int i = 0; i < fieldTypes.size(); i++) {
                vars.addAll(new VariableDataTypeIndex(this, fieldTypes.get(i), fieldNames.get(i), i).getSymbolTableArgs());
            }
        }
        vars.add(this);
        return vars;
    }

    public boolean modified(Variable x) {
        return false;
    }

    public List<Variable> getRelatedAssignment() {
        return List.of(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Variable)) {
            return false;
        }
        Variable other = (Variable) obj;
        return other.name.equals(name) && other.type.equals(type);
    }
}
