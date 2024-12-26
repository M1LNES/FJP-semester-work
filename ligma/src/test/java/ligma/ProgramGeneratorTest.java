package ligma;

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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;

class ProgramGeneratorTest {

    private void runGeneration(String resourcePath, String outputPath) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        CharStream charStream = CharStreams.fromStream(Objects.requireNonNull(inputStream));

        // Run lexical and syntax analysis and get the program context
        LigmaParser.ProgramContext programContext = App.getProgramContext(charStream);

        // Run semantic analysis
        ProgramVisitor programVisitor = new ProgramVisitor();
        Program program = programVisitor.visit(programContext);

        // Run generation
        Generator programGenerator = new ProgramGenerator(program);
        programGenerator.generate();

        // Write the instructions to the output file
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
        Generator.writeInstructions(writer);

        Generator.clear();
        inputStream.close();
        writer.close();
    }

    private static Stream<Arguments> loadFiles(String resourceFolder, String outputFolder) {
        File inFolder = Path.of("src/main/resources", resourceFolder).toFile();
        File outFolder = Path.of("src/main/resources", outputFolder).toFile();

        return Stream.of(Objects.requireNonNull(inFolder.listFiles()))
                .map(file -> Arguments.of(
                    file.getName(),
                    resourceFolder + File.separator + file.getName(),
                    outFolder + File.separator + file.getName())
                );
    }

    static Stream<Arguments> testExampleFiles() {
        return loadFiles("programs", "output");
    }

    @ParameterizedTest(name = "Valid file: {0}")
    @MethodSource("testExampleFiles")
    void validFilesShouldNotThrowExceptions(String fileName, String resourcePath, String outputPath) {
        assertThatCode(() -> runGeneration(resourcePath, outputPath))
                .doesNotThrowAnyException();
    }

}
