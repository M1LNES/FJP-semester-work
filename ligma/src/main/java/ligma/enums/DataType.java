package ligma.enums;

import ligma.exception.SemanticException;
import lombok.Getter;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents different data types supported in the language.
@Getter
public enum DataType {

    INT,
    BOOLEAN;

    /// Parses the given {@code dataType} to the enum.
    ///
    /// @param dataType The String represantation of the data type (i.e.: int).
    /// @return The enum value of the data type.
    public static DataType getDataType(String dataType) {
        try {
            return DataType.valueOf(dataType.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            throw new SemanticException("Invalid data type: " + dataType);
        }
    }

}
