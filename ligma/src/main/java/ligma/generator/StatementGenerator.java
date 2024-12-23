package ligma.generator;

import ligma.ast.expression.Expression;
import ligma.ast.statement.Assignment;
import ligma.ast.statement.ConstantDefinition;
import ligma.ast.statement.DoWhileLoop;
import ligma.ast.statement.IfStatement;
import ligma.ast.statement.RepeatUntilLoop;
import ligma.ast.statement.Statement;
import ligma.ast.statement.VariableDefinition;
import ligma.ast.statement.WhileLoop;
import ligma.enums.Instruction;
import ligma.exception.GenerateException;
import ligma.table.Descriptor;
import ligma.table.VariableDescriptor;
import lombok.Setter;

import java.util.List;

@Setter
public class StatementGenerator extends Generator {

    private static ExpressionGenerator expressionGenerator = new ExpressionGenerator();

    private List<Statement> statements;

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
                default -> {}
            }
        }
    }

    private void generateVariableDefinition(VariableDefinition varDef) {
        String identifier = varDef.getIdentifier();
        Expression expression = varDef.getExpression();

        if (symbolTable.isIdentifierInCurrentScope(identifier)) {
            throw new GenerateException("Identifier '" + identifier + "' was already declared");
        }

        Descriptor descriptor = VariableDescriptor.builder()
                                                  .name(identifier)
                                                  .type(varDef.getType())
                                                  .isConstant(false)
                                                  .scopeLevel(symbolTable.getCurrentScopeLevel())
                                                  .build();

        symbolTable.add(identifier, descriptor);

        // Allocate space for the variable
        addInstruction(Instruction.INT, 0, 1);

        // Put the result of the expression on the top of the stack
        expressionGenerator.setExpression(expression);
        expressionGenerator.generate();

        // Save the result of the expression to the allocated space
        addInstruction(Instruction.STO, 0, descriptor.getAddres());
    }

    private void generateConstantDefinition(ConstantDefinition constDef) {
        String identifier = constDef.getIdentifier();
        Expression expression = constDef.getExpression();

        if (symbolTable.isIdentifierInCurrentScope(identifier)) {
            throw new GenerateException("Identifier '" + identifier + "' was already declared");
        }

        Descriptor descriptor = VariableDescriptor.builder()
                                                  .name(identifier)
                                                  .type(constDef.getType())
                                                  .isConstant(false)
                                                  .scopeLevel(symbolTable.getCurrentScopeLevel())
                                                  .build();

        symbolTable.add(identifier, descriptor);

        // Allocate space for the variable
        addInstruction(Instruction.INT, 0, 1);

        // Put the result of the expression on the top of the stack
        expressionGenerator.setExpression(expression);
        expressionGenerator.generate();

        // Save the result of the expression to the allocated space
        addInstruction(Instruction.STO, 0, descriptor.getAddres());
    }

    private void generateAssignment(Assignment assignment) {
        // Evaluate expression
        Expression expression = assignment.getExpression();
        expressionGenerator.setExpression(expression);
        expressionGenerator.generate();

        List<String> allIdentifiers = assignment.getAllIdentifiers();

        for (int i = 0; i < allIdentifiers.size(); i++) {
            Descriptor descriptor = symbolTable.lookup(allIdentifiers.get(i));

            // Store the value of the expression to the given identifier
            addInstruction(Instruction.STO, 0, descriptor.getAddres());

            // Return the expression value on the top of the stack
            if (i != allIdentifiers.size() - 1) {
                addInstruction(Instruction.INT, 0, 1);
            }
        }
    }

    private void generateIfStatement(IfStatement ifStatement) {
        symbolTable.enterScope(false);

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

        symbolTable.exitScope();

        int afterIfRow = getCurrentInstructionRow();

        int beforeElseRow = getCurrentInstructionRow();

        // Set the address of JMC to the first instruction of the 'else'
        modifyInstructionAddress(beforeIfRow, afterIfRow + 1);

        symbolTable.enterScope(false);

        // Generate all statements in the 'if' body
        List<Statement> elseStatements = ifStatement.getElseStatements();
        setStatements(elseStatements);
        generate();

        int afterElseRow = getCurrentInstructionRow();

        modifyInstructionAddress(beforeElseRow, afterElseRow + 1);

        symbolTable.exitScope();
    }
    private void generateForLoop(ForLoop forLoop) {
        symbolTable.enterScope(false);

        Descriptor descriptor = VariableDescriptor.builder()
                                                  .name(forLoop.getIdentifier())
                                                  .type(DataType.INT)
                                                  .isConstant(false)
                                                  .scopeLevel(symbolTable.getCurrentScopeLevel())
                                                  .build();

        symbolTable.add(forLoop.getIdentifier(), descriptor);

        addInstruction(Instruction.INT, 0, 1);

        // Evaluate the variable definition in the 'for' header
        Expression expression = forLoop.getExpression();
        expressionGenerator.setExpression(expression);
        expressionGenerator.generate();

        addInstruction(Instruction.STO, 0, descriptor.getAddres());

        int startIndex = getCurrentInstructionRow();

        addInstruction(Instruction.LOD, 0, descriptor.getAddres());

        // Evaluate the assigment int the 'for' header
        Expression toExpression = forLoop.getToExpression();
        expressionGenerator.setExpression(toExpression);
        expressionGenerator.generate();

        // Compare
        addInstruction(Instruction.OPR, 0, 10);

        // Jump over the 'for' body
        addInstruction(Instruction.JMC, 0, -1);

        int beforeForBody = getCurrentInstructionRow();

        // Generate 'for' statements
        List<Statement> statements = forLoop.getStatements();
        setStatements(statements);
        generate();

        // Default increment by 1
        addInstruction(Instruction.LOD, 0, descriptor.getAddres());
        addInstruction(Instruction.LIT, 0, 1);
        addInstruction(Instruction.OPR, 0, 2);
        addInstruction(Instruction.STO, 0, descriptor.getAddres());

        addInstruction(Instruction.JMP, 0, startIndex + 1);

        int afterForBody = getCurrentInstructionRow();

        modifyInstructionAddress(beforeForBody, afterForBody + 1);

        symbolTable.exitScope();

    }

    private void generateWhile(WhileLoop whileLoop) {
        symbolTable.enterScope(false);

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

        // Jump to the first address in the 'while' body
        addInstruction(Instruction.JMP, 0, beforeCondition + 1);

        // Modify the JMC instruction to jump over the 'while' body
        modifyInstructionAddress(jmcIndex, getCurrentInstructionRow() + 1);

        symbolTable.exitScope();
    }

    private void generateDoWhile(DoWhileLoop doWhileLoop) {
        symbolTable.enterScope(false);

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

        addInstruction(Instruction.JMP, 0, doBodyStart + 1);

        modifyInstructionAddress(jmcIndex, getCurrentInstructionRow() + 1);

        symbolTable.exitScope();
    }

    private void generateRepeatUntil(RepeatUntilLoop repeatUntilLoop) {
        symbolTable.enterScope(false);

        int repeatBodyStart = getCurrentInstructionRow();

        // Generate all statements in the 'repeat' body
        List<Statement> doWhileStatements = repeatUntilLoop.getStatements();
        setStatements(doWhileStatements);
        generate();

        // Evaluate the condition
        Expression expression = repeatUntilLoop.getExpression();
        expressionGenerator.setExpression(expression);
        expressionGenerator.generate();

        // Jump to the start of the 'repeat-until' body
        addInstruction(Instruction.JMC, 0, repeatBodyStart + 1);

        symbolTable.exitScope();
    }

    private void generateFunctionCall(FunctionCall functionCall) {
        String identifier = functionCall.getIdentifier();
        List<Expression> arguments = functionCall.getArguments();

        Descriptor functionDescriptor = symbolTable.lookup(identifier);

        // Allocate space for the return value
        addInstruction(Instruction.INT, 0, 1);

        // Generate arguments
        for (Expression argument : arguments) {
            expressionGenerator.setExpression(argument);
            expressionGenerator.generate();
        }

        // Call the function
        addInstruction(Instruction.CAL, functionDescriptor.getScopeLevel(), functionDescriptor.getAddres());

        // Clear the arguments from the stack
        addInstruction(Instruction.INT, 0, arguments.size());
    }
}
