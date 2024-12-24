package ligma.ast.expression;

import ligma.enums.DataType;
import ligma.enums.Operator;
import lombok.Getter;

@Getter
public class LogicalExpression extends Expression {

    private final Operator operator;
    private final Expression left;
    private final Expression right;

    public LogicalExpression(Operator operator, Expression left, Expression right, DataType type) {
        super(type);
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

}
