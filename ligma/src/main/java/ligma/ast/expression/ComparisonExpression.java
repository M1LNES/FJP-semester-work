package ligma.ast.expression;

import ligma.enums.DataType;
import ligma.enums.Operator;
import lombok.Getter;

@Getter
public class ComparisonExpression extends Expression {

    private final Operator operator;
    private final Expression left;
    private final Expression right;

    public ComparisonExpression(Operator operator, Expression left, Expression right, DataType type) {
        super(type);
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

}
