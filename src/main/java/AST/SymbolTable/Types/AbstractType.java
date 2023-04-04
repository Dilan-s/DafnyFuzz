package AST.SymbolTable.Types;

import AST.Statements.Expressions.Expression;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractType implements Type {

    private List<PotentialValue> value = new ArrayList<>();

    @Override
    public void setExpressionAndIndAndDependencies(Expression expression, int ind, List<Expression> dependencies) {
        this.value.add(new PotentialValue(expression, ind, dependencies));
    }

    private static class PotentialValue {
        Expression expression;
        Integer ind;
        List<Expression> dependencies;

        public PotentialValue(Expression expression, Integer ind,
            List<Expression> dependencies) {
            this.expression = expression;
            this.ind = ind;
            this.dependencies = dependencies;
        }
    }
}
