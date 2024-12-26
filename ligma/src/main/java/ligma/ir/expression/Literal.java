package ligma.ir.expression;

import ligma.enums.DataType;
import lombok.Getter;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a literal expression with a value of generic type `T` and a data type.
@Getter
public class Literal<T> extends Expression {

    /// The value of the literal expression.
    private final T value;

    public Literal(T value, DataType type) {
        super(type);
        this.value = value;
    }

}
