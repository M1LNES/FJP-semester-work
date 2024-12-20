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

    public Scope(String name, Scope parent) {
        this.name = name;
        this.parent = parent;
        this.level = (parent == null) ? 0 : parent.level + 1; // Level is parent's level + 1
    }

    /// Add a descriptor for the specified identifier
    public void addDescriptor(String identifier, Descriptor descriptor) {
        // Variable already exists in this scope
        if (descriptors.containsKey(identifier)) {
            throw new IllegalArgumentException("Duplicate identifier: " + identifier);
        }

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
