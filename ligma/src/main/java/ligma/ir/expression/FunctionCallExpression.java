package ligma.ir.expression;

import ligma.enums.DataType;
import ligma.ir.function.Callable;
import lombok.Getter;

import java.util.List;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a function call expression with an identifier and a list of arguments.
@Getter
public class FunctionCallExpression extends Expression implements Callable {

    /// The identifier (name) of the function being called.
    private final String identifier;
    /// The list of arguments passed to the function.
    private final List<Expression> arguments;

    public FunctionCallExpression(DataType type, String identifier, List<Expression> arguments) {
        super(type);
        this.identifier = identifier;
        this.arguments = arguments;
    }

}
