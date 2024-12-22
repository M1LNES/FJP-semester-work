package ligma.enums;

public enum ScopeType {
    GLOBAL,
    IF,
    ELSE,
    FOR,
    WHILE,
    DO_WHILE,
    REPEAT_UNTIL,
    FUNCTION;

    public static boolean isAnonymousScopeType(String scopeName) {
        String upperCase = scopeName.toUpperCase();

        return upperCase.equals(IF.name())
            || upperCase.equals(ELSE.name())
            || upperCase.equals(FOR.name())
            || upperCase.equals(WHILE.name())
            || upperCase.equals(DO_WHILE.name())
            || upperCase.equals(REPEAT_UNTIL.name());
    }
}
