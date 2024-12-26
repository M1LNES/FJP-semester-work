package ligma.ir.statement;

import ligma.ir.expression.Expression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a while loop statement, including the loop condition and a list of statements to execute while the condition is true.
@Getter
@RequiredArgsConstructor
public class WhileLoop extends Statement {

    /// The condition expression for the while loop.
    private final Expression expression;
    /// The list of statements inside the while loop.
    private final List<Statement> statements;

}
