package ligma.ir.statement;

import ligma.ir.expression.Expression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Assignment extends Statement {

    /// All identifiers that appear in the assigment.
    /// The first identifier is always present.
    private final List<String> allIdentifiers;
    private final Expression expression;

}
