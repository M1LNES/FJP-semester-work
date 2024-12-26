package ligma.ir.expression;

import ligma.enums.DataType;
import lombok.Getter;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a parenthesized expression with a single operand inside parentheses.
@Getter
public class ParenthesizedExpression extends Expression {

    /// The expression inside the parentheses.
    private final Expression expression;

    public ParenthesizedExpression(Expression expression, DataType type) {
        super(type);
        this.expression = expression;
    }

}
