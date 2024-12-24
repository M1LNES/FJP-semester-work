package ligma.ast.expression;

import ligma.enums.DataType;
import ligma.enums.Operator;
import lombok.Getter;

@Getter
public class UnaryMinusExpression extends Expression {

    private final Operator operator;
    private final Expression expression;

    public UnaryMinusExpression(Operator operator, Expression expression, DataType type) {
        super(type);
        this.operator = operator;
        this.expression = expression;
    }

}
