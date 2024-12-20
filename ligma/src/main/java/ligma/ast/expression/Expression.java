package ligma.ast.expression;

import ligma.enums.DataType;
import lombok.Getter;

@Getter
public abstract class Expression {

    private final DataType type;
    private final int line;

    protected Expression(DataType type, int line) {
        this.type = type;
        this.line = line;
    }

}
