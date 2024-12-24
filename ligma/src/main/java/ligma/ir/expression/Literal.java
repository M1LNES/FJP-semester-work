package ligma.ir.expression;

import ligma.enums.DataType;
import lombok.Getter;

@Getter
public class Literal<T> extends Expression {

    private final T value;

    public Literal(T value, DataType type) {
        super(type);
        this.value = value;
    }

}
