package ligma.table;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Scope {

    /// Scope name (identifier name)
    private final String name;
    /// Map: Identifier -> Descriptor
    private final Map<String, Descriptor> descriptors = new HashMap<>();
    /// Reference to the parent scope
    private final Scope parent;
    /// Scope level in the hierarchy
    private final int level;
    /// PL/0 addressing starts at index 3
    private static final int STARTING_ADDRESS = 3;
    private int currentAddressCounter;

    public Scope(String name, Scope parent) {
        this.name = name;
        this.parent = parent;
        this.level = (parent == null) ? 0 : parent.level + 1; // Level is parent's level + 1
        currentAddressCounter = STARTING_ADDRESS;
    }

    public int getNextAddress() {
        return currentAddressCounter++;
    }

    public int getStartingAddress() {
        return STARTING_ADDRESS;
    }

    /// Add a descriptor for the specified identifier
    public void addDescriptor(String identifier, Descriptor descriptor) {
        // Variable already exists in this scope
        if (descriptors.containsKey(identifier)) {
            throw new IllegalArgumentException("Duplicate identifier: " + identifier);
        }

        // Increment address in the current scope
//        descriptor.setAddres(addressCounter++);

        descriptors.put(identifier, descriptor);
    }

    /// Return descriptor for the identifier or null if not found
    public Descriptor getDescriptor(String identifier) {
        return descriptors.get(identifier);
    }

    /// Check if the identifier has a descriptor
    public boolean hasDescriptor(String identifier) {
        return descriptors.containsKey(identifier);
    }

}
