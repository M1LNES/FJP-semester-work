package ligma.table;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents a single scope in the symbol table hierarchy.
/// Manages descriptors, scope type (named or unnamed), and address allocation.
@Getter
@Setter
public class Scope {

    /// Map to store descriptors for identifiers within the scope
    private final Map<String, Descriptor> descriptors = new HashMap<>();
    /// Indicates whether the scope is named (e.g., a function scope)
    private boolean isNamed;
    /// Tracks the next available address in the scope
    private int nextAddress;

    /// Adds a descriptor to the scope
    public void addDescriptor(String identifier, Descriptor descriptor) {
        descriptors.put(identifier, descriptor);
    }

    /// Retrieves a descriptor by its identifier
    public Descriptor getDescriptor(String identifier) {
        return descriptors.get(identifier);
    }

    /// Allocates and returns the next address, incrementing the address counter
    public int getNextAddress() {
        return nextAddress++;
    }

    /// Returns the current address without incrementing
    public int getCurrentAddress() {
        return nextAddress;
    }

    /// Checks if the scope contains a descriptor for the given identifier
    public boolean containsKey(String identifier) {
        return descriptors.containsKey(identifier);
    }

}
