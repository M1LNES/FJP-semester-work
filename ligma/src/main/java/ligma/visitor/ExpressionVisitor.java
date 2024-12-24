package ligma.visitor;

import ligma.enums.DataType;
import ligma.enums.Operator;
import ligma.exception.SemanticException;
import ligma.generated.LigmaBaseVisitor;
import ligma.generated.LigmaParser;
import ligma.ir.expression.AdditiveExpression;
import ligma.ir.expression.ComparisonExpression;
import ligma.ir.expression.Expression;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExpressionVisitor extends LigmaBaseVisitor<Expression> {

    private static final FunctionVisitor functionVisitor = new FunctionVisitor();

    @Override
    public Expression visitPowerExpression(LigmaParser.PowerExpressionContext ctx) {
        log.debug("Power expression: {}", ctx.getText());
        Expression left = visit(ctx.expression(0));
        Expression right = visit(ctx.expression(1));

        if (left.getType() != DataType.INT || right.getType() != DataType.INT) {
            throw new SemanticException("Cannot apply the 'pow' operation to non-int type");
        }

        return new PowerExpression(Operator.POW, left, right, DataType.INT);
    }

    @Override
    public Expression visitUnaryMinusExpression(LigmaParser.UnaryMinusExpressionContext ctx) {
        log.debug("Unary minus expression: {}", ctx.getText());
        Expression expression = visit(ctx.expression());

        if (expression.getType() != DataType.INT) {
            throw new SemanticException("Cannot apply the 'unary minus' operation to non-int type");
        }

        return new UnaryMinusExpression(Operator.SUB, expression, DataType.INT);
    }

    @Override
    public Expression visitUnaryPlusExpression(LigmaParser.UnaryPlusExpressionContext ctx) {
        log.debug("Unary plus expression: {}", ctx.getText());
        Expression expression = visit(ctx.expression());

        if (expression.getType() != DataType.INT) {
            throw new SemanticException("Cannot apply the 'unary plus' operation to non-int type");
        }

        return new UnaryPlusExpression(Operator.ADD, expression, DataType.INT);
    }

    @Override
    public Expression visitNotExpression(LigmaParser.NotExpressionContext ctx) {
        log.debug("Not expression: {}", ctx.getText());
        Expression expression = visit(ctx.expression());

        DataType expressionType = expression.getType();

        if (expressionType != DataType.BOOLEAN) {
            throw new SemanticException("Cannot negate non-boolean type: " + expressionType);
        }

        return new NotExpression(Operator.NOT, expression, expressionType);
    }

    @Override
    public Expression visitMultiplicativeExpression(LigmaParser.MultiplicativeExpressionContext ctx) {
        log.debug("Multiplicative expression: {}", ctx.getText());
        Operator operator = Operator.fromSymbol(ctx.op.getText());
        Expression left = visit(ctx.expression(0));
        Expression right = visit(ctx.expression(1));

        if (left.getType() != DataType.INT || right.getType() != DataType.INT) {
            throw new SemanticException("Cannot apply the '" + operator.getSymbol() + "' operation to non-int type");
        }

        return new MultiplicativeExpression(operator, left, right, DataType.INT);
    }

    @Override
    public Expression visitAdditiveExpression(LigmaParser.AdditiveExpressionContext ctx) {
        log.debug("Additive expression: {}", ctx.getText());
        Operator operator = Operator.fromSymbol(ctx.op.getText());
        Expression left = visit(ctx.expression(0));
        Expression right = visit(ctx.expression(1));

        if (left.getType() != DataType.INT || right.getType() != DataType.INT) {
            throw new SemanticException("Cannot apply the '" + operator.getSymbol() + "' operation to non-int type");
        }

        return new AdditiveExpression(operator, left, right, DataType.INT);
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
                throw new SemanticException(
                    "Cannot evaluate comparison: " + operator.getSymbol() +
                    " for non-matching types [" + left.getType() + ", " + right.getType() + "]"
                );
            }
        }
        // >, <, >=, <=
        else {
            // Both expressions need to be int
            if (left.getType() != DataType.INT || right.getType() != DataType.INT) {
                throw new SemanticException("Cannot apply the operation to non-int type");
            }
        }

        // Data type of the comparison expression is always boolean
        return new ComparisonExpression(operator, left, right, DataType.BOOLEAN);
    }

    @Override
    public Expression visitLogicalExpression(LigmaParser.LogicalExpressionContext ctx) {
        log.debug("Logical expression: {}", ctx.getText());
        Operator operator = Operator.fromSymbol(ctx.op.getText());
        Expression left = visit(ctx.expression(0));
        Expression right = visit(ctx.expression(1));

        // Both expressions must be of type boolean
        if (left.getType() != DataType.BOOLEAN || right.getType() != DataType.BOOLEAN) {
            throw new SemanticException("Cannot evaluate logical expression with non-boolean types");
        }

        // Data type of the logical expression is always boolean
        return new LogicalExpression(operator, left, right, DataType.BOOLEAN);
    }

    @Override
    public Expression visitParenthesizedExpression(LigmaParser.ParenthesizedExpressionContext ctx) {
        log.debug("Parenthesized expression: {}", ctx.getText());
        Expression expression = visit(ctx.expression());

        return new ParenthesizedExpression(expression, expression.getType());
    }

    @Override
    public Expression visitIdentifierExpression(LigmaParser.IdentifierExpressionContext ctx) {
        log.debug("Identifier expression: {}", ctx.getText());
        String identifier = ctx.IDENTIFIER().getText();

        Descriptor descriptor = SymbolTable.lookup(identifier);

        if (descriptor == null) {
            throw new SemanticException("Identifier: " + identifier + " was not declared");
        }

        return new Identifier(identifier, descriptor.getType());
    }

    @Override
    public Expression visitLiteralExpression(LigmaParser.LiteralExpressionContext ctx) {
        log.debug("Literal expression: {}", ctx.getText());
        LigmaParser.LiteralContext literalCtx = ctx.literal();

        Literal<?> literal = null;

        // int
        if (literalCtx.INTEGER_LITERAL() != null) {
            Integer value = Integer.parseInt(literalCtx.INTEGER_LITERAL().getText());
            literal = new Literal<>(value, DataType.INT);
        }
        // boolean
        else if (literalCtx.BOOLEAN_LITERAL() != null) {
            Boolean value = Boolean.parseBoolean(literalCtx.BOOLEAN_LITERAL().getText());
            literal = new Literal<>(value, DataType.BOOLEAN);
        }

        return literal;
    }

    @Override
    public Expression visitFunctionCallExpression(LigmaParser.FunctionCallExpressionContext ctx) {
        return functionVisitor.visitFunctionCallExpression(ctx);
    }

}
