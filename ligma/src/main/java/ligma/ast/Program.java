package ligma.ast;

import ligma.ast.statement.Statement;

import java.util.List;

public record Program(
    List<Statement> statements
) {

}
