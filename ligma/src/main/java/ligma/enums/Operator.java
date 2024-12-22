package ligma.enums;

import ligma.exception.SemanticException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
    GT(">"),
    LT("<"),
    GTE(">="),
    LTE("<="),

    // Logical Operators
    AND("&&"),
    OR("||"),
    NOT("!");

    private final String symbol;

    public static Operator fromSymbol(String symbol) {
        for (Operator op : Operator.values()) {
            if (op.getSymbol().equals(symbol)) {
                return op;
            }
        }
        throw new SemanticException("Unknown operator: " + symbol);
    }
}
