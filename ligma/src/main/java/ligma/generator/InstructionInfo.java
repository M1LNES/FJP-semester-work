package ligma.generator;

import ligma.enums.Instruction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents an individual instruction with the details.
@Getter
@Setter
@AllArgsConstructor
public class InstructionInfo {

    /// The type of instruction, which defines the operation to be performed (e.g., loading, storing, etc.).
    private Instruction instruction;
    /// The level of the instruction, representing the scope or nesting level of the instruction.
    private int level;
    /// The memory address or position associated with this instruction.
    private int address;

}
