package ligma.ast.expression;

import ligma.enums.DataType;
import lombok.Getter;

import java.util.List;

@Getter
public class FunctionCallExpression extends Expression {

    private final String identifier;
    private final List<Expression> arguments;

    public FunctionCallExpression(DataType type, int line, String identifier, List<Expression> arguments) {
        super(type, line);
        this.identifier = identifier;
        this.arguments = arguments;
    }

}
