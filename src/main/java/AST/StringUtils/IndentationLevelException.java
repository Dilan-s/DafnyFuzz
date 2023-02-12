package AST.StringUtils;

public class IndentationLevelException extends Exception {

    public IndentationLevelException() {
        super("Indentation level cannot be below 0");
    }

}
