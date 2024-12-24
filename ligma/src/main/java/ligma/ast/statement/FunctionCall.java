package ligma.ast.statement;

import ligma.ast.expression.Expression;
import ligma.ast.function.Callable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class FunctionCall extends Statement implements Callable {

    private final String identifier;
    private final List<Expression> arguments;

}
