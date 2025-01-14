package ligma.generator;

import ligma.enums.Instruction;
import ligma.enums.Operator;
import ligma.exception.GenerateException;
import ligma.ir.expression.AdditiveExpression;
import ligma.ir.expression.ComparisonExpression;
import ligma.ir.expression.Expression;
import ligma.ir.expression.FunctionCallExpression;
import ligma.ir.expression.Identifier;
import ligma.ir.expression.Literal;
import ligma.ir.expression.LogicalExpression;
import ligma.ir.expression.MultiplicativeExpression;
import ligma.ir.expression.NotExpression;
import ligma.ir.expression.ParenthesizedExpression;
import ligma.ir.expression.PowerExpression;
import ligma.ir.expression.UnaryMinusExpression;
import ligma.ir.expression.UnaryPlusExpression;
import ligma.table.Descriptor;
import ligma.table.SymbolTable;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Generates code for various types of expressions.
@Slf4j
@Setter
public class ExpressionGenerator extends Generator {

    /// Static instance of FunctionGenerator for function-related expression generation
    private static final FunctionGenerator functionGenerator = new FunctionGenerator();

    /// The current expression being generated
    private Expression expression;

    /// Generates the appropriate PL/0 instructions based on the expression type.
    @Override
    public void generate() {
        switch (expression) {
            case PowerExpression powerExpression -> generatePowerExpression(powerExpression);
            case UnaryMinusExpression unaryMinusExpression -> genUnaryMinusExpression(unaryMinusExpression);
            case UnaryPlusExpression unaryPlusExpression -> genUnaryPlusExpression(unaryPlusExpression);
            case NotExpression notExpression -> genNotExpression(notExpression);
            case MultiplicativeExpression multiplicativeExpression -> genMultiplicativeExpression(multiplicativeExpression);
            case AdditiveExpression additiveExpression -> genAdditiveExpression(additiveExpression);
            case ComparisonExpression comparisonExpression -> genComparisonExpression(comparisonExpression);
            case LogicalExpression logicalExpression -> genLogicalExpression(logicalExpression);
            case ParenthesizedExpression parenthesizedExpression -> genParenthesizedExpression(parenthesizedExpression);
            case Identifier identifier -> genIdentifierExpression(identifier);
            case Literal<?> literal -> generateLiteral(literal);
            case FunctionCallExpression functionCallExpression -> generateFunctionCallExpression(functionCallExpression);
            default -> {}
        }
    }

    /// Helper method to trigger the generation of an expression.
    /// Sets the current expression and calls the generate method.
    ///
    /// @param expression the expression to generate
    private void generateExpression(Expression expression) {
        this.expression = expression;
        generate();
    }

    /// Generates the PL/0 instructions for a power expression (e.g., 2 ^ 5).
    ///
    /// @param powerExpression the power expression to generate
    private void generatePowerExpression(PowerExpression powerExpression) {
        log.debug("Generating power expression");
        Expression left = powerExpression.getLeft();
        Expression right = powerExpression.getRight();

        // Allocate space in the stack for the return value
        addInstruction(Instruction.INT, 0, 1);

        // Call the function
        // Later we can modify the '-1' to the correct address
        addInstruction(Instruction.CAL, 0, -1);

        int calIndex = getCurrentInstructionRow();

        // Jump over the function instructions
        // Later we can modify the '-1' to the correct address
        addInstruction(Instruction.JMP, 0, -1);

        int jmpIndex = getCurrentInstructionRow();
        int functionBodyIndex = getCurrentInstructionRow();

        // Enter function scope
        SymbolTable.enterScope(true);

        // Allocate space in the stack for the Activation Record and result
        addInstruction(Instruction.INT, 0, 4);

        // Initialize result to 1 (push 1 to stack)
        addInstruction(Instruction.LIT, 0, 1);

        // Save 1 to the result
        int resultAddress = SymbolTable.getNextAddress();
        addInstruction(Instruction.STO, 0, resultAddress);

        // Base
        generateExpression(left);

        // Save base address
        int baseAddress = SymbolTable.getNextAddress();

        // Exponent as counter
        generateExpression(right);

        // Save counter to the stack
        int counterAddress = SymbolTable.getNextAddress();

        // Loop address of the power expression
        int loopStart = getCurrentInstructionRow();

        // Load counter
        addInstruction(Instruction.LOD, 0, counterAddress);

        // Test if counter != 0
        addInstruction(Instruction.LIT, 0, 0);
        addInstruction(Instruction.OPR, 0, 9);

        // Jump to the end of the power expression
        // Later we can modify the '-1' to the correct address
        addInstruction(Instruction.JMC, 0, -1);

        // JMC address
        int jmcIndex = getCurrentInstructionRow();

        // Load result and base
        addInstruction(Instruction.LOD, 0, resultAddress);
        addInstruction(Instruction.LOD, 0, baseAddress);

        // Multiply top two values (result * base)
        addInstruction(Instruction.OPR, 0, 4);

        // Save multiplication result to the result
        addInstruction(Instruction.STO, 0, resultAddress);

        // Subtract 1 from counter
        addInstruction(Instruction.LOD, 0, counterAddress);
        addInstruction(Instruction.LIT, 0, 1);
        addInstruction(Instruction.OPR, 0, 3);

        // Save new counter
        addInstruction(Instruction.STO, 0, counterAddress);

        // Jump back to loop
        addInstruction(Instruction.JMP, 0, loopStart + 1);

        // Address of the last loop instruction
        int loopEnd = getCurrentInstructionRow();

        // Set the JMC address
        modifyInstructionAddress(jmcIndex, loopEnd + 1);

        // Clean the result of JMC, base and exponent
        addInstruction(Instruction.INT, 0, -2);

        // Save the result of the power expression to the allocated space
        addInstruction(Instruction.STO, 0, -1);

        // Return from function
        addInstruction(Instruction.RET, 0, 0);

        // Exit function scope
        SymbolTable.exitScope();

        // Address of the last function instruction
        int afterFunctionBodyIndex = getCurrentInstructionRow();

        // Modify the CAL and JMP addresses
        modifyInstructionAddress(calIndex, functionBodyIndex + 1);
        modifyInstructionAddress(jmpIndex, afterFunctionBodyIndex + 1);
    }

    /// Generates the PL/0 instructions for a unary minus expression (e.g., -a).
    ///
    /// @param unaryMinusExpression the unary minus expression to generate
    private void genUnaryMinusExpression(UnaryMinusExpression unaryMinusExpression) {
        log.debug("Generating unary minus expression");
        Expression expressionUnary = unaryMinusExpression.getExpression();
        Operator operator = unaryMinusExpression.getOperator();

        switch (operator) {
            case SUB -> {
                // Add 0 to the top of the stack
                addInstruction(Instruction.LIT, 0, 0);

                // Generate expression
                generateExpression(expressionUnary);

                // Subtract the 0 and expression
                addInstruction(Instruction.OPR, 0, 3);
            }
            default -> {}
        }
    }

    /// Generates the PL/0 instructions for a unary plus expression (e.g., +a).
    ///
    /// @param unaryPlusExpression the unary plus expression to generate
    private void genUnaryPlusExpression(UnaryPlusExpression unaryPlusExpression) {
        log.debug("Generating unary plus expression");
        Expression expressionUnary = unaryPlusExpression.getExpression();
        Operator operator = unaryPlusExpression.getOperator();

        switch (operator) {
            case ADD -> generateExpression(expressionUnary);
            default -> {}
        }
    }

    /// Generates the PL/0 instructions for a logical NOT expression (e.g., !a).
    ///
    /// @param notExpression the NOT expression to generate
    private void genNotExpression(NotExpression notExpression) {
        log.debug("Generating not expression");
        Expression expressionNot = notExpression.getExpression();
        Operator operator = notExpression.getOperator();

        switch (operator) {
            case NOT -> {
                // Add 1 to the top of the stack
                addInstruction(Instruction.LIT, 0, 1);

                // Add boolean to the top of the stack
                generateExpression(expressionNot);

                addInstruction(Instruction.OPR, 0, 2); // Add the values <1;2>
                addInstruction(Instruction.LIT, 0, 1); // Add 1 to the top of the stack
                addInstruction(Instruction.OPR, 0, 8); // Are they equal ?
            }
            default -> {}
        }
    }

    /// Generates the PL/0 instructions for multiplicative expressions (e.g., a * b, a / b, a % b).
    ///
    /// @param multiplicativeExpression the multiplicative expression to generate
    private void genMultiplicativeExpression(MultiplicativeExpression multiplicativeExpression) {
        log.debug("Generating multiplicative expression");
        Expression left = multiplicativeExpression.getLeft();
        Expression right = multiplicativeExpression.getRight();

        // '*' | '/' | '%'
        Operator operator = multiplicativeExpression.getOperator();

        switch (operator) {
            case MUL -> {
                // Generate left expression
                generateExpression(left);
                // Generate right expression
                generateExpression(right);

                // left * right
                addInstruction(Instruction.OPR, 0, 4);
            }
            case DIV -> {
                // Generate left expression
                generateExpression(left);
                // Generate right expression
                generateExpression(right);

                // left / right
                addInstruction(Instruction.OPR, 0, 5);
            }
            case MOD -> {
                // Generate left expression
                generateExpression(left);
                // Generate right expression
                generateExpression(right);

                // left % right
                addInstruction(Instruction.OPR, 0, 6);
            }
            default -> {}
        }
    }

    /// Generates the PL/0 instructions for additive expressions (e.g., a + b, a - b).
    ///
    /// @param additiveExpression the additive expression to generate
    private void genAdditiveExpression(AdditiveExpression additiveExpression) {
        log.debug("Generating additive expression");
        Expression left = additiveExpression.getLeft();
        Expression right = additiveExpression.getRight();

        // '+' | '-'
        Operator operator = additiveExpression.getOperator();

        switch (operator) {
            case ADD -> {
                // Generate left expression
                generateExpression(left);
                // Generate right expression
                generateExpression(right);

                // left + right
                addInstruction(Instruction.OPR, 0, 2);
            }
            case SUB -> {
                // Generate left expression
                generateExpression(left);
                // Generate right expression
                generateExpression(right);

                // left - right
                addInstruction(Instruction.OPR, 0, 3);
            }
            default -> {}
        }
    }

    /// Generates the PL/0 instructions for comparison expressions (e.g., a == b, a != b, a > b, etc.).
    ///
    /// @param comparisonExpression the comparison expression to generate
    private void genComparisonExpression(ComparisonExpression comparisonExpression) {
        log.debug("Generating comparison expression");
        Expression left = comparisonExpression.getLeft();
        Expression right = comparisonExpression.getRight();

        // '==' | '!=' | '>' | '<' | '>=' | '<='
        Operator operator = comparisonExpression.getOperator();

        switch (operator) {
            case EQ -> {
                // Generate left expression
                generateExpression(left);
                // Generate right expression
                generateExpression(right);

                // left == right
                addInstruction(Instruction.OPR, 0, 8);
            }
            case NEQ -> {
                // Generate left expression
                generateExpression(left);
                // Generate right expression
                generateExpression(right);

                // left != right
                addInstruction(Instruction.OPR, 0, 9);
            }
            case GT -> {
                // Generate left expression
                generateExpression(left);
                // Generate right expression
                generateExpression(right);

                // left > right
                addInstruction(Instruction.OPR, 0, 12);
            }
            case LT -> {
                // Generate left expression
                generateExpression(left);
                // Generate right expression
                generateExpression(right);

                // left < right
                addInstruction(Instruction.OPR, 0, 10);
            }
            case GTE -> {
                // Generate left expression
                generateExpression(left);
                // Generate right expression
                generateExpression(right);

                // left >= right
                addInstruction(Instruction.OPR, 0, 11);
            }
            case LTE -> {
                // Generate left expression
                generateExpression(left);
                // Generate right expression
                generateExpression(right);

                // left <= right
                addInstruction(Instruction.OPR, 0, 13);
            }
            default -> {}
        }
    }

    /// Generates the PL/0 instructions for logical expressions (e.g., a && b, a || b).
    ///
    /// @param logicalExpression the logical expression to generate
    private void genLogicalExpression(LogicalExpression logicalExpression) {
        log.debug("Generating logical expression");
        Expression left = logicalExpression.getLeft();
        Expression right = logicalExpression.getRight();

        // '*' | '/' | '%'
        Operator operator = logicalExpression.getOperator();

        switch (operator) {
            case AND -> {
                // Generate left expression
                generateExpression(left);
                // Generate right expression
                generateExpression(right);

                // left && right
                addInstruction(Instruction.OPR, 0, 4); // Multiply
                addInstruction(Instruction.LIT, 0, 1); // Add 1 to the top of the stack
                addInstruction(Instruction.OPR, 0, 8); // Is the result 1 ?
            }
            case OR -> {
                // Generate left expression
                generateExpression(left);
                // Generate right expression
                generateExpression(right);

                // left || right
                addInstruction(Instruction.OPR, 0, 2);  // Add both values
                addInstruction(Instruction.LIT, 0, 1);  // Add 1 to the top of the stack
                addInstruction(Instruction.OPR, 0, 11); // Is the result >= 1 ?
            }
            default -> {}
        }
    }

    /// Generates the PL/0 instructions for parenthesized expressions (e.g., (a + b)).
    /// Parentheses simply alter the order of evaluation.
    ///
    /// @param parenthesizedExpression the parenthesized expression to generate
    private void genParenthesizedExpression(ParenthesizedExpression parenthesizedExpression) {
        log.debug("Generating parenthesized expression");
        Expression parenthesizedExpr = parenthesizedExpression.getExpression();

        generateExpression(parenthesizedExpr);
    }

    /// Generates the PL/0 instructions for an identifier expression (e.g., variable lookup).
    ///
    /// @param identifier the identifier expression to generate
    private void genIdentifierExpression(Identifier identifier) {
        String idenName = identifier.getName();
        Descriptor descriptor = SymbolTable.lookup(idenName);

        addInstruction(Instruction.LOD, SymbolTable.getLevel(idenName), descriptor.getAddres());
    }

    /// Generates the PL/0 instructions for literal expressions (e.g., integers, booleans).
    ///
    /// @param literal the literal expression to generate
    private void generateLiteral(Literal<?> literal) {
        switch (literal.getValue()) {
            case Integer integer -> addInstruction(Instruction.LIT, 0, integer);
            case Boolean bool -> addInstruction(Instruction.LIT, 0, bool ? 1 : 0);
            default -> throw new GenerateException("Unexpected value: " + literal.getValue());
        }
    }

    /// Generates the PL/0 instructions for a function call expression (e.g., function(a, b)).
    ///
    /// @param functionCall the function call expression to generate
    private void generateFunctionCallExpression(FunctionCallExpression functionCall) {
        functionGenerator.setFunctionCall(functionCall);
        functionGenerator.generate();
    }

}
