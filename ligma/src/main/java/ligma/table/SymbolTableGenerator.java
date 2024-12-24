package ligma.table;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
class GeneratorScope {
    private final Map<String, Descriptor> descriptors = new HashMap<>();
    private int nextAddress;
    private int level;

    public void addDescriptor(String identifier, Descriptor descriptor) {
        descriptors.put(identifier, descriptor);
    }

    public Descriptor getDescriptor(String identifier) {
        return descriptors.get(identifier);
    }

    public int getNextAddress() {
        return nextAddress++;
    }

    public int getCurrentAddress() {
        return nextAddress;
    }

    public boolean containsKey(String identifier) {
        return descriptors.containsKey(identifier);
    }

    public void decrementNextAddress(int decrement) {
        nextAddress -= decrement;
    }

}

public class SymbolTableGenerator {

    // Stack to manage nested scopes
    private final Deque<GeneratorScope> scopes;

    private static final int START_ADDRESS = 3;

    // Constructor to initialize the stack
    public SymbolTableGenerator() {
        scopes = new ArrayDeque<>();
    }

    // Enter a new scope by pushing a new scope onto the stack
    public void enterScope(boolean isNamedScope) {
        GeneratorScope scope = new GeneratorScope();

        // Scope is a function
        if (isNamedScope) {
            scope.setNextAddress(START_ADDRESS);
            scope.setLevel(scope.getLevel() + 1);
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
    public void exitScope() {
        if (scopes.isEmpty()) {
            throw new IllegalStateException("Cannot exit scope: No active scope!");
        }

        scopes.pop();
    }

    // Add a descriptor to the current scope
    public void add(String identifier, Descriptor descriptor) {
        if (scopes.isEmpty()) {
            throw new IllegalStateException("No active scope to add the identifier to.");
        }

        GeneratorScope currentScope = scopes.peek();

        int address = currentScope.getNextAddress();
        descriptor.setAddres(address);

        currentScope.addDescriptor(identifier, descriptor);
    }

    // Lookup an identifier in the current scope hierarchy
    public Descriptor lookup(String identifier) {
        for (GeneratorScope scope : scopes) {
            if (scope.containsKey(identifier)) {
                return scope.getDescriptors().get(identifier);
            }
        }
        return null; // Not found
    }

    public int getLevel(Descriptor descriptor) {
        return scopes.peek().getLevel() - descriptor.getScopeLevel();
    }

    // Check if an identifier exists in the current scope
    public boolean isIdentifierInCurrentScope(String identifier) {
        if (scopes.isEmpty()) {
            throw new IllegalStateException("No active scope to find the identifier in.");
        }
        return scopes.peek().containsKey(identifier);
    }

    public int getCurrentScopeLevel() {
        return scopes.peek().getLevel();
    }

    public int getNextAddress() {
        return scopes.peek().getNextAddress();
    }

    public void decrementCurrentScopeNextAddress(int decrement) {
        scopes.peek().decrementNextAddress(decrement);
    }

}
