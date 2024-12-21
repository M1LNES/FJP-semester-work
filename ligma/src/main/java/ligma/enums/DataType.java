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
        } catch (IllegalArgumentException ignored) {
            throw new RuntimeException("Invalid data type: " + dataType);
        }
    }

    public static boolean isNumericType(DataType type) {
        return switch (type) {
            case INT, FLOAT -> true;
            default -> false;
        };
    }

    public static boolean isBooleanType(DataType type) {
        return type == BOOLEAN;
    }

    public static DataType getResultingNumericDataType(DataType leftExprType, DataType rightExprType) {
        DataType finalExprType = null;

        // boolean + 5 | 5 + boolean -> invalid
        if (!DataType.isNumericType(leftExprType) || !DataType.isNumericType(rightExprType)) {
            throw new RuntimeException("Cannot apply the operation to non-numeric type");
        }

        // int + int -> int
        if (leftExprType == DataType.INT && rightExprType == DataType.INT) {
            finalExprType = DataType.INT;
        }
        // float + float -> float
        else if (leftExprType == DataType.FLOAT && rightExprType == DataType.FLOAT) {
            finalExprType = DataType.FLOAT;
        }
        // int + float | float + int -> float
        else if (
            (leftExprType == DataType.INT && rightExprType == DataType.FLOAT)
            || (leftExprType == DataType.FLOAT && rightExprType == DataType.INT)
        ) {
            finalExprType = DataType.FLOAT;
        }

        return finalExprType;
    }

}
