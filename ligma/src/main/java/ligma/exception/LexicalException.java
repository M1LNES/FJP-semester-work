package ligma.exception;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a custom exception thrown during lexical analysis, extending {@code RuntimeException}.
public class LexicalException extends RuntimeException {

    public LexicalException(String message) {
        super(message);
    }

}
