package ligma.enums;

import ligma.exception.SemanticException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents various operators supported in the language, including arithmetic, relational, and logical operators.
@Getter
@RequiredArgsConstructor
public enum Operator {
    // Arithmetic Operators
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    MOD("%"),
    POW("^"),

    // Relational Operators
    EQ("=="),
    NEQ("!="),
    LT("<"),
    GTE(">="),
    GT(">"),
    LTE("<="),

    // Logical Operators
    AND("&&"),
    OR("||"),
    NOT("!");

    /// The symbol representing the operator.
    private final String symbol;

    /// Retrieves the corresponding operator for a given symbol.
    ///
    /// @param symbol The symbol of the operator (e.g., "+").
    /// @return The corresponding {@code Operator} enum.
    /// @throws SemanticException If the symbol does not match any operator.
    public static Operator fromSymbol(String symbol) {
        for (Operator op : Operator.values()) {
            if (op.getSymbol().equals(symbol)) {
                return op;
            }
        }
        throw new SemanticException("Unknown operator: " + symbol);
    }
}
