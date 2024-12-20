package ligma.visitor;

import ligma.ast.expression.Expression;
import ligma.ast.expression.Literal;
import ligma.generated.LigmaBaseVisitor;
import ligma.generated.LigmaParser;
import org.antlr.v4.runtime.tree.TerminalNode;

public class ExpressionVisitor extends LigmaBaseVisitor<Expression> {

    @Override
    public Expression visitPowerExpression(LigmaParser.PowerExpressionContext ctx) {
        return super.visitPowerExpression(ctx);
    }

    @Override
    public Expression visitUnaryMinusExpression(LigmaParser.UnaryMinusExpressionContext ctx) {
        return super.visitUnaryMinusExpression(ctx);
    }

    @Override
    public Expression visitUnaryPlusExpression(LigmaParser.UnaryPlusExpressionContext ctx) {
        return super.visitUnaryPlusExpression(ctx);
    }

    @Override
    public Expression visitNotExpression(LigmaParser.NotExpressionContext ctx) {
        return super.visitNotExpression(ctx);
    }

    @Override
    public Expression visitMultiplicativeExpression(LigmaParser.MultiplicativeExpressionContext ctx) {
        return super.visitMultiplicativeExpression(ctx);
    }

    @Override
    public Expression visitAdditiveExpression(LigmaParser.AdditiveExpressionContext ctx) {
        return super.visitAdditiveExpression(ctx);
    }

    @Override
    public Expression visitComparisonExpression(LigmaParser.ComparisonExpressionContext ctx) {
        return super.visitComparisonExpression(ctx);
    }

    @Override
    public Expression visitLogicalExpression(LigmaParser.LogicalExpressionContext ctx) {
        return super.visitLogicalExpression(ctx);
    }

    @Override
    public Expression visitParenthesizedExpression(LigmaParser.ParenthesizedExpressionContext ctx) {
        return super.visitParenthesizedExpression(ctx);
    }

    @Override
    public Expression visitIdentifierExpression(LigmaParser.IdentifierExpressionContext ctx) {
        return super.visitIdentifierExpression(ctx);
    }

    @Override
    public Expression visitLiteralExpression(LigmaParser.LiteralExpressionContext ctx) {
        LigmaParser.LiteralContext literalCtx = ctx.literal();
        Literal<?> literal = null;
        int line = ctx.getStart().getLine();

        if (literalCtx.INTEGER_LITERAL() != null) {
            Integer value = Integer.parseInt(literalCtx.INTEGER_LITERAL().getText());
            literal = new Literal<>(value, line);
        }
        else if (literalCtx.FLOAT_LITERAL() != null) {
            Float value = Float.parseFloat(literalCtx.FLOAT_LITERAL().getText());
            literal = new Literal<>(value, line);
        }
        else if (literalCtx.BOOLEAN_LITERAL() != null) {
            Boolean value = Boolean.parseBoolean(literalCtx.BOOLEAN_LITERAL().getText());
            literal = new Literal<>(value, line);
        }
        else {
            throw new RuntimeException("Invalid literal: " + ctx.getText());
        }

        return literal;
    }

}
