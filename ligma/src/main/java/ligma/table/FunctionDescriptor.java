package ligma.table;

import ligma.ir.expression.Expression;
import ligma.ir.function.FunctionParameter;
import ligma.ir.statement.Statement;
import ligma.enums.DataType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class FunctionDescriptor extends Descriptor {

    private final List<FunctionParameter> parameters;
    private final List<Statement> statements;
    private final Expression returnExpression;

    @Builder
    public FunctionDescriptor(String name, DataType type, int scopeLevel, int addres, List<FunctionParameter> parameters, Expression expression, List<Statement> statements, Expression returnExpression) {
        super(name, type, scopeLevel, addres);
        this.parameters = parameters;
        this.statements = statements;
        this.returnExpression = returnExpression;
    }

}
