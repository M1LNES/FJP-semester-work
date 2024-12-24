package ligma.ir.function;

import ligma.ir.expression.Expression;

import java.util.List;

public interface Callable {

    String getIdentifier();
    List<Expression> getArguments();

}