package ligma.visitor;

import ligma.ast.Program;
import ligma.ast.statement.Statement;
import ligma.generated.LigmaBaseVisitor;
import ligma.generated.LigmaParser;
import ligma.table.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class ProgramVisitor extends LigmaBaseVisitor<Program> {

    private final StatementVisitor statementVisitor = new StatementVisitor();

    @Override
    public Program visitProgram(LigmaParser.ProgramContext ctx) {
        List<Statement> statements = new ArrayList<>();

        SymbolTable.enterScope("global");

        for (LigmaParser.StatementContext statementContext : ctx.statement()) {
            statements.add(statementVisitor.visit(statementContext));
        }

        return new Program(statements);
    }

}
