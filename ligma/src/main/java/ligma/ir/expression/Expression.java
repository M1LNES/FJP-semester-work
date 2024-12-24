package ligma.ir.expression;

import ligma.enums.DataType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Expression {

    private final DataType type;

}
