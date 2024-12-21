package ligma.table;

import ligma.enums.DataType;
import lombok.Builder;
import lombok.Getter;

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
