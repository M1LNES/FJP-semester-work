package ligma;

import ligma.ast.program.Program;
import ligma.generated.LigmaLexer;
import ligma.generated.LigmaParser;
import ligma.listener.SyntaxErrorListener;
import ligma.table.Scope;
import ligma.table.SymbolTable;
import ligma.visitor.ProgramVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LigmaParserTest {

    private void parseFile(String filePath) throws IOException {
        CharStream charStream = CharStreams.fromFileName(filePath);
        LigmaLexer lexer = new LigmaLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        LigmaParser parser = new LigmaParser(tokenStream);

        // Add syntax error listener
        SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();
        parser.addErrorListener(syntaxErrorListener);

        LigmaParser.ProgramContext programContext = parser.program();
        ProgramVisitor programVisitor = new ProgramVisitor();

        // Musim pred kazdym testem mazat - uz vim, proc se to panu Dankovi nelibilo :)
        // TODO zatim hodne provizorni reseni, jinak to neudelame :)

        SymbolTable.getScopes().clear();

        Program program = programVisitor.visit(programContext);

        Map<String, Scope> symbolTable = SymbolTable.getScopes();

    }

    @Test
    void validFilesShouldNotThrowExceptions() {
        File validFolder = new File("./src/test/resources/valid/expression");

        for (File file : Objects.requireNonNull(validFolder.listFiles())) {
            assertThatCode(() -> parseFile(file.getAbsolutePath()))
                    .doesNotThrowAnyException();
        }
    }

    @Test
    void invalidFilesShouldThrowExceptions() {
        File invalidFolder = new File("./src/test/resources/invalid/expression");

        for (File file : Objects.requireNonNull(invalidFolder.listFiles())) {
            assertThatThrownBy(() -> parseFile(file.getAbsolutePath()))
                    .isInstanceOf(Exception.class)
                    .withFailMessage("Test failed for file: " + file.getName());
        }
    }


}
