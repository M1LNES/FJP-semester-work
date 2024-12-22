package ligma.ast.statement;

import ligma.ast.expression.Expression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ForLoop extends Statement {

    private final String identifier;
    private final Expression expression;
    private final Expression toExpression;
    private final List<Statement> statements;

}
