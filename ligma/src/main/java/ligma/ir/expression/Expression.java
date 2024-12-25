package ligma.ir.expression;

import ligma.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class Expression {

    private DataType type;

}
