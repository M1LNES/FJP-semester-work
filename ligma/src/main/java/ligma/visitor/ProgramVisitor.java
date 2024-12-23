package ligma.visitor;

import ligma.ast.function.Function;
import ligma.ast.program.Program;
import ligma.ast.statement.Statement;
import ligma.enums.ScopeType;
import ligma.generated.LigmaBaseVisitor;
import ligma.generated.LigmaParser;
import ligma.table.SymbolTable;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProgramVisitor extends LigmaBaseVisitor<Program> {

    private final StatementVisitor statementVisitor = new StatementVisitor();
    private final FunctionDefinitionVisitor functionVisitor = new FunctionDefinitionVisitor();

    @Override
    public Program visitProgram(LigmaParser.ProgramContext ctx) {
        log.debug("Program");

        SymbolTable.enterScope(ScopeType.GLOBAL.name());

        List<Statement> statements = new ArrayList<>();
        List<Function> functions = new ArrayList<>();

        // statements
        for (LigmaParser.StatementContext statementContext : ctx.statement()) {
            statements.add(statementVisitor.visit(statementContext));
        }

        // functions
        for (LigmaParser.FunctionDefinitionContext functionContext : ctx.functionDefinition()) {
            functions.add(functionVisitor.visit(functionContext));
        }

        SymbolTable.exitScope();

        return new Program(statements, functions);
    }

}
