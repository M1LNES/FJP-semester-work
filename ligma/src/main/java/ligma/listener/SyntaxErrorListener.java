package ligma.listener;

import ligma.exception.SyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// A custom syntax error listener for the Ligma language parser.
/// Overrides the default ANTLR error listener to provide detailed error messages,
/// logs syntax errors, and throws a custom SyntaxException for handling.
@Slf4j
public class SyntaxErrorListener extends BaseErrorListener {

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        log.error("Syntax error at line {}, position {}: {}", line, charPositionInLine, msg);
        throw new SyntaxException("Parsing failed: " + msg);
    }

}
