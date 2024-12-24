package ligma.generator;

import ligma.ir.function.Function;
import ligma.ir.program.Program;
import ligma.ir.statement.Statement;
import ligma.enums.Instruction;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ProgramGenerator extends Generator {

    private final Program program;

    @Override
    public void generate() {
        // Jump to the first instruction of the program - always at line 1
        addInstruction(Instruction.JMP, 0, 1);
        // Allocate space for the Activation Record
        addInstruction(Instruction.INT, 0, 3);

        // Enter global scope
        symbolTable.enterScope(false);

        List<Statement> statements = program.statements();
        List<Function> functions = program.functions();

        // Set functions so that they can be called
        Generator.setFunctions(functions);

        // Generate statements
        StatementGenerator statementGenerator = new StatementGenerator();
        statementGenerator.setStatements(statements);
        statementGenerator.generate();

        // Exit global scope
        symbolTable.exitScope();

        // Last instruction indicating end
        addInstruction(Instruction.RET, 0, 0);
    }

}
