package ligma.generator;

import ligma.enums.DataType;
import ligma.enums.Instruction;
import ligma.exception.GenerateException;
import ligma.ir.expression.Expression;
import ligma.ir.function.Callable;
import ligma.ir.statement.Assignment;
import ligma.ir.statement.ConstantDefinition;
import ligma.ir.statement.DoWhileLoop;
import ligma.ir.statement.ForLoop;
import ligma.ir.statement.FunctionCall;
import ligma.ir.statement.IfStatement;
import ligma.ir.statement.RepeatUntilLoop;
import ligma.ir.statement.Statement;
import ligma.ir.statement.VariableDefinition;
import ligma.ir.statement.WhileLoop;
import ligma.table.Descriptor;
import ligma.table.SymbolTable;
import ligma.table.VariableDescriptor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Generates statement PL/0 instructions, including variable definitions, assignments, loops, etc.
@Slf4j
@Setter
public class StatementGenerator extends Generator {

    /// Expression generator used to evaluate expressions in statements.
    private static final ExpressionGenerator expressionGenerator = new ExpressionGenerator();
    /// Function generator used to handle function calls.
    private static final FunctionGenerator functionGenerator = new FunctionGenerator();

    /// List of statements to be generated.
    private List<Statement> statements;

    /// Generates PL/0 instructions for the statements by iterating through each and delegating to the appropriate method based on the statement type.
    @Override
    public void generate() {
        // Generate statements
        for (Statement statement : statements) {
            switch (statement) {
                case VariableDefinition varDef -> generateVariableDefinition(varDef);
                case ConstantDefinition constDef -> generateConstantDefinition(constDef);
                case Assignment assignment -> generateAssignment(assignment);
                case IfStatement ifStatement -> generateIfStatement(ifStatement);
                case ForLoop forLoop -> generateForLoop(forLoop);
                case WhileLoop whileLoop -> generateWhile(whileLoop);
                case DoWhileLoop doWhileLoop -> generateDoWhile(doWhileLoop);
                case RepeatUntilLoop repeatUntilLoop -> generateRepeatUntil(repeatUntilLoop);
                case FunctionCall functionCall -> generateFunctionCall(functionCall);
                default -> {}
            }
        }
    }

    /// Generates PL/0 instructions for a variable definition.
    ///
    /// @param varDef The variable definition to process.
    private void generateVariableDefinition(VariableDefinition varDef) {
        log.debug("Generating variable definition");
        String identifier = varDef.getIdentifier();
        Expression expression = varDef.getExpression();
        DataType dataType = varDef.getType();

        Descriptor descriptor = VariableDescriptor.builder()
                                                  .name(identifier)
                                                  .type(dataType)
                                                  .isConstant(false)
                                                  .build();

        SymbolTable.add(identifier, descriptor);

        // Allocate space for the variable
        addInstruction(Instruction.INT, 0, 1);

        // Put the result of the expression on the top of the stack
        expressionGenerator.setExpression(expression);
        expressionGenerator.generate();

        // If its a function -> set data type to a function return type
        if (expression instanceof Callable callable) {
            DataType functionReturnType = getFunctionReturnType(callable.getIdentifier());
            expression.setType(functionReturnType);
        }

        // Check data types (needed because of function return types)
        validateAssignmentType(descriptor, expression);

        // Save the result of the expression to the allocated space
        addInstruction(Instruction.STO, SymbolTable.getLevel(identifier), descriptor.getAddres());
    }

    /// Generates PL/0 instructions for a constant definition.
    ///
    /// @param constDef The constant definition to process.
    private void generateConstantDefinition(ConstantDefinition constDef) {
        log.debug("Generating constant definition");
        String identifier = constDef.getIdentifier();
        Expression expression = constDef.getExpression();

        Descriptor descriptor = VariableDescriptor.builder()
                                                  .name(identifier)
                                                  .type(constDef.getType())
                                                  .isConstant(false)
                                                  .build();

        SymbolTable.add(identifier, descriptor);

        // Allocate space for the variable
        addInstruction(Instruction.INT, 0, 1);

        // Put the result of the expression on the top of the stack
        expressionGenerator.setExpression(expression);
        expressionGenerator.generate();

        // If its a function -> set data type to a function return type
        if (expression instanceof Callable callable) {
            DataType functionReturnType = getFunctionReturnType(callable.getIdentifier());
            expression.setType(functionReturnType);
        }

        // Check data types (needed because of function return types)
        validateAssignmentType(descriptor, expression);

        // Save the result of the expression to the allocated space
        addInstruction(Instruction.STO, 0, descriptor.getAddres());
    }

    /// Generates PL/0 instructions for an assignment.
    ///
    /// @param assignment The assignment to process.
    private void generateAssignment(Assignment assignment) {
        log.debug("Generating assignment");

        // Evaluate expression
        Expression expression = assignment.getExpression();
        expressionGenerator.setExpression(expression);
        expressionGenerator.generate();

        List<String> allIdentifiers = assignment.getAllIdentifiers();

        for (int i = 0; i < allIdentifiers.size(); i++) {
            String identifier = allIdentifiers.get(i);
            Descriptor descriptor = SymbolTable.lookup(identifier);

            // If its a function -> set data type to a function return type
            if (expression instanceof Callable callable) {
                DataType functionReturnType = getFunctionReturnType(callable.getIdentifier());
                expression.setType(functionReturnType);
            }

            // Check data types (needed because of function return types)
            validateAssignmentType(descriptor, expression);

            // Store the value of the expression to the given identifier
            addInstruction(Instruction.STO, SymbolTable.getLevel(identifier), descriptor.getAddres());

            // Return the expression value on the top of the stack
            if (i != allIdentifiers.size() - 1) {
                addInstruction(Instruction.INT, 0, 1);
            }
        }
    }

    /// Generates PL/0 instructions for an if statement.
    ///
    /// @param ifStatement The if statement to process.
    private void generateIfStatement(IfStatement ifStatement) {
        log.debug("Generating if statement");

        SymbolTable.enterScope(false);

        // Evaluate the condition of the 'if' statement
        Expression expression = ifStatement.getExpression();
        expressionGenerator.setExpression(expression);
        expressionGenerator.generate();

        // Conditional jump - later we can modify the '-1' to the correct address
        addInstruction(Instruction.JMC, 0, -1);

        int beforeIfRow = getCurrentInstructionRow();

        // Generate all statements in the 'if' body
        List<Statement> ifStatements = ifStatement.getIfStatements();
        setStatements(ifStatements);
        generate();

        // Jump over the 'else' body
        // Later we can modify the '-1' to the correct address
        addInstruction(Instruction.JMP, 0, -1);

        int afterIfRow = getCurrentInstructionRow();

        int beforeElseRow = getCurrentInstructionRow();

        // Clear the scope variables
        addInstruction(Instruction.INT, 0, -SymbolTable.getCurrentScopeSize());

        SymbolTable.exitScope();

        // Set the address of JMC to the first instruction of the 'else'
        modifyInstructionAddress(beforeIfRow, afterIfRow + 1);

        SymbolTable.enterScope(false);

        // Generate all statements in the 'if' body
        List<Statement> elseStatements = ifStatement.getElseStatements();
        setStatements(elseStatements);
        generate();

        int afterElseRow = getCurrentInstructionRow();

        modifyInstructionAddress(beforeElseRow, afterElseRow + 1);

        // Clear the scope variables
        addInstruction(Instruction.INT, 0, -SymbolTable.getCurrentScopeSize());

        SymbolTable.exitScope();
    }

    /// Generates PL/0 instructions for a for loop.
    ///
    /// @param forLoop The for loop to process.
    private void generateForLoop(ForLoop forLoop) {
        log.debug("Generating for loop");

        SymbolTable.enterScope(false);

        String identifier = forLoop.getIdentifier();
        Descriptor descriptor = VariableDescriptor.builder()
                                                  .name(identifier)
                                                  .type(DataType.INT)
                                                  .isConstant(false)
                                                  .build();

        SymbolTable.add(identifier, descriptor);

        addInstruction(Instruction.INT, 0, 1);

        // Evaluate the variable definition in the 'for' header
        Expression expression = forLoop.getExpression();
        expressionGenerator.setExpression(expression);
        expressionGenerator.generate();

        addInstruction(Instruction.STO, SymbolTable.getLevel(identifier), descriptor.getAddres());

        int startIndex = getCurrentInstructionRow();

        addInstruction(Instruction.LOD, SymbolTable.getLevel(identifier), descriptor.getAddres());

        // Evaluate the assigment int the 'for' header
        Expression toExpression = forLoop.getToExpression();
        expressionGenerator.setExpression(toExpression);
        expressionGenerator.generate();

        // Compare '<'
        addInstruction(Instruction.OPR, 0, 10);

        // Jump over the 'for' body
        addInstruction(Instruction.JMC, 0, -1);

        int beforeForBody = getCurrentInstructionRow();

        // Generate 'for' statements
        List<Statement> forStatements = forLoop.getStatements();
        setStatements(forStatements);
        generate();

        // Default increment by 1
        addInstruction(Instruction.LOD, SymbolTable.getLevel(identifier), descriptor.getAddres());
        addInstruction(Instruction.LIT, 0, 1);
        addInstruction(Instruction.OPR, 0, 2);
        addInstruction(Instruction.STO, SymbolTable.getLevel(identifier), descriptor.getAddres());

        addInstruction(Instruction.JMP, 0, startIndex + 1);

        int afterForBody = getCurrentInstructionRow();

        modifyInstructionAddress(beforeForBody, afterForBody + 1);

        // Clear the scope variables
        addInstruction(Instruction.INT, 0, -SymbolTable.getCurrentScopeSize());

        SymbolTable.exitScope();
    }

    /// Generates PL/0 instructions for a while loop.
    ///
    /// @param whileLoop The while loop to process.
    private void generateWhile(WhileLoop whileLoop) {
        log.debug("Generating while loop");

        SymbolTable.enterScope(false);

        int beforeCondition = getCurrentInstructionRow();

        // Evaluate the condition of the 'while' loop
        Expression expression = whileLoop.getExpression();
        expressionGenerator.setExpression(expression);
        expressionGenerator.generate();

        // Jump over the 'while' body
        // Conditional jump - later we can modify the '-1' to the correct address
        addInstruction(Instruction.JMC, 0, -1);

        int jmcIndex = getCurrentInstructionRow();

        // Generate all statements in the 'while' body
        List<Statement> whileStatements = whileLoop.getStatements();
        setStatements(whileStatements);
        generate();

        // Clear the scope variables
        addInstruction(Instruction.INT, 0, -SymbolTable.getCurrentScopeSize());

        // Jump to the first address in the 'while' body
        addInstruction(Instruction.JMP, 0, beforeCondition + 1);

        // Modify the JMC instruction to jump over the 'while' body
        modifyInstructionAddress(jmcIndex, getCurrentInstructionRow() + 1);

        SymbolTable.exitScope();
    }

    /// Generates PL/0 instructions for a do-while loop.
    ///
    /// @param doWhileLoop The do-while loop to process.
    private void generateDoWhile(DoWhileLoop doWhileLoop) {
        log.debug("Generating do while loop");

        SymbolTable.enterScope(false);

        int doBodyStart = getCurrentInstructionRow();

        // Generate all statements in the 'do' body
        List<Statement> doWhileStatements = doWhileLoop.getStatements();
        setStatements(doWhileStatements);
        generate();

        // Evaluate the condition
        Expression expression = doWhileLoop.getExpression();
        expressionGenerator.setExpression(expression);
        expressionGenerator.generate();

        // Jump over the 'do-while' body
        // Conditional jump - later we can modify the '-1' to the correct address
        addInstruction(Instruction.JMC, 0, -1);

        int jmcIndex = getCurrentInstructionRow();

        // Clear the scope variables
        addInstruction(Instruction.INT, 0, -SymbolTable.getCurrentScopeSize());

        addInstruction(Instruction.JMP, 0, doBodyStart + 1);

        modifyInstructionAddress(jmcIndex, getCurrentInstructionRow() + 1);

        // Clear the scope variables
        addInstruction(Instruction.INT, 0, -SymbolTable.getCurrentScopeSize());

        SymbolTable.exitScope();
    }

    /// Generates PL/0 instructions for a repeat-until loop.
    ///
    /// @param repeatUntilLoop The repeat-until loop to process.
    private void generateRepeatUntil(RepeatUntilLoop repeatUntilLoop) {
        log.debug("Generating repeat until loop");

        SymbolTable.enterScope(false);

        int repeatBodyStart = getCurrentInstructionRow();

        // Generate all statements in the 'repeat' body
        List<Statement> doWhileStatements = repeatUntilLoop.getStatements();
        setStatements(doWhileStatements);
        generate();

        // Clear the scope variables
        addInstruction(Instruction.INT, 0, -SymbolTable.getCurrentScopeSize());

        // Evaluate the condition
        Expression expression = repeatUntilLoop.getExpression();
        expressionGenerator.setExpression(expression);
        expressionGenerator.generate();

        // Jump to the start of the 'repeat-until' body
        addInstruction(Instruction.JMC, 0, repeatBodyStart + 1);

        SymbolTable.exitScope();
    }

    /// Generates PL/0 instructions for a function call.
    ///
    /// @param functionCall The function call to process.
    private void generateFunctionCall(FunctionCall functionCall) {
        functionGenerator.setFunctionCall(functionCall);
        functionGenerator.generate();
    }

    /// Validates that the assignment is compatible with the data type.
    ///
    /// @param descriptor The descriptor of the variable to which the value will be assigned.
    /// @param expression The expression to be assigned.
    /// @throws GenerateException if the data types do not match.
    private void validateAssignmentType(Descriptor descriptor, Expression expression) {
        // Validate that the data types are compatible (needed because of functions)
        if (descriptor.getType() != expression.getType()) {
            throw new GenerateException(
                "Cannot assign a value of type " + expression.getType() +
                " to variable '" + descriptor.getName() +
                "' because it is of type " + descriptor.getType());
        }
    }
}
