package ligma;

import ligma.generated.LigmaLexer;
import ligma.generated.LigmaParser;
import ligma.generator.Generator;
import ligma.generator.ProgramGenerator;
import ligma.ir.program.Program;
import ligma.listener.EnhancedLigmaLexer;
import ligma.listener.SyntaxErrorListener;
import ligma.visitor.ProgramVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;

class ProgramExampleTest {

    private void runSemanticAnalysis(String resourcePath) throws IOException {
        Program program = null;

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        CharStream charStream = CharStreams.fromStream(Objects.requireNonNull(inputStream));
        LigmaLexer lexer = new EnhancedLigmaLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        LigmaParser parser = new LigmaParser(tokenStream);

        // Add syntax error listener
        SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();
        parser.addErrorListener(syntaxErrorListener);

        LigmaParser.ProgramContext programContext = parser.program();

        ProgramVisitor programVisitor = new ProgramVisitor();
        program = programVisitor.visit(programContext);

        BufferedWriter writer = new BufferedWriter(Writer.nullWriter());
        Generator.setWriter(writer);

        Generator programGenerator = new ProgramGenerator(program);
        programGenerator.generate();

        Generator.writeInstructions();

        inputStream.close();
    }

    private static Stream<Arguments> loadFiles(String resourceFolder) {
        File folder = Path.of("src/main/resources", resourceFolder).toFile();
        return Stream.of(Objects.requireNonNull(folder.listFiles()))
                .map(file -> Arguments.of(file.getName(), resourceFolder + "/" + file.getName()));
    }

    static Stream<Arguments> testExampleFiles() {
        return loadFiles("programs");
    }

    @ParameterizedTest(name = "Valid file: {0}")
    @MethodSource("testExampleFiles")
    void validFilesShouldNotThrowExceptions(String fileName, String resourcePath) {
        assertThatCode(() -> runSemanticAnalysis(resourcePath))
                .doesNotThrowAnyException();
    }

}
