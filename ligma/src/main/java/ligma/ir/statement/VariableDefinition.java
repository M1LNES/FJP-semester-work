package ligma.ir.statement;

import ligma.enums.DataType;
import ligma.ir.expression.Expression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VariableDefinition extends Statement {

    private final String identifier;
    private final DataType type;
    private final Expression expression;

}
