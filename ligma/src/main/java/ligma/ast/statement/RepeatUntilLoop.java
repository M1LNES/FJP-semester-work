package ligma.ast.statement;

import ligma.ast.expression.Expression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class RepeatUntilLoop extends Statement {

    private final List<Statement> statements;
    private final Expression expression;

}
