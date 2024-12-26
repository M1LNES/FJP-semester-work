package ligma.ir.statement;

import ligma.ir.expression.Expression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents an assignment statement, including the identifiers and the expression assigned.
@Getter
@RequiredArgsConstructor
public class Assignment extends Statement {

    /// All identifiers that appear in the assigment.
    /// The first identifier is always present.
    private final List<String> allIdentifiers;
    /// The expression being assigned to the identifiers.
    private final Expression expression;

}
