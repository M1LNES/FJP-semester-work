package ligma.enums;

import lombok.Getter;

@Getter
public enum DataType {

    INT,
    FLOAT,
    BOOLEAN;

    /// Parses the given {@code dataType} to the enum.
    ///
    /// @param dataType The String represantation of the data type (i.e.: int, float).
    /// @return The enum value of the data type.
    public static DataType getDataType(String dataType) {
        try {
            return DataType.valueOf(dataType.toUpperCase());
        } catch (IllegalArgumentException _) {
            throw new RuntimeException("Invalid data type: " + dataType);
        }
    }

}
