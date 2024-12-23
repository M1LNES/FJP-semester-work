package ligma.generator;

import ligma.ast.expression.Expression;
import ligma.ast.statement.Assignment;
import ligma.ast.statement.ConstantDefinition;
import ligma.ast.statement.IfStatement;
import ligma.ast.statement.Statement;
import ligma.ast.statement.VariableDefinition;
import ligma.enums.Instruction;
import ligma.enums.ScopeType;
import ligma.table.Descriptor;
import ligma.table.SymbolTable;
import lombok.Setter;

import java.util.ArrayList;
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
                default -> {}
            }
        }
    }

    private void generateVariableDefinition(VariableDefinition varDef) {
        String identifier = varDef.getIdentifier();
        Expression expression = varDef.getExpression();

        Descriptor variableDescriptor = SymbolTable.lookup(identifier);

        // Allocate space for the variable
        addInstruction(Instruction.INT, 0, 1);

        // Put the result of the expression on the top of the stack
        expressionGenerator.setExpression(expression);
        expressionGenerator.generate();

        // Save the result of the expression to the allocated space
        addInstruction(Instruction.STO, 0, variableDescriptor.getAddres());
    }

    private void generateConstantDefinition(ConstantDefinition constDef) {
        String identifier = constDef.getIdentifier();
        Expression expression = constDef.getExpression();

        Descriptor constDescriptor = SymbolTable.lookup(identifier);

        // Allocate space for the variable
        addInstruction(Instruction.INT, 0, 1);

        // Put the result of the expression on the top of the stack
        expressionGenerator.setExpression(expression);
        expressionGenerator.generate();

        // Save the result of the expression to the allocated space
        addInstruction(Instruction.STO, 0, constDescriptor.getAddres());
    }
    private void generateAssignment(Assignment assignment) {
        // Evaluate expression
        Expression expression = assignment.getExpression();
        expressionGenerator.setExpression(expression);
        expressionGenerator.generate();

        List<String> allIdentifiers = assignment.getAllIdentifiers();

        for (int i = 0; i < allIdentifiers.size(); i++) {
            Descriptor descriptor = SymbolTable.lookup(allIdentifiers.get(i));

            // Store the value of the expression to the given identifier
            addInstruction(Instruction.STO, 0, descriptor.getAddres());

            // Return the expression value on the top of the stack
            if (i != allIdentifiers.size() - 1) {
                addInstruction(Instruction.INT, 0, 1);
            }
        }
    }

    private void generateIfStatement(IfStatement ifStatement) {
        SymbolTable.reenterScope(ScopeType.IF.name());

        // Evaluate the condition of the if statement
        Expression expression = ifStatement.getExpression();
        expressionGenerator.setExpression(expression);
        expressionGenerator.generate();

        // Lit 'true' to the top of the stack
        addInstruction(Instruction.LIT, 0, 1);
        // Compare the expression result with the 'true'
        addInstruction(Instruction.OPR, 0, 8);
        // Conditional jump - later we can modify the '-1' to the correct address
        addInstruction(Instruction.JMC, 0, -1);

        int beforeIfRow = getCurrentInstructionRow();

        // Generate all statements in the 'if' body
        List<Statement> ifStatements = ifStatement.getIfStatements();
        setStatements(ifStatements);
        generate();

        SymbolTable.exitScope();

        // TODO: fix the if body (the statements are evaluated badly)

        // Jump over the 'else' body
        // Later we can modify the '-1' to the correct address
        addInstruction(Instruction.JMP, 0, -1);

        int afterIfRow = getCurrentInstructionRow();

        int beforeElseRow = getCurrentInstructionRow();

        // Set the address of JMC to the first instruction of the 'else'
        modifyInstructionAddress(beforeIfRow, afterIfRow + 1);

        SymbolTable.reenterScope(ScopeType.ELSE.name());

        // Generate all statements in the 'if' body
        List<Statement> elseStatements = ifStatement.getElseStatements();
        setStatements(elseStatements);
        generate();

        int afterElseRow = getCurrentInstructionRow();

        modifyInstructionAddress(beforeElseRow, afterElseRow + 1);

        SymbolTable.exitScope();
    }

}
