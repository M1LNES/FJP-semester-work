package ligma.ast.expression;

import ligma.enums.DataType;
import lombok.Getter;

@Getter
public class ParenthesizedExpression extends Expression {

    private final Expression expression;

    public ParenthesizedExpression(Expression expression, DataType type, int line) {
        super(type, line);
        this.expression = expression;
    }

}
