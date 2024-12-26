package ligma.ir.expression;

import ligma.enums.DataType;
import lombok.Getter;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Represents an identifier expression with a name and a data type.
@Getter
public class Identifier extends Expression {

    /// The name of the identifier.
    private final String name;

    public Identifier(String name, DataType type) {
        super(type);
        this.name = name;
    }

}
