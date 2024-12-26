package ligma.ir.expression;

import ligma.enums.DataType;
import ligma.enums.Operator;
import lombok.Getter;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a logical expression with an operator and two operands (left and right).
@Getter
public class LogicalExpression extends Expression {

    /// The operator used in the logical expression (e.g., `&&`, `||`, etc.).
    private final Operator operator;
    /// The left operand of the logical expression.
    private final Expression left;
    /// The right operand of the logical expression.
    private final Expression right;

    public LogicalExpression(Operator operator, Expression left, Expression right, DataType type) {
        super(type);
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

}
