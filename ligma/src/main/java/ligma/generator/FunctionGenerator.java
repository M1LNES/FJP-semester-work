package ligma.generator;

import ligma.ast.expression.Expression;
import ligma.ast.function.Function;
import ligma.ast.function.FunctionParameter;
import ligma.ast.statement.Statement;
import ligma.enums.Instruction;
import ligma.table.Descriptor;
import ligma.table.FunctionDescriptor;
import ligma.table.VariableDescriptor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@RequiredArgsConstructor
public class FunctionGenerator extends Generator {

    private List<Function> functions;

    private static final ExpressionGenerator expressionGenerator = new ExpressionGenerator();
    private static final StatementGenerator statementGenerator = new StatementGenerator();

    @Override
    public void generate() {
        for (Function function : functions) {
            generateFunction(function);
        }
    }

    private void generateFunction(Function function) {
        symbolTable.enterScope(true);

        String identifier = function.name();
        List<FunctionParameter> parameters = function.parameters();
        List<Statement> statements = function.statements();
        Expression returnExpression = function.returnExpression();

        Descriptor descriptor = FunctionDescriptor.builder()
                                                  .type(function.returnType())
                                                  .scopeLevel(symbolTable.getCurrentScopeLevel())
                                                  .statements(statements)
                                                  .parameters(parameters)
                                                  .returnExpression(returnExpression)
                                                  .build();

        symbolTable.add(identifier, descriptor);

        for (FunctionParameter parameter : parameters) {
            Descriptor paramDescriptor = VariableDescriptor.builder()
                                                           .name(parameter.name())
                                                           .isConstant(false)
                                                           .type(parameter.type())
                                                           .scopeLevel(symbolTable.getCurrentScopeLevel())
                                                           .build();

            symbolTable.add(parameter.name(), paramDescriptor);
        }

        // Allocate space for the Activation Record
        addInstruction(Instruction.INT, 0, 3);
        // Allocate space for the parameters
        addInstruction(Instruction.INT, 0, parameters.size());

        for (int i = parameters.size(); i > 0; i--) {
            addInstruction(Instruction.LOD, 0, -i);
        }

        // Generate function statements
        statementGenerator.setStatements(statements);
        statementGenerator.generate();

        // Generate return epxression
        expressionGenerator.setExpression(returnExpression);
        expressionGenerator.generate();

        // Store the return value to the allocated space from the function caller
        addInstruction(Instruction.STO, 0, -(statements.size() + 1));

        addInstruction(Instruction.RET, 0, 0);

        symbolTable.exitScope();
    }

}
