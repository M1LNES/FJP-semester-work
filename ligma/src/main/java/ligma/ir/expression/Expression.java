package ligma.ir.expression;

import ligma.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents an abstract class for an expression with a specific data type.
@Getter
@Setter
@AllArgsConstructor
public abstract class Expression {

    /// The data type of the expression.
    private DataType type;

}
