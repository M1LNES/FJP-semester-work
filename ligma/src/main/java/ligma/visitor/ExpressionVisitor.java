package ligma.visitor;

import ligma.ast.expression.AdditiveExpression;
import ligma.ast.expression.ComparisonExpression;
import ligma.ast.expression.Expression;
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
import ligma.enums.Operator;
import ligma.generated.LigmaBaseVisitor;
import ligma.generated.LigmaParser;
import ligma.table.Descriptor;
import ligma.table.SymbolTable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExpressionVisitor extends LigmaBaseVisitor<Expression> {

    @Override
    public Expression visitPowerExpression(LigmaParser.PowerExpressionContext ctx) {
        log.debug("Power expression: {}", ctx.getText());
        Operator operator = Operator.POW;
        Expression left = visit(ctx.expression(0));
        Expression right = visit(ctx.expression(1));

        DataType finalExprType = DataType.getResultingNumericDataType(
            left.getType(), right.getType()
        );
        int line = ctx.getStart().getLine();

        return new PowerExpression(operator, left, right, finalExprType, line);
    }

    @Override
    public Expression visitUnaryMinusExpression(LigmaParser.UnaryMinusExpressionContext ctx) {
        log.debug("Unary minus expression: {}", ctx.getText());
        Operator operator = Operator.SUB;
        Expression expression = visit(ctx.expression());

        DataType expressionType = expression.getType();
        int line = ctx.getStart().getLine();

        if (!DataType.isNumericType(expressionType)) {
            throw new RuntimeException("Unsupported expression type: " + expressionType);
        }

        // Unary minus doesnt change the type -> keep expression type
        return new UnaryMinusExpression(operator, expression, expressionType, line);
    }

    @Override
    public Expression visitUnaryPlusExpression(LigmaParser.UnaryPlusExpressionContext ctx) {
        log.debug("Unary plus expression: {}", ctx.getText());
        Operator operator = Operator.ADD;
        Expression expression = visit(ctx.expression());

        DataType expressionType = expression.getType();
        int line = ctx.getStart().getLine();

        if (!DataType.isNumericType(expressionType)) {
            throw new RuntimeException("Unsupported expression type: " + expressionType);
        }

        // Unary plus doesnt change the type -> keep expression type
        return new UnaryPlusExpression(operator, expression, expressionType, line);
    }

    @Override
    public Expression visitNotExpression(LigmaParser.NotExpressionContext ctx) {
        log.debug("Not expression: {}", ctx.getText());
        Operator operator = Operator.NOT;
        Expression expression = visit(ctx.expression());

        DataType expressionType = expression.getType();
        int line = ctx.getStart().getLine();

        if (!DataType.isBooleanType(expressionType)) {
            throw new RuntimeException("Cannot negate non-boolean type: " + expressionType);
        }

        return new NotExpression(operator, expression, expressionType, line);
    }

    @Override
    public Expression visitMultiplicativeExpression(LigmaParser.MultiplicativeExpressionContext ctx) {
        log.debug("Multiplicative expression: {}", ctx.getText());
        Operator operator = Operator.fromSymbol(ctx.op.getText());
        Expression left = visit(ctx.expression(0));
        Expression right = visit(ctx.expression(1));

        DataType finalExprType = DataType.getResultingNumericDataType(
            left.getType(), right.getType()
        );
        int line = ctx.getStart().getLine();

        return new MultiplicativeExpression(operator, left, right, finalExprType, line);
    }

    @Override
    public Expression visitAdditiveExpression(LigmaParser.AdditiveExpressionContext ctx) {
        log.debug("Additive expression: {}", ctx.getText());
        Operator operator = Operator.fromSymbol(ctx.op.getText());
        Expression left = visit(ctx.expression(0));
        Expression right = visit(ctx.expression(1));

        DataType finalExprType = DataType.getResultingNumericDataType(
            left.getType(), right.getType()
        );
        int line = ctx.getStart().getLine();

        return new AdditiveExpression(operator, left, right, finalExprType, line);
    }

    @Override
    public Expression visitComparisonExpression(LigmaParser.ComparisonExpressionContext ctx) {
        log.debug("Comparison expression: {}", ctx.getText());
        Operator operator = Operator.fromSymbol(ctx.op.getText());
        Expression left = visit(ctx.expression(0));
        Expression right = visit(ctx.expression(1));

        // ==, !*
        if (operator == Operator.EQ || operator == Operator.NEQ) {
            // Both expressions need to be of the same data type
            if (!left.getType().equals(right.getType())) {
                throw new RuntimeException(
                    "Cannot evaluate comparison: " + operator.getSymbol() +
                    " for non-matching types [" + left.getType() + ", " + right.getType() + "]"
                );
            }
        }
        // >, <, >=, <=
        else {
            // Both expressions need to be of numeric data type
            if (!DataType.isNumericType(left.getType()) || !DataType.isNumericType(right.getType())) {
                throw new RuntimeException("Cannot apply the operation to non-numeric type");
            }
        }

        // Data type of the comparison expression is always boolean
        DataType finalExprType = DataType.BOOLEAN;
        int line = ctx.getStart().getLine();

        return new ComparisonExpression(operator, left, right, finalExprType, line);
    }

    @Override
    public Expression visitLogicalExpression(LigmaParser.LogicalExpressionContext ctx) {
        log.debug("Logical expression: {}", ctx.getText());
        Operator operator = Operator.fromSymbol(ctx.op.getText());
        Expression left = visit(ctx.expression(0));
        Expression right = visit(ctx.expression(1));

        // Both expressions must be of type boolean
        if (!DataType.isBooleanType(left.getType()) || !DataType.isBooleanType(right.getType())) {
            throw new RuntimeException("Cannot evaluate logical expression with non-boolean types");
        }

        // Data type of the logical expression is always boolean
        DataType finalExprType = DataType.BOOLEAN;
        int line = ctx.getStart().getLine();

        return new LogicalExpression(operator, left, right, finalExprType, line);
    }

    @Override
    public Expression visitParenthesizedExpression(LigmaParser.ParenthesizedExpressionContext ctx) {
        log.debug("Parenthesized expression: {}", ctx.getText());
        Expression expression = visit(ctx.expression());
        int line = ctx.getStart().getLine();

        return new ParenthesizedExpression(expression, expression.getType(), line);
    }

    @Override
    public Expression visitIdentifierExpression(LigmaParser.IdentifierExpressionContext ctx) {
        log.debug("Identifier expression: {}", ctx.getText());
        String identifier = ctx.IDENTIFIER().getText();
        int line = ctx.getStart().getLine();

        Descriptor descriptor = SymbolTable.lookup(identifier);

        if (descriptor == null) {
            throw new RuntimeException("Identifier: " + identifier + " was not declared");
        }

        return new Identifier(identifier, descriptor.type(), line);
    }

    @Override
    public Expression visitLiteralExpression(LigmaParser.LiteralExpressionContext ctx) {
        log.debug("Literal expression: {}", ctx.getText());
        LigmaParser.LiteralContext literalCtx = ctx.literal();
        Literal<?> literal = null;
        int line = ctx.getStart().getLine();

        // int
        if (literalCtx.INTEGER_LITERAL() != null) {
            Integer value = Integer.parseInt(literalCtx.INTEGER_LITERAL().getText());
            literal = new Literal<>(value, DataType.INT, line);
        }
        // float
        else if (literalCtx.FLOAT_LITERAL() != null) {
            Float value = Float.parseFloat(literalCtx.FLOAT_LITERAL().getText());
            literal = new Literal<>(value, DataType.FLOAT, line);
        }
        // boolean
        else if (literalCtx.BOOLEAN_LITERAL() != null) {
            Boolean value = Boolean.parseBoolean(literalCtx.BOOLEAN_LITERAL().getText());
            literal = new Literal<>(value, DataType.BOOLEAN, line);
        }
        else {
            throw new RuntimeException("Invalid literal: " + ctx.getText());
        }

        return literal;
    }

}
