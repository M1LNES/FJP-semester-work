package ligma.ast.expression;

import ligma.enums.DataType;
import ligma.enums.Operator;
import lombok.Getter;

@Getter
public class NotExpression extends Expression {

    private final Operator operator;
    private final Expression expression;

    public NotExpression(Operator operator, Expression expression, DataType type) {
        super(type);
        this.operator = operator;
        this.expression = expression;
    }

}
