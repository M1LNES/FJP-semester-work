package ligma;

import ligma.generated.LigmaLexer;
import ligma.generated.LigmaParser;
import ligma.listener.SyntaxErrorListener;
import ligma.table.SymbolTable;
import ligma.visitor.ProgramVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExpressionTest {

    @BeforeEach
    void setUp() {
        SymbolTable.getScopes().clear();
    }

    private void runSemanticAnalysis(String resourcePath) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        CharStream charStream = CharStreams.fromStream(Objects.requireNonNull(inputStream));
        LigmaLexer lexer = new LigmaLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        LigmaParser parser = new LigmaParser(tokenStream);

        // Add syntax error listener
        SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();
        parser.addErrorListener(syntaxErrorListener);

        LigmaParser.ProgramContext programContext = parser.program();

        ProgramVisitor programVisitor = new ProgramVisitor();
        programVisitor.visit(programContext);

        inputStream.close();
    }

    private static Stream<Arguments> loadFiles(String resourceFolder) {
        File folder = Path.of("src/test/resources", resourceFolder).toFile();
        return Stream.of(Objects.requireNonNull(folder.listFiles()))
                     .map(file -> Arguments.of(file.getName(), resourceFolder + "/" + file.getName()));
    }

    static Stream<Arguments> validFiles() {
        return loadFiles("valid/expression");
    }

    static Stream<Arguments> invalidFiles() {
        return loadFiles("invalid/expression");
    }

    @ParameterizedTest(name = "Valid file: {0}")
    @MethodSource("validFiles")
    void validFilesShouldNotThrowExceptions(String fileName, String resourcePath) {
        assertThatCode(() -> runSemanticAnalysis(resourcePath))
            .doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "Invalid file: {0}")
    @MethodSource("invalidFiles")
    void invalidFilesShouldThrowExceptions(String fileName, String resourcePath) {
        assertThatThrownBy(() -> runSemanticAnalysis(resourcePath))
            .isInstanceOf(RuntimeException.class);
    }

}
