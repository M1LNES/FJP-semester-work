package ligma.ir.statement;

import ligma.ir.expression.Expression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a do-while loop statement, including a list of statements and a loop condition.
@Getter
@RequiredArgsConstructor
public class DoWhileLoop extends Statement {

    /// The list of statements inside the do-while loop.
    private final List<Statement> statements;
    /// The condition expression for the do-while loop.
    private final Expression expression;

}
