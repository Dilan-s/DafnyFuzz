package AST.Statements.util;

import java.util.List;

public enum ReturnStatus {

  UNKNOWN {
    @Override
    public boolean stop() {
      return false;
    }
  },
  RETURN,
  BREAK,
  CONTINUE,
  ;

  private List<Object> values;
  private int depth;

  ReturnStatus() {
    this.values = null;
    this.depth = 0;
  }

  public static ReturnStatus breakWithDepth(int depth) {
    ReturnStatus breakStatus = BREAK;
    breakStatus.setDepth(depth);
    return breakStatus;
  }

  public static ReturnStatus returnValues(List<Object> values) {
    ReturnStatus returnStatus = RETURN;
    returnStatus.set(values);
    return returnStatus;
  }

  private void set(List<Object> values) {
    this.values = values;
  }

  public boolean stop() {
    return true;
  }

  public List<Object> getValues() {
    return values;
  }

  public int getDepth() {
    return depth;
  }

  private void setDepth(int depth) {
    this.depth = depth;
  }
}
