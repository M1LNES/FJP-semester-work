package ligma.generator;

import ligma.enums.DataType;
import ligma.enums.Instruction;
import ligma.exception.GenerateException;
import ligma.ir.function.Function;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Abstract generator that provides shared attributes and methods for specific generators.
@Setter
public abstract class Generator {

    /// A list of all functions in the program.
    @Setter
    protected static List<Function> functions;
    /// A map storing function addresses by their names.
    protected static final Map<String, Integer> functionAddresses = new HashMap<>();
    /// A list of generated instructions.
    private static List<InstructionInfo> instructions = new ArrayList<>();

    /// Abstract method to generate PL/0 instructions. Each subclass of `Generator` must implement
    /// this method to produce the relevant instructions (such as function calls, expressions, etc.).
    public abstract void generate();

    /// Adds a new instruction to the list of instructions.
    ///
    /// @param instruction the instruction to add
    /// @param level the level of the instruction (scope level)
    /// @param address the address associated with the instruction
    protected static void addInstruction(Instruction instruction, int level, int address) {
        instructions.add(new InstructionInfo(instruction, level, address));
    }

    /// Gets the current row (index) in the list of instructions.
    /// This is useful for keeping track of where in the instruction stream the generator is.
    ///
    /// @return the current instruction row
    protected static int getCurrentInstructionRow() {
        return instructions.size() - 1;
    }

    /// Modifies the address of an existing instruction at the given index.
    ///
    /// @param index the index of the instruction to modify
    /// @param address the new address to set for the instruction
    protected static void modifyInstructionAddress(int index, int address) {
        instructions.get(index).setAddress(address);
    }

    /// Writes the generated instructions to the output file.
    /// Each instruction is written in a formatted manner, including the row, instruction type, level, and address.
    ///
    /// @param writer A writer used to output the generated instructions to a file.
    public static void writeInstructions(BufferedWriter writer) {
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

    /// Retrieves the return type of the specified function.
    /// If the function is not found, it returns `DataType.INT` as the default type.
    ///
    /// @param functionIdentifier the name of the function whose return type is to be fetched
    /// @return the return type of the function
    protected static DataType getFunctionReturnType(String functionIdentifier) {
        for (Function function : functions) {
            if (function.name().equals(functionIdentifier)) {
                return function.returnType();
            }
        }

        return DataType.INT;
    }

    public static void clear() {
        functions.clear();
        functionAddresses.clear();
        instructions.clear();
    }

}
