package ligma.ast.expression;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class Literal<T> extends Expression {

    private final T value;

    public Literal(T value, int line) {
        super(line);
        this.value = value;
    }

}
