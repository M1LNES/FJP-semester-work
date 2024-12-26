package ligma.ir.expression;

import ligma.enums.DataType;
import ligma.enums.Operator;
import lombok.Getter;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a "not" expression with an operator and an operand.
@Getter
public class NotExpression extends Expression {

    /// The operator used in the "not" expression (e.g., `!`).
    private final Operator operator;
    /// The operand of the "not" expression.
    private final Expression expression;

    public NotExpression(Operator operator, Expression expression, DataType type) {
        super(type);
        this.operator = operator;
        this.expression = expression;
    }

}
