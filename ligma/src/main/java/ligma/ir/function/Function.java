package ligma.ir.function;

import ligma.ir.expression.Expression;
import ligma.ir.statement.Statement;
import ligma.enums.DataType;

import java.util.List;

public record Function(
    String name,
    DataType returnType,
    List<FunctionParameter> parameters,
    List<Statement> statements,
    Expression returnExpression
) {

}