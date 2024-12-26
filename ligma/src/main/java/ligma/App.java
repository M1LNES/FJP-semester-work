package ligma;

import ligma.generated.LigmaLexer;
import ligma.generated.LigmaParser;
import ligma.generator.Generator;
import ligma.generator.ProgramGenerator;
import ligma.ir.program.Program;
import ligma.listener.EnhancedLigmaLexer;
import ligma.listener.SyntaxErrorListener;
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

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Main class for the Ligma compiler.
@Slf4j
public class App {

    /// Main entry point for the compiler application.
    ///
    /// @param args Command-line arguments: 1st argument is the input file, 2nd is the output file.
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

            // Create CharStream from the input file
            CharStream charStream = CharStreams.fromStream(input);

            // Get the program context
            LigmaParser.ProgramContext programContext = getProgramContext(charStream);

            // Visit the parsed program context using the ProgramVisitor
            ProgramVisitor programVisitor = new ProgramVisitor();
            program = programVisitor.visit(programContext);

            log.info("Semantic analysis has finished successfully");
        } catch (IOException exception) {
            log.error("File not found: {}", inputFilename);
        }

        // Run PL/0 instructions generation
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename))) {
            log.info("Successfully opened output file: {}", inputFilename);

            // Initialize program generator and generate PL/0 instructions
            Generator programGenerator = new ProgramGenerator(program);
            programGenerator.generate();

            // Write generated instructions to the output file
            Generator.writeInstructions(writer);

            log.info("Successfully generated PL/0 instructions to the output file");
        } catch (IOException exception) {
            log.error("Output file not found: {}", outputFilename);
        }
    }

    /// Parses the input stream to extract the program context.
    ///
    /// @param charStream The input stream containing the source code to be parsed.
    public static LigmaParser.ProgramContext getProgramContext(CharStream charStream) {
        // Initialize the lexer (with listener to lexical errors) with the charStream
        LigmaLexer ligmaLexer = new EnhancedLigmaLexer(charStream);
        // Create a token stream from the lexer
        CommonTokenStream tokenStream = new CommonTokenStream(ligmaLexer);
        // Create the parser from the token stream
        LigmaParser parser = new LigmaParser(tokenStream);

        // Listen for syntax errors during parsing
        SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(syntaxErrorListener);

        // Parse the program and return the program context
        return parser.program();
    }

}
