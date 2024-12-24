package ligma.ast.expression;

import ligma.enums.DataType;
import lombok.Getter;

@Getter
public class Identifier extends Expression {

    private final String name;

    public Identifier(String name, DataType type) {
        super(type);
        this.name = name;
    }

}
