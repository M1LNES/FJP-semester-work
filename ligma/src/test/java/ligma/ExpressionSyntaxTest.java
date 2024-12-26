package ligma;

import ligma.exception.SyntaxException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
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

class ExpressionSyntaxTest {

    private void runSyntaxAnalysis(String resourcePath) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        CharStream charStream = CharStreams.fromStream(Objects.requireNonNull(inputStream));

        // Run syntax analysis
        App.getProgramContext(charStream);

        inputStream.close();
    }

    private static Stream<Arguments> loadFiles(String resourceFolder) {
        File folder = Path.of("src/test/resources", resourceFolder).toFile();
        return Stream.of(Objects.requireNonNull(folder.listFiles()))
                .map(file -> Arguments.of(file.getName(), resourceFolder + "/" + file.getName()));
    }

    static Stream<Arguments> validFiles() {
        return loadFiles("syntax/valid/expression");
    }

    static Stream<Arguments> invalidFiles() {
        return loadFiles("syntax/invalid/expression");
    }

    @ParameterizedTest(name = "Valid file: {0}")
    @MethodSource("validFiles")
    void validFilesShouldNotThrowExceptions(String fileName, String resourcePath) {
        assertThatCode(() -> runSyntaxAnalysis(resourcePath))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "Invalid file: {0}")
    @MethodSource("invalidFiles")
    void invalidFilesShouldThrowExceptions(String fileName, String resourcePath) {
        assertThatThrownBy(() -> runSyntaxAnalysis(resourcePath))
                .isInstanceOf(SyntaxException.class);
    }

}
