package ligma.table;

import ligma.enums.ScopeType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SymbolTable {

    /// Persistent storage for all scopes
    @Getter
    private static final Map<String, Scope> scopes = new HashMap<>();
    /// Counters for each type of scope
    @Getter
    private static final Map<String, Integer> scopeCounters = new HashMap<>();
    /// Active scope
    @Getter
    private static Scope currentScope = null;

    private SymbolTable() {
    }

    /// Generate unique names for new scopes (useful for 'if', 'for', ..)
    public static String generateScopeName(String scopeType) {
        int count = scopeCounters.getOrDefault(scopeType, 0) + 1;
        scopeCounters.put(scopeType, count);
        return scopeType + "_" + count;
    }

    /// Enter a new scope
    public static void enterScope(String scopeName) {
        // The scope is anonymous -> add number suffix
        if (ScopeType.isAnonymousScopeType(scopeName)) {
            scopeName = generateScopeName(scopeName);
        }

        if (scopes.containsKey(scopeName)) {
            throw new IllegalArgumentException("Scope '" + scopeName + "' already exists.");
        }

        log.debug("Entering scope '{}'", scopeName);

        Scope newScope = new Scope(scopeName, currentScope); // Set parent to the current scope
        currentScope = newScope; // Update the current scope
        scopes.put(scopeName, newScope); // Persist the scope
    }

    public static void reenterScope(String scopeName) {
        // The scope is anonymous -> add number suffix
        if (ScopeType.isAnonymousScopeType(scopeName)) {
            scopeName = generateScopeName(scopeName);
        }

        if (!scopes.containsKey(scopeName)) {
            throw new IllegalArgumentException("Scope '" + scopeName + "' doesn't exist.");
        }

        log.debug("Reentering scope '{}'", scopeName);

        currentScope = scopes.get(scopeName);
    }

    /// Exit the current scope
    public static void exitScope() {
        if (currentScope == null) {
            throw new IllegalStateException("Cannot exit scope: No active scope!");
        }

        log.debug("Leaving scope '{}'", currentScope.getName());

        currentScope = currentScope.getParent(); // Move up to the parent scope
    }

    /// Add a descriptor to the current scope
    public static void add(String identifier, Descriptor descriptor) {
        if (currentScope == null) {
            throw new IllegalStateException("No active scope to add the identifier to.");
        }

        currentScope.addDescriptor(identifier, descriptor);
    }

    /// Check whether the identifier exists in the current scope
    public static boolean isIdentifierInCurrentScope(String identifier) {
        if (currentScope == null) {
            throw new IllegalStateException("No active scope to find the identifier in.");
        }

        return currentScope.hasDescriptor(identifier);
    }

    /// Lookup a variable in the current scope hierarchy
    public static Descriptor lookup(String identifier) {
        Scope scope = currentScope;

        while (scope != null) {
            Descriptor descriptor = scope.getDescriptor(identifier);

            // Descriptor found
            if (descriptor != null) {
                return descriptor;
            }

            // Move up to the parent scope
            scope = scope.getParent();
        }

        // Descriptor not found
        return null;
    }

    /// Retrieve a scope by its name
    public static Scope getScope(String scopeName) {
        return scopes.get(scopeName);
    }

    public static void resetCounter() {
        for (Map.Entry<String, Integer> scopeCounter : scopeCounters.entrySet()) {
            scopeCounter.setValue(0);
        }
    }
}
