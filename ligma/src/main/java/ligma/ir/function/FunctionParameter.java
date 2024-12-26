package ligma.ir.function;

import ligma.enums.DataType;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a parameter for a function, including its type and name.
/// @param name The name of the function parameter.
/// @param type The type of the function parameter.
public record FunctionParameter(
    DataType type,
    String name
) {

}
