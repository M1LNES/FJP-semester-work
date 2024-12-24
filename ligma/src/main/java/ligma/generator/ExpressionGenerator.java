package ligma.generator;

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
import ligma.enums.Instruction;
import ligma.enums.Operator;
import ligma.exception.GenerateException;
import ligma.table.Descriptor;
import lombok.Setter;

@Setter
public class ExpressionGenerator extends Generator {

    private static final FunctionGenerator functionGenerator = new FunctionGenerator();

    private Expression expression;

    @Override
    public void generate() {
        switch (expression) {
            case PowerExpression powerExpression -> generatePowerExpression(powerExpression);
            case UnaryMinusExpression unaryMinusExpression -> genUnaryMinusExpression(unaryMinusExpression);
            case UnaryPlusExpression unaryPlusExpression -> {} // Nothing needed
            case NotExpression notExpression -> genNotExpression(notExpression);
            case MultiplicativeExpression multiplicativeExpression -> genMultiplicativeExpression(multiplicativeExpression);
            case AdditiveExpression additiveExpression -> genAdditiveExpression(additiveExpression);
            case ComparisonExpression comparisonExpression -> genComparisonExpression(comparisonExpression);
            case LogicalExpression logicalExpression -> genLogicalExpression(logicalExpression);
            case ParenthesizedExpression parenthesizedExpression -> {} // Nothing needed
            case Identifier identifier -> genIdentifierExpression(identifier);
            case Literal<?> literal -> generateLiteral(literal);
            case FunctionCallExpression functionCallExpression -> generateFunctionCallExpression(functionCallExpression);
            default -> {}
        }
    }

    private void generatePowerExpression(PowerExpression powerExpression) {
        Expression left = powerExpression.getLeft();
        Expression right = powerExpression.getRight();

        addInstruction(Instruction.INT, 0, 4);

        // Initialize result to 1 (push 1 to stack)
        addInstruction(Instruction.LIT, 0, 1);

        int resultAddress = symbolTable.getNextAddress();
        addInstruction(Instruction.STO, 0, resultAddress);

        // Base
        // Generate left expression
        expression = left;
        generate();

        int baseAddress = symbolTable.getNextAddress();
        addInstruction(Instruction.STO, 0, baseAddress);

        // Exponent
        // Generate right expression
        expression = right;
        generate();

        int exponentAddress = symbolTable.getNextAddress();
        addInstruction(Instruction.STO, 0, exponentAddress);

        // Exponent as counter
        // Generate right expression
        expression = right;
        generate();

        // Save counter to the stack
        int counterAddress = symbolTable.getNextAddress();
        addInstruction(Instruction.STO, 0, counterAddress);

        int loopStart = getCurrentInstructionRow();

        addInstruction(Instruction.LOD, 0, counterAddress);

        // Test if counter != 0
        addInstruction(Instruction.LIT, 0, 0);
        addInstruction(Instruction.OPR, 0, 9);

        // Jump to the end of the power expression
        addInstruction(Instruction.JMC, 0, -1);

        int jmcIndex = getCurrentInstructionRow();

        addInstruction(Instruction.LOD, 0, resultAddress);
        addInstruction(Instruction.LOD, 0, baseAddress);

        // Multiply top two values (result * base)
        addInstruction(Instruction.OPR, 0, 4);

        addInstruction(Instruction.STO, 0, resultAddress);

        // Subtract 1 from counter
        addInstruction(Instruction.LOD, 0, counterAddress);
        addInstruction(Instruction.LIT, 0, 1);
        addInstruction(Instruction.OPR, 0, 3);

        addInstruction(Instruction.STO, 0, counterAddress);

        // Jump back to loop
        addInstruction(Instruction.JMP, 0, loopStart + 1);

        int loopEnd = getCurrentInstructionRow();

        modifyInstructionAddress(jmcIndex, loopEnd + 1);

        // Clean the result of JMC, base and exponent
        addInstruction(Instruction.INT, 0, -3);

        symbolTable.decrementCurrentScopeNextAddress(3);
    }

    private void genUnaryMinusExpression(UnaryMinusExpression unaryMinusExpression) {
        Expression expressionUnary = unaryMinusExpression.getExpression();
        Operator operator = unaryMinusExpression.getOperator();

        switch (operator) {
            case SUB -> {
                addInstruction(Instruction.LIT, 0, 0);

                expression = expressionUnary;
                generate();

                addInstruction(Instruction.OPR, 0, 3);
            }
            default -> {}
        }
    }

    private void genNotExpression(NotExpression notExpression) {
        Expression expressionNot = notExpression.getExpression();
        Operator operator = notExpression.getOperator();

        switch (operator) {
            case NOT -> {
                // Add 1 to the top of the stack
                addInstruction(Instruction.LIT, 0, 1);

                // Add boolean to the top of the stack
                expression = expressionNot;
                generate();

                // Add the values <1;2>
                addInstruction(Instruction.OPR, 0, 2);

                // Add 1 to the top of the stack
                addInstruction(Instruction.LIT, 0, 1);
                // Are they equal ?
                addInstruction(Instruction.OPR, 0, 8);
            }
            default -> {}
        }
    }

    private void genMultiplicativeExpression(MultiplicativeExpression multiplicativeExpression) {
        Expression left = multiplicativeExpression.getLeft();
        Expression right = multiplicativeExpression.getRight();

        // '*' | '/' | '%'
        Operator operator = multiplicativeExpression.getOperator();

        switch (operator) {
            case MUL -> {
                // Generate left expression
                expression = left;
                generate();

                // Generate right expression
                expression = right;
                generate();

                // Multiply the values on top of the stack
                // left * right
                addInstruction(Instruction.OPR, 0, 4);
            }
            case DIV -> {
                // Generate left expression
                expression = left;
                generate();

                // Generate right expression
                expression = right;
                generate();

                // Divide the values on top of the stack
                // left / right
                addInstruction(Instruction.OPR, 0, 5);
            }
            case MOD -> {
                // Generate left expression
                expression = left;
                generate();

                // Generate right expression
                expression = right;
                generate();

                // Module of the values on top of the stack
                // left % right
                addInstruction(Instruction.OPR, 0, 6);
            }
            default -> {}
        }
    }

    private void genAdditiveExpression(AdditiveExpression additiveExpression) {
        Expression left = additiveExpression.getLeft();
        Expression right = additiveExpression.getRight();

        // '+' | '-'
        Operator operator = additiveExpression.getOperator();

        switch (operator) {
            case ADD -> {
                // Generate left expression
                expression = left;
                generate();

                // Generate right expression
                expression = right;
                generate();

                // Add the values on top of the stack
                // left + right
                addInstruction(Instruction.OPR, 0, 2);
            }
            case SUB -> {
                // Generate left expression
                expression = left;
                generate();

                // Generate right expression
                expression = right;
                generate();

                // Subtract the values on top of the stack
                // left - right
                addInstruction(Instruction.OPR, 0, 3);
            }
            default -> {}
        }
    }

    private void genComparisonExpression(ComparisonExpression comparisonExpression) {
        Expression left = comparisonExpression.getLeft();
        Expression right = comparisonExpression.getRight();

        // '==' | '!=' | '>' | '<' | '>=' | '<='
        Operator operator = comparisonExpression.getOperator();

        switch (operator) {
            case EQ -> {
                // Generate left expression
                expression = left;
                generate();

                // Generate right expression
                expression = right;
                generate();

                addInstruction(Instruction.OPR, 0, 8);
            }
            case NEQ -> {
                // Generate left expression
                expression = left;
                generate();

                // Generate right expression
                expression = right;
                generate();

                addInstruction(Instruction.OPR, 0, 9);
            }
            case GT -> {
                // Generate left expression
                expression = left;
                generate();

                // Generate right expression
                expression = right;
                generate();

                addInstruction(Instruction.OPR, 0, 12);
            }
            case LT -> {
                // Generate left expression
                expression = left;
                generate();

                // Generate right expression
                expression = right;
                generate();

                addInstruction(Instruction.OPR, 0, 10);
            }
            case GTE -> {
                // Generate left expression
                expression = left;
                generate();

                // Generate right expression
                expression = right;
                generate();

                addInstruction(Instruction.OPR, 0, 11);
            }
            case LTE -> {
                // Generate left expression
                expression = left;
                generate();

                // Generate right expression
                expression = right;
                generate();

                addInstruction(Instruction.OPR, 0, 13);
            }
            default -> {}
        }
    }

    private void genLogicalExpression(LogicalExpression logicalExpression) {
        Expression left = logicalExpression.getLeft();
        Expression right = logicalExpression.getRight();

        // '*' | '/' | '%'
        Operator operator = logicalExpression.getOperator();

        switch (operator) {
            case AND -> {
                // Generate left expression
                expression = left;
                generate();

                // Generate right expression
                expression = right;
                generate();

                // left && right
                addInstruction(Instruction.OPR, 0, 4); // Multiply
                addInstruction(Instruction.LIT, 0, 1); // Add 1 to the top of the stack
                addInstruction(Instruction.OPR, 0, 8); // Is the result 1 ?
            }
            case OR -> {
                // Generate left expression
                expression = left;
                generate();

                // Generate right expression
                expression = right;
                generate();

                // left || right
                addInstruction(Instruction.OPR, 0, 2); // Add both values
                addInstruction(Instruction.LIT, 0, 1); // Add 0 to the top of the stack
                addInstruction(Instruction.OPR, 0, 11); // Is the result >= 1 ?
            }
            default -> {}
        }
    }

    private void genIdentifierExpression(Identifier identifier) {
        String idenName = identifier.getName();
        Descriptor descriptor = symbolTable.lookup(idenName);

        addInstruction(Instruction.LOD, symbolTable.getLevel(idenName), descriptor.getAddres());
    }

    private void generateLiteral(Literal<?> literal) {
        switch (literal.getValue()) {
            case Integer integer -> addInstruction(Instruction.LIT, 0, integer);
            case Boolean bool -> addInstruction(Instruction.LIT, 0, bool ? 1 : 0);
            default -> throw new GenerateException("Unexpected value: " + literal.getValue());
        }
    }

    private void generateFunctionCallExpression(FunctionCallExpression functionCall) {
        functionGenerator.setFunctionCall(functionCall);
        functionGenerator.generate();
    }

}
