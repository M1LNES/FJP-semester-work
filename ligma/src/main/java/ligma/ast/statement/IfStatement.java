package ligma.ast.statement;

import ligma.ast.expression.Expression;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class IfStatement extends Statement {

    private final Expression expression;
    private final List<Statement> ifStatements;
    /// null -> if statement doesn't have 'else'
    private final List<Statement> elseStatements;

}
