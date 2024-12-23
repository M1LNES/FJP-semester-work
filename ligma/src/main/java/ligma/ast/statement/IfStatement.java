package ligma.ast.statement;

import ligma.ast.expression.Expression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class IfStatement extends Statement {

    private final Expression expression;
    private final List<Statement> ifStatements;
    /// Empty if there is no 'else' in the if statement
    private final List<Statement> elseStatements;

}
