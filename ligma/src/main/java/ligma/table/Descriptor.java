package ligma.table;

import ligma.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/// Descriptor used in the Symbol Table.
@Getter
@AllArgsConstructor
public abstract class Descriptor {

    /// Name of the identifier.
    private String name;
    /// Data type / Return type (e.g., int, boolean).
    private DataType type;
    /// Scope level of the indentifier.
    private int scopeLevel;
    /// Address in stack.
    private int addres;

}
