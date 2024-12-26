package ligma.ir.program;

import ligma.ir.function.Function;
import ligma.ir.statement.Statement;

import java.util.List;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a program consisting of a list of statements and functions.
/// @param statements The list of statements in the program.
/// @param functions The list of functions in the program.
public record Program(
    List<Statement> statements,
    List<Function> functions
) {

}
