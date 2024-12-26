package ligma.table;

import ligma.exception.SemanticException;

import java.util.ArrayDeque;
import java.util.Deque;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// A class to manage a symbol table with nested scopes
/// allowing addition, lookup, and manipulation of descriptors.
public class SymbolTable {

    /// Stack to manage nested scopes
    private static final Deque<Scope> scopes = new ArrayDeque<>();

    /// Starting address for named scopes
    private static final int START_ADDRESS = 3;

    /// Private constructor to prevent instantiation
    private SymbolTable() {
    }

    /// Enter a new scope by pushing a new scope onto the stack
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

    /// Exit the current scope by popping the top map off the stack
    public static void exitScope() {
        ensureScopeIsNotEmpty("Cannot exit scope: No active scope!");

        scopes.pop();
    }

    /// Get the size of the current scope
    public static int getCurrentScopeSize() {
        ensureScopeIsNotEmpty("Cannot get current scope size: No active scope!");

        return scopes.peek().getDescriptors().size();
    }

    /// Add a descriptor to the current scope
    public static void add(String identifier, Descriptor descriptor) {
        ensureScopeIsNotEmpty("No active scope to add the identifier to.");

        Scope currentScope = scopes.peek();

        // Duplicate identifier
        if (currentScope.containsKey(identifier)) {
            throw new SemanticException("Identifier '" + identifier + "' already exists in the current scope.");
        }

        // Set address to the identifier
        int address = currentScope.getNextAddress();
        descriptor.setAddres(address);

        currentScope.addDescriptor(identifier, descriptor);
    }

    /// Lookup an identifier in the scope hierarchy
    public static Descriptor lookup(String identifier) {
        for (Scope scope : scopes) {
            if (scope.containsKey(identifier)) {
                return scope.getDescriptor(identifier);
            }
        }

        // Not found
        return null;
    }

    /// Check if an identifier exists in the current scope
    public static boolean isIdentifierInCurrentScope(String identifier) {
        ensureScopeIsNotEmpty("No active scope to get the identifier from.");

        return scopes.peek().containsKey(identifier);
    }

    /// Get the nesting level of an identifier
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

    /// Get the next address in the current scope
    public static int getNextAddress() {
        ensureScopeIsNotEmpty("Cannot get next address: No active scope!");

        return scopes.peek().getNextAddress();
    }

    /// Check if scopes are not empty
    private static void ensureScopeIsNotEmpty(String message) {
        if (scopes.isEmpty()) {
            throw new SemanticException(message);
        }
    }

}
