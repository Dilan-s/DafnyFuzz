package AST.StringUtils;

import java.util.Collections;

public class IndentedStringBuilder {

    private final StringBuilder stringBuilder;
    private int indentationLevel;

    public IndentedStringBuilder() {
        this.stringBuilder = new StringBuilder();
        this.indentationLevel = 0;
    }

    public void append(String s) {
        if (!s.isEmpty()) {
            stringBuilder.append(String.join("", Collections.nCopies(indentationLevel, "\t")));
            stringBuilder.append(s);
        }
    }

    public void indent() throws IndentationLevelException {
        indentationLevel++;
        if (indentationLevel < 0) {
            throw new IndentationLevelException();
        }
    }

    public void unindent() throws IndentationLevelException {
        indentationLevel--;
        if (indentationLevel < 0) {
            throw new IndentationLevelException();
        }
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}
