package ligma.ast.function;

import ligma.enums.DataType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FunctionParameter {

    private final DataType type;
    private final String name;

}
