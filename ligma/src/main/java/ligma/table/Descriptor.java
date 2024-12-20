package ligma.table;

import ligma.enums.DataType;
import lombok.Builder;

/// Descriptor used in the Symbol Table.
///
/// @param name Name of the identifier.
/// @param type Data type / Return type (e.g., int, boolean).
/// @param isConstant To differentiate between constants and variables.
/// @param isFunction Is it a function ?
/// @param scopeLevel Scope level of the indentifier.
/// @param address Address in stack.
@Builder
public record Descriptor(
    String name,
    DataType type,
    boolean isConstant,
    boolean isFunction,
    int scopeLevel,
    int address
) {

}
