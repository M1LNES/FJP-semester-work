package ligma.ir.statement;

import ligma.ir.expression.Expression;
import ligma.ir.function.Callable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a function call statement, including the function identifier and a list of arguments.
@Getter
@RequiredArgsConstructor
public class FunctionCall extends Statement implements Callable {

    /// The identifier (name) of the function being called.
    private final String identifier;
    /// The list of arguments passed to the function in the call.
    private final List<Expression> arguments;

}
