package ligma.ast;

import ligma.ast.expression.Expression;

public record Variable(
    String type,
    String name,
    Object value
) {

}
