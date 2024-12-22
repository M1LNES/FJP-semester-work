package ligma.visitor;

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
import ligma.ast.function.FunctionParameter;
import ligma.enums.DataType;
import ligma.enums.Operator;
import ligma.exception.SemanticException;
import ligma.generated.LigmaBaseVisitor;
import ligma.generated.LigmaParser;
import ligma.table.Descriptor;
import ligma.table.FunctionDescriptor;
import ligma.table.SymbolTable;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExpressionVisitor extends LigmaBaseVisitor<Expression> {

    @Override
    public Expression visitPowerExpression(LigmaParser.PowerExpressionContext ctx) {
        log.debug("Power expression: {}", ctx.getText());
        Expression left = visit(ctx.expression(0));
        Expression right = visit(ctx.expression(1));

        int line = ctx.getStart().getLine();

        if (left.getType() != DataType.INT || right.getType() != DataType.INT) {
            throw new SemanticException("Cannot apply the 'pow' operation to non-int type");
        }

        return new PowerExpression(Operator.POW, left, right, DataType.INT, line);
    }

    @Override
    public Expression visitUnaryMinusExpression(LigmaParser.UnaryMinusExpressionContext ctx) {
        log.debug("Unary minus expression: {}", ctx.getText());
        Expression expression = visit(ctx.expression());

        int line = ctx.getStart().getLine();

        if (expression.getType() != DataType.INT) {
            throw new SemanticException("Cannot apply the 'unary minus' operation to non-int type");
        }

        return new UnaryMinusExpression(Operator.SUB, expression, DataType.INT, line);
    }

    @Override
    public Expression visitUnaryPlusExpression(LigmaParser.UnaryPlusExpressionContext ctx) {
        log.debug("Unary plus expression: {}", ctx.getText());
        Expression expression = visit(ctx.expression());

        int line = ctx.getStart().getLine();

        if (expression.getType() != DataType.INT) {
            throw new SemanticException("Cannot apply the 'unary plus' operation to non-int type");
        }

        return new UnaryPlusExpression(Operator.ADD, expression, DataType.INT, line);
    }

    @Override
    public Expression visitNotExpression(LigmaParser.NotExpressionContext ctx) {
        log.debug("Not expression: {}", ctx.getText());
        Expression expression = visit(ctx.expression());

        DataType expressionType = expression.getType();
        int line = ctx.getStart().getLine();

        if (expressionType != DataType.BOOLEAN) {
            throw new SemanticException("Cannot negate non-boolean type: " + expressionType);
        }

        return new NotExpression(Operator.NOT, expression, expressionType, line);
    }

    @Override
    public Expression visitMultiplicativeExpression(LigmaParser.MultiplicativeExpressionContext ctx) {
        log.debug("Multiplicative expression: {}", ctx.getText());
        Operator operator = Operator.fromSymbol(ctx.op.getText());
        Expression left = visit(ctx.expression(0));
        Expression right = visit(ctx.expression(1));

        int line = ctx.getStart().getLine();

        if (left.getType() != DataType.INT || right.getType() != DataType.INT) {
            throw new SemanticException("Cannot apply the '" + operator.getSymbol() + "' operation to non-int type");
        }

        return new MultiplicativeExpression(operator, left, right, DataType.INT, line);
    }

    @Override
    public Expression visitAdditiveExpression(LigmaParser.AdditiveExpressionContext ctx) {
        log.debug("Additive expression: {}", ctx.getText());
        Operator operator = Operator.fromSymbol(ctx.op.getText());
        Expression left = visit(ctx.expression(0));
        Expression right = visit(ctx.expression(1));

        int line = ctx.getStart().getLine();

        if (left.getType() != DataType.INT || right.getType() != DataType.INT) {
            throw new SemanticException("Cannot apply the '" + operator.getSymbol() + "' operation to non-int type");
        }

        return new AdditiveExpression(operator, left, right, DataType.INT, line);
    }

    @Override
    public Expression visitComparisonExpression(LigmaParser.ComparisonExpressionContext ctx) {
        log.debug("Comparison expression: {}", ctx.getText());
        Operator operator = Operator.fromSymbol(ctx.op.getText());
        Expression left = visit(ctx.expression(0));
        Expression right = visit(ctx.expression(1));

        int line = ctx.getStart().getLine();

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
        return new ComparisonExpression(operator, left, right, DataType.BOOLEAN, line);
    }

    @Override
    public Expression visitLogicalExpression(LigmaParser.LogicalExpressionContext ctx) {
        log.debug("Logical expression: {}", ctx.getText());
        Operator operator = Operator.fromSymbol(ctx.op.getText());
        Expression left = visit(ctx.expression(0));
        Expression right = visit(ctx.expression(1));

        int line = ctx.getStart().getLine();

        // Both expressions must be of type boolean
        if (left.getType() != DataType.BOOLEAN || right.getType() != DataType.BOOLEAN) {
            throw new SemanticException("Cannot evaluate logical expression with non-boolean types");
        }

        // Data type of the logical expression is always boolean
        return new LogicalExpression(operator, left, right, DataType.BOOLEAN, line);
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
            throw new SemanticException("Identifier: " + identifier + " was not declared");
        }

        return new Identifier(identifier, descriptor.getType(), line);
    }

    @Override
    public Expression visitLiteralExpression(LigmaParser.LiteralExpressionContext ctx) {
        log.debug("Literal expression: {}", ctx.getText());
        LigmaParser.LiteralContext literalCtx = ctx.literal();
        int line = ctx.getStart().getLine();

        Literal<?> literal = null;

        // int
        if (literalCtx.INTEGER_LITERAL() != null) {
            Integer value = Integer.parseInt(literalCtx.INTEGER_LITERAL().getText());
            literal = new Literal<>(value, DataType.INT, line);
        }
        // boolean
        else if (literalCtx.BOOLEAN_LITERAL() != null) {
            Boolean value = Boolean.parseBoolean(literalCtx.BOOLEAN_LITERAL().getText());
            literal = new Literal<>(value, DataType.BOOLEAN, line);
        }

        return literal;
    }

    @Override
    public Expression visitFunctionCallExpression(LigmaParser.FunctionCallExpressionContext ctx) {
        log.debug("Function call expression: {}", ctx.getText());

        String identifier = ctx.functionCall().IDENTIFIER().getText();
        int line = ctx.getStart().getLine();

        Descriptor descriptor = SymbolTable.lookup(identifier);

        // Function doesn't exist
        if (descriptor == null) {
            throw new SemanticException("Function " + identifier + " was not declared yet");
        }
        // Identifier is not a function
        if (!(descriptor instanceof FunctionDescriptor funcDescriptor)) {
            throw new SemanticException(identifier + " is not a function");
        }

        List<FunctionParameter> parameters = funcDescriptor.getParameters();
        List<Expression> arguments = new ArrayList<>();

        // Traverse arguments
        for (LigmaParser.ExpressionContext expressionCtx : ctx.functionCall().argumentList().expression()) {
            arguments.add(visit(expressionCtx));
        }

        // Argument count doesn't match the parameter count
        if (arguments.size() != parameters.size()) {
            throw new SemanticException(
                "Function '" + identifier + "' needs " + parameters.size() + " arguments" +
                    " but " + arguments.size() + " was provided"
            );
        }

        // Check that every argument type matches the parameter type
        for (int i = 0; i < arguments.size(); i++) {
            if (arguments.get(i).getType() != parameters.get(i).type()) {
                throw new SemanticException(
                    "Argument type is " + arguments.get(i).getType() +
                        " but " + parameters.get(i).type() + " was expected"
                );
            }
        }

        return new FunctionCallExpression(descriptor.getType(), line, identifier, arguments);
    }

}
