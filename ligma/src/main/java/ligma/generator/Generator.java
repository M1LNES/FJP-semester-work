package ligma.generator;

import ligma.enums.Instruction;
import ligma.exception.GenerateException;
import ligma.table.SymbolTableGeneration;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Setter
public abstract class Generator {

    @Getter
    private static List<InstructionInfo> instructions = new ArrayList<>();
    @Setter
    private static BufferedWriter writer;

    protected static SymbolTableGeneration symbolTable = new SymbolTableGeneration();

    public abstract void generate();

    public static void addInstruction(Instruction instruction, int level, int address) {
        instructions.add(new InstructionInfo(instruction, level, address));
    }

    public static int getCurrentInstructionRow() {
        return instructions.size() - 1;
    }

    public static void modifyInstructionAddress(int index, int address) {
        instructions.get(index).setAddress(address);
    }

    public static void writeInstructions() {
        int row = 0;

        for (InstructionInfo instruction : instructions) {
            String instructionString = String.format("%-3d %-5s %-3d %d%n",
                row++,
                instruction.getInstruction().name(),
                instruction.getLevel(),
                instruction.getAddress()
            );

            try {
                writer.write(instructionString);
            } catch (IOException e) {
                throw new GenerateException("Could not write instruction '" + instructionString + "' to file");
            }
        }

    }

}
