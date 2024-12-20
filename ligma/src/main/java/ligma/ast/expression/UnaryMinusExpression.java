package ligma.ast.expression;

import ligma.enums.DataType;
import ligma.enums.Operator;
import lombok.Getter;

@Getter
public class UnaryMinusExpression extends Expression {

    private final Operator operator;
    private final Expression expression;

    public UnaryMinusExpression(Operator operator, Expression expression, DataType type, int line) {
        super(type, line);
        this.operator = operator;
        this.expression = expression;
    }

}
