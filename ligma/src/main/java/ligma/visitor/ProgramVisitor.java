package ligma.visitor;

import ligma.enums.ScopeType;
import ligma.generated.LigmaBaseVisitor;
import ligma.generated.LigmaParser;
import ligma.ir.function.Function;
import ligma.ir.program.Program;
import ligma.ir.statement.Statement;
import ligma.table.SymbolTable;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProgramVisitor extends LigmaBaseVisitor<Program> {

    private static final StatementVisitor statementVisitor = new StatementVisitor();
    private static final FunctionVisitor functionVisitor = new FunctionVisitor();

    @Override
    public Program visitProgram(LigmaParser.ProgramContext ctx) {
        log.debug("Program");

        SymbolTable.enterScope(ScopeType.GLOBAL.name());

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
