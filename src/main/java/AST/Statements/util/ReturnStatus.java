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

    ReturnStatus() {
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
}
