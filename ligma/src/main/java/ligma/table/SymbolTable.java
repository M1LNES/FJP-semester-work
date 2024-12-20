package ligma.table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    /// Persistent storage for all scopes
    @Getter
    private static final Map<String, Scope> scopes = new HashMap<>();
    /// Active scope
    @Getter
    private static Scope currentScope = null;

    private SymbolTable() {
    }

    /// Enter a new scope
    public static void enterScope(String scopeName) {
        if (scopes.containsKey(scopeName)) {
            throw new IllegalArgumentException("Scope '" + scopeName + "' already exists.");
        }

        Scope newScope = new Scope(scopeName, currentScope); // Set parent to the current scope
        currentScope = newScope; // Update the current scope
        scopes.put(scopeName, newScope); // Persist the scope
    }

    /// Exit the current scope
    public static void exitScope() {
        if (currentScope == null) {
            throw new IllegalStateException("Cannot exit scope: No active scope!");
        }

        currentScope = currentScope.getParent(); // Move up to the parent scope
    }

    /// Add a descriptor to the current scope
    public static boolean add(String identifier, Descriptor descriptor) {
        if (currentScope == null) {
            throw new IllegalStateException("No active scope to add the variable to.");
        }

        return currentScope.addDescriptor(identifier, descriptor);
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

}
