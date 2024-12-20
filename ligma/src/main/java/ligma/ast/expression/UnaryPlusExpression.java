package ligma.ast.expression;

import ligma.enums.DataType;
import ligma.enums.Operator;
import lombok.Getter;

@Getter
public class UnaryPlusExpression extends Expression {

    private final Operator operator;
    private final Expression expression;

    public UnaryPlusExpression(Operator operator, Expression expression, DataType type, int line) {
        super(type, line);
        this.operator = operator;
        this.expression = expression;
    }

}
