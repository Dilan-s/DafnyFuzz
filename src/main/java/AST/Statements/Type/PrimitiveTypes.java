package AST.Statements.Type;

import AST.StringUtils.Constants;

public enum PrimitiveTypes implements Type {
    INT("int"),
    BOOL("bool"),
    VOID {
        @Override
        public String getTypeIndicatorString() {
            return "";
        }
    };

    private final String representation;

    PrimitiveTypes(String representation) {
        this.representation = representation;
    }

    PrimitiveTypes() {
        this(null);
    }

    @Override
    public String getTypeIndicatorString() {
        StringBuilder indicator = new StringBuilder();
        indicator.append(Constants.TYPE_INDICATOR);
        indicator.append(representation);
        return indicator.toString();
    }
}
