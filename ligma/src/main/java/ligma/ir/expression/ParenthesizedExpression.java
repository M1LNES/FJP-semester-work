package ligma.ir.expression;

import ligma.enums.DataType;
import lombok.Getter;

@Getter
public class ParenthesizedExpression extends Expression {

    private final Expression expression;

    public ParenthesizedExpression(Expression expression, DataType type) {
        super(type);
        this.expression = expression;
    }

}
