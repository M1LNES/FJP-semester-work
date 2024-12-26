package ligma.ir.statement;

import ligma.ir.expression.Expression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a for loop statement, including an identifier, expressions for the loop range, and the loop body.
@Getter
@RequiredArgsConstructor
public class ForLoop extends Statement {

    /// The identifier used in the for loop.
    private final String identifier;
    /// The expression that initializes the loop variable.
    private final Expression expression;
    /// The expression that defines the upper limit of the loop.
    private final Expression toExpression;
    /// The list of statements inside the for loop.
    private final List<Statement> statements;

}
