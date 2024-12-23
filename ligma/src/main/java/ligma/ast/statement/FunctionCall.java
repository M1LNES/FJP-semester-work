package ligma.ast.statement;

import ligma.ast.expression.Expression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class FunctionCall extends Statement {

    private final String identifier;
    private final List<Expression> arguments;

}
