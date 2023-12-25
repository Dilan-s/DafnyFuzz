package AST.SymbolTable.Types.Variables;

import AST.Expressions.Array.ArrayValue;
import AST.Expressions.DClass.DClassValue;
import AST.Expressions.DataType.DataTypeValue;
import AST.SymbolTable.Identifier;
import AST.SymbolTable.SymbolTable.SymbolTable;
import AST.SymbolTable.Types.DCollectionTypes.DArray;
import AST.SymbolTable.Types.Type;
import AST.SymbolTable.Types.UserDefinedTypes.DClass;
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
  protected Object value;
  private boolean isConstant;
  private boolean isDeclared;


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
      List<Boolean> dec = new ArrayList<>();
      List<Boolean> con = new ArrayList<>();
      List<Variable> remove = new ArrayList<>();
      List<Variable> replace = new ArrayList<>();
      if (type.equals(new DArray())) {
        DArray dArray = this.type.asDArray();
        ArrayValue prevV = (ArrayValue) o;
        for (int i = 0; i < prevV.size(); i++) {
          Variable variableArrayIndex = new VariableArrayIndex(this,
            dArray.getInnerType(), i);
          remove.add(variableArrayIndex);
        }
        ArrayValue newV = (ArrayValue) value;
        for (int i = 0; i < newV.size(); i++) {
          VariableArrayIndex variableArrayIndex = new VariableArrayIndex(this,
            dArray.getInnerType(), i);
          replace.add(variableArrayIndex);
        }
      } else if (type.equals(new DClass())) {
        DClass dClass = this.type.asDClass();
        DClassValue prevV = (DClassValue) o;

        List<Type> fieldTypes = dClass.getFieldTypes();
        List<String> fieldNames = dClass.getFieldNames();
        List<Boolean> isConst = dClass.getIsConst();
        for (int i = 0; i < prevV.size(); i++) {
          Variable variableClassIndex = new VariableClassIndex(this, fieldTypes.get(i),
            fieldNames.get(i), i);
          remove.add(variableClassIndex);
        }
        DClassValue newV = (DClassValue) value;
        for (int i = 0; i < newV.size(); i++) {
          Variable variableClassIndex = new VariableClassIndex(this, fieldTypes.get(i),
            fieldNames.get(i), i);
          if (isConst.get(i)) {
            variableClassIndex.setConstant();
          }
          replace.add(variableClassIndex);
        }
      } else if (type.equals(new DataTypeRule())) {
        DataTypeValue newV = (DataTypeValue) value;
        DataTypeValue prevV = (DataTypeValue) o;

        DataTypeRule dataTypeRule = prevV.getType().asDataTypeRule();
        List<Type> fieldTypes = dataTypeRule.getFieldTypes();
        List<String> fieldNames = dataTypeRule.getFieldNames();

        for (int i = 0; i < fieldTypes.size(); i++) {
          Variable variableDataTypeIndex = new VariableDataTypeIndex(this, fieldTypes.get(i),
            fieldNames.get(i), i);
          variableDataTypeIndex = symbolTable.getVariable(variableDataTypeIndex);
          dec.add(variableDataTypeIndex == null || variableDataTypeIndex.isDeclared());
          con.add(variableDataTypeIndex == null || variableDataTypeIndex.isConstant());
          remove.add(variableDataTypeIndex);
        }

        dataTypeRule = newV.getType().asDataTypeRule();
        this.type = dataTypeRule;
        fieldTypes = dataTypeRule.getFieldTypes();
        fieldNames = dataTypeRule.getFieldNames();

        for (int i = 0; i < fieldTypes.size(); i++) {
          VariableDataTypeIndex variableDataTypeIndex = new VariableDataTypeIndex(this,
            fieldTypes.get(i), fieldNames.get(i), i);
          if (dec.get(i)) {
            variableDataTypeIndex.setDeclared();
          }
          if (con.get(i)) {
            variableDataTypeIndex.setConstant();
          }
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
      DArray dArray = this.type.asDArray();
      for (int i = 0; i < DArray.MIN_SIZE_OF_ARRAY; i++) {
        vars.addAll(new VariableArrayIndex(this, dArray.getInnerType(), i).getSymbolTableArgs());
      }
    } else if (type.equals(new Tuple())) {
      Tuple tuple = this.type.asTuple();
      for (int i = 0; i < tuple.getNoOfType(); i++) {
        vars.addAll(new VariableTupleIndex(this, tuple.getType(i), i).getSymbolTableArgs());
      }
    } else if (type.equals(new DataTypeRule())) {
      DataTypeRule dataTypeRule = this.type.asDataTypeRule();
      List<Type> fieldTypes = dataTypeRule.getFieldTypes();
      List<String> fieldNames = dataTypeRule.getFieldNames();
      for (int i = 0; i < fieldTypes.size(); i++) {
        vars.addAll(new VariableDataTypeIndex(this, fieldTypes.get(i), fieldNames.get(i),
          i).getSymbolTableArgs());
      }
    } else if (type.equals(new DClass())) {
      DClass dClass = type.asDClass();
      List<Type> fieldTypes = dClass.getFieldTypes();
      List<String> fieldNames = dClass.getFieldNames();
      List<Boolean> isConst = dClass.getIsConst();
      for (int i = 0; i < fieldTypes.size(); i++) {
        VariableClassIndex variableClassIndex = new VariableClassIndex(this,
          fieldTypes.get(i), fieldNames.get(i), i);
        if (isConst.get(i)) {
          variableClassIndex.setConstant();
        }
        vars.addAll(variableClassIndex.getSymbolTableArgs());
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
