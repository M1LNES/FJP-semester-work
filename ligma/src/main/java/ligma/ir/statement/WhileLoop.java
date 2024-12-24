package ligma.ir.statement;

import ligma.ir.expression.Expression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class WhileLoop extends Statement {

    private final Expression expression;
    private final List<Statement> statements;

}
