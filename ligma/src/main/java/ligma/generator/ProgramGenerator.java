package ligma.generator;

import ligma.ast.function.Function;
import ligma.ast.program.Program;
import ligma.ast.statement.Statement;
import ligma.enums.Instruction;
import ligma.enums.ScopeType;
import ligma.table.Scope;
import ligma.table.SymbolTable;
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
        SymbolTable.reenterScope(ScopeType.GLOBAL.name());

        List<Statement> statements = program.getStatements();
        List<Function> functions = program.getFunctions();

        // Generate statements
        StatementGenerator statementGenerator = new StatementGenerator();
        statementGenerator.setStatements(statements);
        statementGenerator.generate();

        // Generate functions
        Generator functionGenerator = new FunctionGenerator(functions);
        functionGenerator.generate();

        // Exit global scope
        SymbolTable.exitScope();

        // Last instruction indicating end
        addInstruction(Instruction.RET, 0, 0);
    }

}
