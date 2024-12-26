package ligma.ir.statement;

import ligma.enums.DataType;
import ligma.ir.expression.Expression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a variable definition statement, including an identifier, type, and an initial expression.
@Getter
@RequiredArgsConstructor
public class VariableDefinition extends Statement {

    /// The identifier of the variable being defined.
    private final String identifier;
    /// The data type of the variable.
    private final DataType type;
    /// The expression assigned to the variable.
    private final Expression expression;

}
