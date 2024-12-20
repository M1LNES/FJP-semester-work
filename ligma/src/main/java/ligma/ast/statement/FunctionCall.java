package ligma.ast.statement;

import ligma.ast.expression.Expression;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class FunctionCall extends Statement {

    private final String identifier;
    private final List<Expression> arguments;

}
