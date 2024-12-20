package ligma.ast.expression;

import ligma.enums.DataType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class Literal<T> extends Expression {

    private final T value;

    public Literal(T value, DataType type, int line) {
        super(type, line);
        this.value = value;
    }

}
