package ligma.ir.expression;

import ligma.enums.DataType;
import lombok.Getter;

@Getter
public abstract class Expression {

    private final DataType type;

    protected Expression(DataType type) {
        this.type = type;
    }

}
