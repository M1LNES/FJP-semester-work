package ligma.generator;

import ligma.ast.expression.AdditiveExpression;
import ligma.ast.expression.ComparisonExpression;
import ligma.ast.expression.Expression;
import ligma.ast.expression.FunctionCallExpression;
import ligma.ast.expression.Identifier;
import ligma.ast.expression.Literal;
import ligma.ast.expression.LogicalExpression;
import ligma.ast.expression.MultiplicativeExpression;
import ligma.ast.expression.NotExpression;
import ligma.ast.expression.ParenthesizedExpression;
import ligma.ast.expression.PowerExpression;
import ligma.ast.expression.UnaryMinusExpression;
import ligma.ast.expression.UnaryPlusExpression;
import ligma.enums.DataType;
import ligma.enums.Instruction;
import ligma.enums.Operator;
import ligma.table.Descriptor;
import ligma.table.SymbolTable;
import lombok.Setter;

@Setter
public class ExpressionGenerator extends Generator {

    private Expression expression;

    @Override
    public void generate() {
        switch (expression) {
            case UnaryMinusExpression unaryMinusExpression -> genUnaryMinusExpression(unaryMinusExpression);
            case UnaryPlusExpression unaryPlusExpression -> {} // Nothing needed
            case NotExpression notExpression -> genNotExpression(notExpression);
            case MultiplicativeExpression multiplicativeExpression -> genMultiplicativeExpression(multiplicativeExpression);
            case AdditiveExpression additiveExpression -> genAdditiveExpression(additiveExpression);
            case ComparisonExpression comparisonExpression -> genComparisonExpression(comparisonExpression);
            case LogicalExpression logicalExpression -> genLogicalExpression(logicalExpression);
            case ParenthesizedExpression parenthesizedExpression -> {} // TODO: add
            case Identifier identifier -> genIdentifierExpression(identifier);
            case Literal<?> literal -> generateLiteral(literal);
            case FunctionCallExpression functionCallExpression -> {} // TODO: add
            default -> {}
        }
    }

    private void genIdentifierExpression(Identifier identifier) {
        String idenName = identifier.getName();
        Descriptor descriptor = SymbolTable.lookup(idenName);

        addInstruction(Instruction.LOD, descriptor.getScopeLevel(), descriptor.getAddres());
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
        }
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

    private void generateLiteral(Literal<?> literal) {
        switch (literal.getValue()) {
            case Integer integer -> addInstruction(Instruction.LIT, 0, integer);
            case Boolean bool -> addInstruction(Instruction.LIT, 0, bool ? 1 : 0);
            default -> throw new IllegalStateException("Unexpected value: " + literal.getValue());
        }
    }

}
