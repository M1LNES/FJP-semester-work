package ligma.exception;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a custom exception thrown during the generation process, extending {@code RuntimeException}.
public class GenerateException extends RuntimeException {

    public GenerateException(String message) {
        super(message);
    }

}
