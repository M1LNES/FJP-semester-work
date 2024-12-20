package ligma;

import ligma.ast.program.Program;
import ligma.generated.LigmaLexer;
import ligma.generated.LigmaParser;
import ligma.listener.SyntaxErrorListener;
import ligma.table.Scope;
import ligma.table.SymbolTable;
import ligma.visitor.ProgramVisitor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
public class App {
    public static void main(String[] args) {
        if (args.length != 1) {
            log.error("Compiler expected one argument: file with program");
            return;
        }

        String filename = args[0];

        try(InputStream input = new FileInputStream(filename))
        {
            log.info("Successfully opened file: {}", filename);

            CharStream charStream = CharStreams.fromStream(input);
            LigmaLexer ligmaLexer = new LigmaLexer(charStream);
            CommonTokenStream tokenStream = new CommonTokenStream(ligmaLexer);
            LigmaParser parser = new LigmaParser(tokenStream);

            // Listen to syntax errors
            SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();
            parser.addErrorListener(syntaxErrorListener);

            LigmaParser.ProgramContext programContext = parser.program();
            ProgramVisitor programVisitor = new ProgramVisitor();

            Program program = programVisitor.visit(programContext);

            Map<String, Scope> symbolTable = SymbolTable.getScopes();

            log.info("Semantic analysis has finished successfully");
        }
        catch (IOException exception)
        {
            log.error("File not found: {}", filename);
        }
    }
}
