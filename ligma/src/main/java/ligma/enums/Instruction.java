package ligma.enums;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a PL/0 instruction
///
/// #### Stack Operations
/// - **`lit 0, A`**: Push the constant `A` onto the stack.
/// - **`opr 0, A`**: Execute instruction `A`.
///
/// #### Unary and Binary Operations (`opr` codes)
/// | Code | Operation                                |
/// |------|------------------------------------------|
/// | 1    | Unary minus                              |
/// | 2    | Addition (`+`)                           |
/// | 3    | Subtraction (`-`)                        |
/// | 4    | Multiplication (`*`)                     |
/// | 5    | Integer division (`/`)                   |
/// | 6    | Modulo division (`%`)                    |
/// | 7    | Test if a number is odd                  |
/// | 8    | Equality test (`==`)                     |
/// | 9    | Inequality test (`!=`)                   |
/// | 10   | Less than (`<`)                          |
/// | 11   | Greater than or equal to (`>=`)          |
/// | 12   | Greater than (`>`)                       |
/// | 13   | Less than or equal to (`<=`)             |
///
/// #### Memory Operations
/// - **`lod L, A`**: Load the value of the variable at address `L, A` onto the top of the stack.
/// - **`sto L, A`**: Store the value from the top of the stack into the variable at address `L, A`.
///
/// #### Procedure and Control Flow
/// - **`cal L, A`**: Call procedure `A` from level `L`.
/// - **`int 0, A`**: Increase the stack top register by `A`.
/// - **`jmp 0, A`**: Jump to address `A`.
/// - **`jmc 0, A`**: Conditional jump to address `A` if the value on the stack top is `0`.
/// - **`ret 0, 0`**: Return from a procedure.
public enum Instruction {
    LIT,
    OPR,
    LOD,
    STO,
    CAL,
    INT,
    JMP,
    JMC,
    RET
}
