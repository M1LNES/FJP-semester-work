package ligma.table;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Scope {

    private final Map<String, Descriptor> descriptors = new HashMap<>();
    private boolean isNamed;
    private int nextAddress;

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

}
