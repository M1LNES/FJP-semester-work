package ligma.ast.statement;

import ligma.ast.expression.Expression;
import ligma.enums.DataType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VariableDefinition extends Statement {

    private final String identifier;
    private final DataType type;
    private final Expression expression;

}
