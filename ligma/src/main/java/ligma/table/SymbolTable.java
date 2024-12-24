package ligma.table;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;

@Slf4j
public class SymbolTable {

    /// Stack to manage nested scopes
    private static final Deque<Scope> scopes = new ArrayDeque<>();

    private static final int START_ADDRESS = 3;

    private SymbolTable() {
    }

    // Enter a new scope by pushing a new scope onto the stack
    public static void enterScope(boolean isNamedScope) {
        Scope scope = new Scope();

        // Scope is a function
        if (isNamedScope) {
            scope.setNamed(true);
            scope.setNextAddress(START_ADDRESS);
        }
        // Scope is not a function
        else {
            int nextAddress = scopes.isEmpty()
                ? START_ADDRESS
                : scopes.peek().getCurrentAddress();

            scope.setNextAddress(nextAddress);
        }

        scopes.push(scope);
    }

    // Exit the current scope by popping the top map off the stack
    public static void exitScope() {
        if (scopes.isEmpty()) {
            throw new IllegalStateException("Cannot exit scope: No active scope!");
        }

        scopes.pop();
    }

    // Add a descriptor to the current scope
    public static void add(String identifier, Descriptor descriptor) {
        if (scopes.isEmpty()) {
            throw new IllegalStateException("No active scope to add the identifier to.");
        }

        Scope currentScope = scopes.peek();

        // Set address to the identifier
        int address = currentScope.getNextAddress();
        descriptor.setAddres(address);

        currentScope.addDescriptor(identifier, descriptor);
    }

    // Lookup an identifier in the current scope hierarchy
    public static Descriptor lookup(String identifier) {
        for (Scope scope : scopes) {
            if (scope.containsKey(identifier)) {
                return scope.getDescriptor(identifier);
            }
        }
        return null; // Not found
    }

    public static boolean isIdentifierInCurrentScope(String identifier) {
        if (scopes.isEmpty()) {
            throw new IllegalStateException("No active scope to get the identifier from.");
        }
        return scopes.peek().containsKey(identifier);
    }

    public static int getLevel(String identifier) {
        int level = 0;

        for (Scope scope : scopes) {
            if (scope.containsKey(identifier)) {
                return level;
            }
            // Increment level for each named scope
            if (scope.isNamed()) {
                level++;
            }
        }

        return level;
    }

    public static int getNextAddress() {
        if (scopes.isEmpty()) {
            throw new IllegalStateException("No active scope to get the next address.");
        }
        return scopes.peek().getNextAddress();
    }

}
