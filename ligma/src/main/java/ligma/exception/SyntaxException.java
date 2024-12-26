package ligma.exception;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a custom exception thrown during syntax analysis, extending {@code RuntimeException}.
public class SyntaxException extends RuntimeException {

    public SyntaxException(String message) {
        super(message);
    }

}
