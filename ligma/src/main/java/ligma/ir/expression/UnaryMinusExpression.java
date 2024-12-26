package ligma.ir.expression;

import ligma.enums.DataType;
import ligma.enums.Operator;
import lombok.Getter;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a unary minus expression with an operator and a single operand.
@Getter
public class UnaryMinusExpression extends Expression {

    /// The operator used in the unary minus expression (e.g., `-`).
    private final Operator operator;
    /// The operand of the unary minus expression.
    private final Expression expression;

    public UnaryMinusExpression(Operator operator, Expression expression, DataType type) {
        super(type);
        this.operator = operator;
        this.expression = expression;
    }

}
