package ligma.ast.function;

import ligma.ast.expression.Expression;

import java.util.List;

public interface Callable {
    String getIdentifier();
    List<Expression> getArguments();
}