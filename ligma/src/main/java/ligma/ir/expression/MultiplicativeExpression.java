package ligma.ir.expression;

import ligma.enums.DataType;
import ligma.enums.Operator;
import lombok.Getter;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a multiplicative expression with an operator and two operands (left and right).
@Getter
public class MultiplicativeExpression extends Expression {

    /// The operator used in the multiplicative expression (e.g., `*`, `/`, etc.).
    private final Operator operator;
    /// The left operand of the multiplicative expression.
    private final Expression left;
    /// The right operand of the multiplicative expression.
    private final Expression right;

    public MultiplicativeExpression(Operator operator, Expression left, Expression right, DataType type) {
        super(type);
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

}
