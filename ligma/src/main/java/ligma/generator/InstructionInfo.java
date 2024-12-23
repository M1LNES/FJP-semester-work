package ligma.generator;

import ligma.enums.Instruction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InstructionInfo {

    private Instruction instruction;
    private int level;
    private int address;

}
