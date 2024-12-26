package ligma.table;

import ligma.enums.DataType;
import lombok.Builder;
import lombok.Getter;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a variable or constant descriptor with its associated metadata.
/// Extends the generic Descriptor class to include constant-specific details.
@Getter
public class VariableDescriptor extends Descriptor {

    /// To differentiate between constants and variables.
    private final boolean isConstant;

    @Builder
    public VariableDescriptor(String name, DataType type, int scopeLevel, int addres, boolean isConstant) {
        super(name, type, scopeLevel, addres);
        this.isConstant = isConstant;
    }

}
