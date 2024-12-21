package ligma.enums;

public enum ScopeType {
    GLOBAL,
    IF,
    ELSE,
    WHILE,
    FUNCTION;

    public static boolean isAnonymousScopeType(String scopeName) {
        String upperCase = scopeName.toUpperCase();

        return upperCase.equals(IF.name())
            || upperCase.equals(ELSE.name())
            || upperCase.equals(WHILE.name());
    }
}
