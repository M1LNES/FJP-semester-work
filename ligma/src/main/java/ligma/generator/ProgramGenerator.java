package ligma.generator;

import ligma.enums.Instruction;
import ligma.ir.function.Function;
import ligma.ir.program.Program;
import ligma.ir.statement.Statement;
import ligma.table.SymbolTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ProgramGenerator extends Generator {

    private final Program program;

    @Override
    public void generate() {
        log.debug("Generating program");

        // Jump to the first instruction of the program - always at line 1
        addInstruction(Instruction.JMP, 0, 1);
        // Allocate space for the Activation Record
        addInstruction(Instruction.INT, 0, 3);

        // Enter global scope
        SymbolTable.enterScope(false);

        List<Statement> statements = program.statements();
        List<Function> functions = program.functions();

        // Set functions so that they can be called
        Generator.setFunctions(functions);

        // Generate statements
        StatementGenerator statementGenerator = new StatementGenerator();
        statementGenerator.setStatements(statements);
        statementGenerator.generate();

        // Exit global scope
        SymbolTable.exitScope();

        // Last instruction indicating end
        addInstruction(Instruction.RET, 0, 0);
    }

}
