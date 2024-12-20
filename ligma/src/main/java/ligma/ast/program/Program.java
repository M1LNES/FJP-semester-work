package ligma.ast.program;

import ligma.ast.function.Function;
import ligma.ast.statement.Statement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Program {

    private final List<Statement> statements;
    private final List<Function> functions;

}
