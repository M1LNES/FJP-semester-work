package ligma.ir.function;

import ligma.enums.DataType;

public record FunctionParameter(
    DataType type,
    String name
) {

}