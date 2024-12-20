package ligma.ast.statement;

import ligma.ast.expression.Expression;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VariableDefinition extends Statement {
    private final String identifier;
    private final Expression expression;
}
