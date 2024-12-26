package ligma.ir.function;

import ligma.ir.expression.Expression;

import java.util.List;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Interace shared between different function calls (statement and expression)
public interface Callable {

    /// Returns the identifier (name) of the callable.
    String getIdentifier();
    /// Returns the list of arguments passed to the callable.
    List<Expression> getArguments();

}