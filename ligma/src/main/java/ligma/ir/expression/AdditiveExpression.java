package ligma.ir.expression;

import ligma.enums.DataType;
import ligma.enums.Operator;
import lombok.Getter;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents an additive expression with two operands and an operator.
@Getter
public class AdditiveExpression extends Expression {

    /// The operator used in the additive expression (e.g., +, -)
    private final Operator operator;
    /// The left-hand operand of the expression
    private final Expression left;
    /// The right-hand operand of the expression
    private final Expression right;

    public AdditiveExpression(Operator operator, Expression left, Expression right, DataType type) {
        super(type);
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

}
