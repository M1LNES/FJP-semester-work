package ligma.listener;

import ligma.exception.LexicalException;
import ligma.generated.LigmaLexer;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.misc.Interval;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// An enhanced lexer for the Ligma language with improved error handling.
/// Overrides the default ANTLR lexer behavior to provide detailed error messages
/// and logs them for debugging purposes.
@Slf4j
public class EnhancedLigmaLexer extends LigmaLexer {

    public EnhancedLigmaLexer(CharStream input) {
        super(input);
    }

    @Override
    public void notifyListeners(LexerNoViableAltException e) {
        // Get the offending character or token
        String errorText = _input.getText(Interval.of(_tokenStartCharIndex, _input.index()));

        // Generate a custom error message
        String message = "Lexical error at line " + getLine() + ", column " + getCharPositionInLine() + ": unexpected character '" + errorText + "'";
        log.error(message);

        throw new LexicalException(message);
    }

}
