package ligma.ir.statement;

import ligma.ir.expression.Expression;
import ligma.ir.function.Callable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class FunctionCall extends Statement implements Callable {

    private final String identifier;
    private final List<Expression> arguments;

}
