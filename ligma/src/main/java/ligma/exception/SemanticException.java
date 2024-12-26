package ligma.exception;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a custom exception thrown during semantic analysis, extending {@code RuntimeException}.
public class SemanticException extends RuntimeException {

    public SemanticException(String message) {
        super(message);
    }

}
