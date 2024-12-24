package ligma.table;

import ligma.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/// Descriptor used in the Symbol Table.
@Getter
@Setter
@AllArgsConstructor
public abstract class Descriptor {

    /// Name of the identifier.
    private String name;
    /// Data type / Return type (e.g., int, boolean).
    private DataType type;
    /// Scope level of the indentifier.
    private int level;
    /// Relative Address (to the current scope) in stack.
    private int addres;

}
