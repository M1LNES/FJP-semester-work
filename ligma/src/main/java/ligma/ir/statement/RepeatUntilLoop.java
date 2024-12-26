package ligma.ir.statement;

import ligma.ir.expression.Expression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a repeat-until loop statement, including the list of statements and the loop condition.
@Getter
@RequiredArgsConstructor
public class RepeatUntilLoop extends Statement {

    /// The list of statements inside the repeat-until loop.
    private final List<Statement> statements;
    /// The condition expression for the repeat-until loop.
    private final Expression expression;

}
