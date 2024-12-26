package ligma;

import ligma.exception.GenerateException;
import ligma.generated.LigmaParser;
import ligma.generator.Generator;
import ligma.generator.ProgramGenerator;
import ligma.ir.program.Program;
import ligma.visitor.ProgramVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FunctionSemanticTest {

    private void runSemanticAnalysis(String resourcePath) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        CharStream charStream = CharStreams.fromStream(Objects.requireNonNull(inputStream));

        LigmaParser.ProgramContext programContext = App.getProgramContext(charStream);

        // Visit the parsed program context using the ProgramVisitor
        ProgramVisitor programVisitor = new ProgramVisitor();
        Program program = programVisitor.visit(programContext);

        // Initialize program generator and generate PL/0 instructions
        Generator programGenerator = new ProgramGenerator(program);
        programGenerator.generate();

        BufferedWriter writer = new BufferedWriter(Writer.nullWriter());
        Generator.writeInstructions(writer);

        Generator.clear();
        inputStream.close();
    }

    private static Stream<Arguments> loadFiles(String resourceFolder) {
        File folder = Path.of("src/test/resources", resourceFolder).toFile();
        return Stream.of(Objects.requireNonNull(folder.listFiles()))
                .map(file -> Arguments.of(file.getName(), resourceFolder + "/" + file.getName()));
    }

    static Stream<Arguments> validFiles() {
        return loadFiles("generator/functions/valid");
    }
    static Stream<Arguments> invalidFiles() {
        return loadFiles("generator/functions/invalid");
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
                .isInstanceOf(GenerateException.class);
    }

}
