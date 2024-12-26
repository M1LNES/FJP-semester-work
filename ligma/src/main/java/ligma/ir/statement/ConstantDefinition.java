package ligma.ir.statement;

import ligma.enums.DataType;
import ligma.ir.expression.Expression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a constant definition statement, including an identifier, type, and an expression.
@Getter
@RequiredArgsConstructor
public class ConstantDefinition extends Statement {

    /// The identifier of the constant being defined.
    private final String identifier;
    /// The data type of the constant.
    private final DataType type;
    /// The expression assigned to the constant.
    private final Expression expression;

}
