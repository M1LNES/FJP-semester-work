package ligma.enums;

import ligma.exception.SemanticException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Operator {
    // Arithmetic Operators
    UNARY_MINUS("-", 1),
    ADD("+", 2),
    SUB("-", 3),
    MUL("*", 4),
    DIV("/", 5),
    MOD("%", 6),
    POW("^", -1),

    // Relational Operators
    EQ("==", 8),
    NEQ("!=", 9),
    LT("<", 10),
    GTE(">=", 11),
    GT(">", 12),
    LTE("<=", 13),

    // Logical Operators
    AND("&&", -1),
    OR("||", -1),
    NOT("!", -1);

    private final String symbol;
    private final int code;

    public static Operator fromSymbol(String symbol) {
        for (Operator op : Operator.values()) {
            if (op.getSymbol().equals(symbol)) {
                return op;
            }
        }
        throw new SemanticException("Unknown operator: " + symbol);
    }
}
