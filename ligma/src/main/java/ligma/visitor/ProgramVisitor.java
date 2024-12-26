package ligma.visitor;

import ligma.generated.LigmaBaseVisitor;
import ligma.generated.LigmaParser;
import ligma.ir.function.Function;
import ligma.ir.program.Program;
import ligma.ir.statement.Statement;
import ligma.table.SymbolTable;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// The ProgramVisitor class is responsible for visiting the top-level program structure in the Ligma language.
/// It traverses the program, visiting all statements and function definitions, and performs semantic checks
/// by interacting with the Symbol Table. This class is part of the semantic analysis phase and ensures that
/// the program structure is valid and well-formed.
@Slf4j
public class ProgramVisitor extends LigmaBaseVisitor<Program> {

    /// A static visitor for processing statements within the program.
    private static final StatementVisitor statementVisitor = new StatementVisitor();
    /// A static visitor for processing functions within the program.
    private static final FunctionVisitor functionVisitor = new FunctionVisitor();

    /// Visits a program context in the Ligma language, processing the program's statements and functions,
    /// managing scope through the Symbol Table, and creating a new Program object representing the parsed structure.
    ///
    /// @param ctx The context of the program.
    /// @return A Program object that represents the parsed program.
    @Override
    public Program visitProgram(LigmaParser.ProgramContext ctx) {
        log.debug("Program");

        SymbolTable.enterScope(false);

        // statements
        List<Statement> statements = new ArrayList<>();
        for (LigmaParser.StatementContext statementContext : ctx.statement()) {
            statements.add(statementVisitor.visit(statementContext));
        }

        // functions
        List<Function> functions = new ArrayList<>();
        for (LigmaParser.FunctionDefinitionContext functionContext : ctx.functionDefinition()) {
            functions.add((Function) functionVisitor.visit(functionContext));
        }

        SymbolTable.exitScope();

        return new Program(statements, functions);
    }

}
