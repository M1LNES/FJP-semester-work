package ligma.ast.function;

import ligma.ast.expression.Expression;
import ligma.ast.statement.Statement;
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
