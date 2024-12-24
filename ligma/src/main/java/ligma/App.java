package ligma;

import ligma.generated.LigmaLexer;
import ligma.generated.LigmaParser;
import ligma.generator.Generator;
import ligma.generator.ProgramGenerator;
import ligma.ir.program.Program;
import ligma.listener.EnhancedLigmaLexer;
import ligma.listener.SyntaxErrorListener;
import ligma.table.Scope;
import ligma.table.SymbolTable;
import ligma.visitor.ProgramVisitor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
public class App {

    public static void main(String[] args) {
        if (args.length != 2) {
            log.error("Compiler expected two arguments: <file with program> <output file with PL/0 instructions>");
            return;
        }

        String inputFilename = args[0];
        String outputFilename = args[1];

        Program program = null;

        // Run lexical, syntax and semantic analysis
        try (InputStream input = new FileInputStream(inputFilename)) {
            log.info("Successfully opened input file: {}", inputFilename);

            CharStream charStream = CharStreams.fromStream(input);
            LigmaLexer ligmaLexer = new EnhancedLigmaLexer(charStream);
            CommonTokenStream tokenStream = new CommonTokenStream(ligmaLexer);
            LigmaParser parser = new LigmaParser(tokenStream);

            // Listen to syntax errors
            SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();
            parser.addErrorListener(syntaxErrorListener);

            LigmaParser.ProgramContext programContext = parser.program();
            ProgramVisitor programVisitor = new ProgramVisitor();

            program = programVisitor.visit(programContext);

            log.info("Semantic analysis has finished successfully");
        } catch (IOException exception) {
            log.error("File not found: {}", inputFilename);
        }

        // Run code generation
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename))) {
            log.info("Successfully opened output file: {}", inputFilename);

            Generator.setWriter(writer);

            Generator programGenerator = new ProgramGenerator(program);
            programGenerator.generate();

            Generator.writeInstructions();

            log.info("Successfully generated PL/0 instructions to the output file");
        } catch (IOException exception) {
            log.error("Output file not found: {}", outputFilename);
        }
    }

}
