package ligma.generator;

import ligma.enums.DataType;
import ligma.enums.Instruction;
import ligma.ir.expression.Expression;
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

@Slf4j
@Setter
public class StatementGenerator extends Generator {

    private static final ExpressionGenerator expressionGenerator = new ExpressionGenerator();
    private static final FunctionGenerator functionGenerator = new FunctionGenerator();

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
                case FunctionCall functionCall -> generateFunctionCall(functionCall);
                default -> {}
            }
        }
    }

    private void generateVariableDefinition(VariableDefinition varDef) {
        log.debug("Generating variable definition");
        String identifier = varDef.getIdentifier();
        Expression expression = varDef.getExpression();

        Descriptor descriptor = VariableDescriptor.builder()
                                                  .name(identifier)
                                                  .type(varDef.getType())
                                                  .isConstant(false)
                                                  .build();

        SymbolTable.add(identifier, descriptor);

        // Allocate space for the variable
        addInstruction(Instruction.INT, 0, 1);

        // Put the result of the expression on the top of the stack
        expressionGenerator.setExpression(expression);
        expressionGenerator.generate();

        // Save the result of the expression to the allocated space
        addInstruction(Instruction.STO, SymbolTable.getLevel(identifier), descriptor.getAddres());
    }

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

        // Save the result of the expression to the allocated space
        addInstruction(Instruction.STO, 0, descriptor.getAddres());
    }

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

            // Store the value of the expression to the given identifier
            addInstruction(Instruction.STO, SymbolTable.getLevel(identifier), descriptor.getAddres());

            // Return the expression value on the top of the stack
            if (i != allIdentifiers.size() - 1) {
                addInstruction(Instruction.INT, 0, 1);
            }
        }
    }

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

        SymbolTable.exitScope();

        int afterIfRow = getCurrentInstructionRow();

        int beforeElseRow = getCurrentInstructionRow();

        // Set the address of JMC to the first instruction of the 'else'
        modifyInstructionAddress(beforeIfRow, afterIfRow + 1);

        SymbolTable.enterScope(false);

        // Generate all statements in the 'if' body
        List<Statement> elseStatements = ifStatement.getElseStatements();
        setStatements(elseStatements);
        generate();

        int afterElseRow = getCurrentInstructionRow();

        modifyInstructionAddress(beforeElseRow, afterElseRow + 1);

        SymbolTable.exitScope();
    }

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

        // Clear the counter
        addInstruction(Instruction.INT, 0, -1);

        modifyInstructionAddress(beforeForBody, afterForBody + 1);

        SymbolTable.exitScope();
    }

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

        // Jump to the first address in the 'while' body
        addInstruction(Instruction.JMP, 0, beforeCondition + 1);

        // Modify the JMC instruction to jump over the 'while' body
        modifyInstructionAddress(jmcIndex, getCurrentInstructionRow() + 1);

        SymbolTable.exitScope();
    }

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

        addInstruction(Instruction.JMP, 0, doBodyStart + 1);

        modifyInstructionAddress(jmcIndex, getCurrentInstructionRow() + 1);

        SymbolTable.exitScope();
    }

    private void generateRepeatUntil(RepeatUntilLoop repeatUntilLoop) {
        log.debug("Generating repeat until loop");

        SymbolTable.enterScope(false);

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

        SymbolTable.exitScope();
    }

    private void generateFunctionCall(FunctionCall functionCall) {
        functionGenerator.setFunctionCall(functionCall);
        functionGenerator.generate();
    }
}
