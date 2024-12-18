package ligma;

import ligma.generated.LigmaLexer;
import ligma.generated.LigmaParser;
import ligma.visitor.ProgramVisitor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Log
public class App {
    public static void main(String[] args) {
        if (args.length != 1) {
            log.severe("Compiler expected one argument: file with program");
            return;
        }

        String filename = args[0];

        try(InputStream input = new FileInputStream(filename))
        {
            log.info("Successfully opened file: " + filename);

            CharStream charStream = CharStreams.fromStream(input);
            LigmaLexer ligmaLexer = new LigmaLexer(charStream);
            CommonTokenStream tokenStream = new CommonTokenStream(ligmaLexer);
            LigmaParser parser = new LigmaParser(tokenStream);
            LigmaParser.ProgramContext programContext = parser.program();
            ProgramVisitor programVisitor = new ProgramVisitor();

            programVisitor.visit(programContext);
        }
        catch (IOException exception)
        {
            log.severe("File not found: " + filename);
        }
    }
}
