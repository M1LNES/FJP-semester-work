package ligma.ir.statement;

import ligma.ir.expression.Expression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents an if statement with a condition, a list of statements for the 'if' branch, and a list of statements for the 'else' branch.
@Getter
@RequiredArgsConstructor
public class IfStatement extends Statement {

    /// The condition expression for the if statement.
    private final Expression expression;
    /// The list of statements executed if the condition is true.
    private final List<Statement> ifStatements;
    /// The list of statements executed if the condition is false (empty if there is no 'else' branch).
    private final List<Statement> elseStatements;

}
