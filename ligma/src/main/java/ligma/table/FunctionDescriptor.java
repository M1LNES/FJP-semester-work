package ligma.table;

import ligma.enums.DataType;
import ligma.ir.expression.Expression;
import ligma.ir.function.FunctionParameter;
import ligma.ir.statement.Statement;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a function descriptor with its parameters, body, and return expression.
/// Extends the base Descriptor class with function-specific metadata.
@Getter
public class FunctionDescriptor extends Descriptor {

    /// The parameters of the function
    private final List<FunctionParameter> parameters;
    /// The statements representing the function body
    private final List<Statement> statements;
    /// The expression for the function's return value
    private final Expression returnExpression;

    @Builder
    public FunctionDescriptor(String name, DataType type, int scopeLevel, int addres, List<FunctionParameter> parameters, Expression expression, List<Statement> statements, Expression returnExpression) {
        super(name, type, scopeLevel, addres);
        this.parameters = parameters;
        this.statements = statements;
        this.returnExpression = returnExpression;
    }

}
