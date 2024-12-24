package ligma.ir.expression;

import ligma.enums.DataType;
import ligma.ir.function.Callable;
import lombok.Getter;

import java.util.List;

@Getter
public class FunctionCallExpression extends Expression implements Callable {

    private final String identifier;
    private final List<Expression> arguments;

    public FunctionCallExpression(DataType type, String identifier, List<Expression> arguments) {
        super(type);
        this.identifier = identifier;
        this.arguments = arguments;
    }

}
