package ligma.ast.statement;

import ligma.ast.expression.Expression;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class WhileStatement extends Statement {

    private final Expression expression;
    private final List<Statement> statements;

}
