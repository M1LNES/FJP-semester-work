package ligma.ast.expression;

import ligma.enums.DataType;
import ligma.enums.Operator;
import lombok.Getter;

@Getter
public class AdditiveExpression extends Expression {

    private final Operator operator;
    private final Expression left;
    private final Expression right;

    public AdditiveExpression(Operator operator, Expression left, Expression right, DataType type, int line) {
        super(type, line);
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

}