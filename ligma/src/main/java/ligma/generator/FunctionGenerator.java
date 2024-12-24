package ligma.generator;

import ligma.enums.Instruction;
import ligma.exception.GenerateException;
import ligma.ir.expression.Expression;
import ligma.ir.function.Callable;
import ligma.ir.function.Function;
import ligma.ir.function.FunctionParameter;
import ligma.ir.statement.Statement;
import ligma.table.Descriptor;
import ligma.table.SymbolTable;
import ligma.table.VariableDescriptor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Setter
@RequiredArgsConstructor
public class FunctionGenerator extends Generator {

    private static final Map<String, Integer> functionAddresses = new HashMap<>();

    private static final ExpressionGenerator expressionGenerator = new ExpressionGenerator();
    private static final StatementGenerator statementGenerator = new StatementGenerator();

    /// function call - can be statement or expression
    private Callable functionCall;

    @Override
    public void generate() {
        generateFunctionCall(functionCall);
    }

    private void generateFunctionCall(Callable functionCall) {
        log.debug("Generating function call");
        String identifier = functionCall.getIdentifier();
        List<Expression> arguments = functionCall.getArguments();

        // Find function to generate
        Function function = functions.stream()
                                     .filter(fun -> fun.name().equals(identifier))
                                     .findFirst()
                                     .orElseThrow(() -> new GenerateException("Function " + identifier + " not found"));

        List<FunctionParameter> parameters = function.parameters();

        // Argument count is not the same as parameter count
        if (arguments.size() != parameters.size()) {
            throw new GenerateException(
                "Function '" + identifier + "' needs " + parameters.size() + " arguments" +
                " but " + arguments.size() + " was provided"
            );
        }

        // Allocate space for the return value
        addInstruction(Instruction.INT, 0, 1);

        // Generate arguments
        for (int i = 0; i < arguments.size(); i++) {
            Expression argument = arguments.get(i);
            FunctionParameter parameter = parameters.get(i);

            // Argument and parameter have different types
            if (argument.getType() != parameter.type()) {
                throw new GenerateException(
                    "Argument type is " + arguments.get(i).getType() +
                    " but " + parameters.get(i).type() + " was expected"
                );
            }

            // Generate argument
            expressionGenerator.setExpression(argument);
            expressionGenerator.generate();
        }

        // Call the function
        // Later we can modify the '-1' to the first instruction address of the function
        addInstruction(Instruction.CAL, 0, -1);

        int calIndex = getCurrentInstructionRow();

        // Clear the arguments from the stack
        addInstruction(Instruction.INT, 0, -arguments.size());

        // Function was already generated
        if (functionAddresses.containsKey(identifier)) {
            int functionAddress = functionAddresses.get(identifier);
            modifyInstructionAddress(calIndex, functionAddress);
            return;
        }

        // Jump over the function body
        // Later we can modify the '-1' to the first instruction address after the function
        addInstruction(Instruction.JMP, 0, -1);

        int jmpIndex = getCurrentInstructionRow();
        int functionBodyIndex = getCurrentInstructionRow();

        // Generate function body
        generateFunction(function);

        int afterFunctionBodyIndex = getCurrentInstructionRow();

        // Modify the CAL and JMP addresses
        modifyInstructionAddress(calIndex, functionBodyIndex + 1);
        modifyInstructionAddress(jmpIndex, afterFunctionBodyIndex + 1);
    }

    private void generateFunction(Function function) {
        log.debug("Generating function");

        SymbolTable.enterScope(true);

        // Function info
        String identifier = function.name();
        List<FunctionParameter> parameters = function.parameters();
        List<Statement> statements = function.statements();
        Expression returnExpression = function.returnExpression();

        // Add parameters to the symbol table
        addParametersToTheSymbolTable(parameters);

        // Add function address to the map
        int functionAddress = getCurrentInstructionRow();
        functionAddresses.put(identifier, functionAddress + 1);

        // Allocate space for the Activation Record
        addInstruction(Instruction.INT, 0, 3);

        // Load arguments
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
        addInstruction(Instruction.STO, 0, -(parameters.size() + 1));

        // Return
        addInstruction(Instruction.RET, 0, 0);

        SymbolTable.exitScope();

        log.debug("Finished generating function");
    }

    private void addParametersToTheSymbolTable(List<FunctionParameter> parameters) {
        for (FunctionParameter parameter : parameters) {
            Descriptor paramDescriptor = VariableDescriptor.builder()
                .name(parameter.name())
                .type(parameter.type())
                .isConstant(false)
                .build();

            SymbolTable.add(parameter.name(), paramDescriptor);
        }
    }

}
