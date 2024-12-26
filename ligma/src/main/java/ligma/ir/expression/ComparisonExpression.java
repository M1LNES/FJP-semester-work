package ligma.ir.expression;

import ligma.enums.DataType;
import ligma.enums.Operator;
import lombok.Getter;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a comparison expression with two operands and an operator.
@Getter
public class ComparisonExpression extends Expression {

    /// The operator used in the comparison (e.g., `==`, `!=`, etc.).
    private final Operator operator;
    /// The left operand of the comparison expression.
    private final Expression left;
    /// The right operand of the comparison expression.
    private final Expression right;

    public ComparisonExpression(Operator operator, Expression left, Expression right, DataType type) {
        super(type);
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

}
