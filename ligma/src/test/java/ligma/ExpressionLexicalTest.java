package ligma;

import ligma.exception.LexicalException;
import ligma.generated.LigmaLexer;
import ligma.generated.LigmaParser;
import ligma.listener.EnhancedLigmaLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ExpressionLexicalTest {

    private void runLexicalAnalysis(String resourcePath) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        CharStream charStream = CharStreams.fromStream(Objects.requireNonNull(inputStream));
        LigmaLexer lexer = new EnhancedLigmaLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        LigmaParser parser = new LigmaParser(tokenStream);

        parser.program();

        inputStream.close();
    }

    private static Stream<Arguments> loadFiles(String resourceFolder) {
        File folder = Path.of("src/test/resources", resourceFolder).toFile();
        return Stream.of(Objects.requireNonNull(folder.listFiles()))
                     .map(file -> Arguments.of(file.getName(), resourceFolder + "/" + file.getName()));
    }

    static Stream<Arguments> invalidFiles() {
        return loadFiles("lexical/invalid/expression");
    }

    @ParameterizedTest(name = "Invalid file: {0}")
    @MethodSource("invalidFiles")
    void invalidFilesShouldThrowExceptions(String fileName, String resourcePath) {
        assertThatThrownBy(() -> runLexicalAnalysis(resourcePath))
            .isInstanceOf(LexicalException.class);
    }

}
