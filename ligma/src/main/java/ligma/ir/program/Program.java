package ligma.ir.program;

import ligma.ir.function.Function;
import ligma.ir.statement.Statement;

import java.util.List;

public record Program(
    List<Statement> statements,
    List<Function> functions
) {

}
