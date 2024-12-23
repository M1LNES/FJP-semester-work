package ligma.generator;

import ligma.ast.expression.Expression;
import ligma.ast.expression.FunctionCallExpression;
import ligma.ast.function.Function;
import ligma.ast.function.FunctionParameter;
import ligma.ast.statement.FunctionCall;
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

    /// Can be FunctionCallStatement or FunctionCallExpression
    private Object functionCall;

    private static final ExpressionGenerator expressionGenerator = new ExpressionGenerator();
    private static final StatementGenerator statementGenerator = new StatementGenerator();

    @Override
    public void generate() {
        generateFunctionCall(functionCall);
    }

    private void generateFunctionCall(Object functionCall) {
        String identifier = "";
        List<Expression> arguments = List.of();

        switch (functionCall) {
            case FunctionCall fnCall -> {
                identifier = fnCall.getIdentifier();
                arguments = fnCall.getArguments();
            }
            case FunctionCallExpression fnCallExpression -> {
                identifier = fnCallExpression.getIdentifier();
                arguments = fnCallExpression.getArguments();
            }
            default -> {}
        }

        Descriptor functionDescriptor = symbolTable.lookup(identifier);

        // Allocate space for the return value
        addInstruction(Instruction.INT, 0, 1);

        // Generate arguments
        for (Expression argument : arguments) {
            expressionGenerator.setExpression(argument);
            expressionGenerator.generate();
        }

        // Call the function
        // Later we can modify the '-1' to the first instruction address of the function
        addInstruction(Instruction.CAL, 0, -1);

        int calIndex = getCurrentInstructionRow();

        // Clear the arguments from the stack
        addInstruction(Instruction.INT, 0, -arguments.size());

        // Jump over the function body
        // Later we can modify the '-1' to the first instruction address after the function
        addInstruction(Instruction.JMP, 0, -1);

        int jmpIndex = getCurrentInstructionRow();

        int functionBodyIndex = getCurrentInstructionRow();

        // TODO: throw Exception if the function does not exist

        // Generate function body
        for (Function function : functions) {
            if (function.name().equals(identifier)) {
                generateFunction(function);
            }
        }

        int afterFunctionBodyIndex = getCurrentInstructionRow();

        modifyInstructionAddress(calIndex, functionBodyIndex + 1);
        modifyInstructionAddress(jmpIndex, afterFunctionBodyIndex + 1);
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
