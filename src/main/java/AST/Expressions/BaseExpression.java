package AST.Expressions;

import AST.SymbolTable.Types.Variables.Variable;
import java.util.List;
import java.util.Map;

public abstract class BaseExpression implements Expression {

  private int useFreq;

  public BaseExpression() {
    this.useFreq = 0;
  }

  @Override
  public void incrementUse() {
    this.useFreq++;
  }

  @Override
  public int getNoOfUses() {
    return useFreq;
  }

  @Override
  public List<Object> getValue(Map<Variable, Variable> paramMap, StringBuilder s) {
    incrementUse();
    return getValue(paramMap, s, true);
  }

  protected abstract List<Object> getValue(Map<Variable, Variable> paramMap, StringBuilder s,
    boolean unused);

}
