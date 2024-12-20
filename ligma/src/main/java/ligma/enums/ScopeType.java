package ligma.enums;

public enum ScopeType {
    GLOBAL,
    IF,
    WHILE,
    FUNCTION;

    public static boolean isAnonymousScopeType(ScopeType scopeType) {
        return switch (scopeType) {
            case IF, WHILE -> true;
            default -> false;
        };
    }
}
