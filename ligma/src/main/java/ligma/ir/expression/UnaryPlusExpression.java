package ligma.ir.expression;

import ligma.enums.DataType;
import ligma.enums.Operator;
import lombok.Getter;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a unary plus expression with an operator and a single operand.
@Getter
public class UnaryPlusExpression extends Expression {

    /// The operator used in the unary plus expression (e.g., `+`).
    private final Operator operator;
    /// The operand of the unary plus expression.
    private final Expression expression;

    public UnaryPlusExpression(Operator operator, Expression expression, DataType type) {
        super(type);
        this.operator = operator;
        this.expression = expression;
    }

}
