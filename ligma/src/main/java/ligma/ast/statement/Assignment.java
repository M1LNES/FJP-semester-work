package ligma.ast.statement;

import ligma.ast.expression.Expression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Assignment extends Statement {

    private final String identifier;
    private final Expression expression;

}
