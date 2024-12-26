package ligma.ir.function;

import ligma.enums.DataType;
import ligma.ir.expression.Expression;
import ligma.ir.statement.Statement;

import java.util.List;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a function.
/// @param name The name of the function.
/// @param returnType The return type of the function.
/// @param parameters The list of parameters for the function.
/// @param statements The list of statements contained in the function.
/// @param returnExpression The expression that produces the return value of the function.
public record Function(
    String name,
    DataType returnType,
    List<FunctionParameter> parameters,
    List<Statement> statements,
    Expression returnExpression
) {

}
