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

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// Visitor class for processing various types of expressions.
/// This class is responsible for handling different types of expressions in the abstract syntax tree (AST),
/// such as unary expressions, arithmetic expressions, logical expressions, comparisons, function calls, and literals.
/// It ensures type checking and semantic validation during the expression evaluation.
@Slf4j
public class ExpressionVisitor extends LigmaBaseVisitor<Expression> {

    /// A static visitor for processing functions.
    private static final FunctionVisitor functionVisitor = new FunctionVisitor();

    /// Visits a power expression (exponentiation) in the parse tree.
    ///
    /// @param ctx The parse tree context for the power expression.
    /// @return A PowerExpression object representing the power operation.
    /// @throws SemanticException If either operand is not of type int.
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

    /// Visits a unary minus expression in the parse tree.
    ///
    /// @param ctx The parse tree context for the unary minus expression.
    /// @return A UnaryMinusExpression object representing the unary minus operation.
    /// @throws SemanticException If the operand is not of type int.
    @Override
    public Expression visitUnaryMinusExpression(LigmaParser.UnaryMinusExpressionContext ctx) {
        log.debug("Unary minus expression: {}", ctx.getText());
        Expression expression = visit(ctx.expression());

        if (expression.getType() != DataType.INT) {
            throw new SemanticException("Cannot apply the 'unary minus' operation to non-int type");
        }

        return new UnaryMinusExpression(Operator.SUB, expression, DataType.INT);
    }

    /// Visits a unary plus expression in the parse tree.
    ///
    /// @param ctx The parse tree context for the unary plus expression.
    /// @return A UnaryPlusExpression object representing the unary plus operation.
    /// @throws SemanticException If the operand is not of type int.
    @Override
    public Expression visitUnaryPlusExpression(LigmaParser.UnaryPlusExpressionContext ctx) {
        log.debug("Unary plus expression: {}", ctx.getText());
        Expression expression = visit(ctx.expression());

        if (expression.getType() != DataType.INT) {
            throw new SemanticException("Cannot apply the 'unary plus' operation to non-int type");
        }

        return new UnaryPlusExpression(Operator.ADD, expression, DataType.INT);
    }

    /// Visits a logical NOT expression in the parse tree.
    ///
    /// @param ctx The parse tree context for the NOT expression.
    /// @return A NotExpression object representing the logical NOT operation.
    /// @throws SemanticException If the operand is not of type boolean.
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

    /// Visits a multiplicative expression (multiplication or division) in the parse tree.
    ///
    /// @param ctx The parse tree context for the multiplicative expression.
    /// @return A MultiplicativeExpression object representing the multiplication or division operation.
    /// @throws SemanticException If either operand is not of type int.
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

    /// Visits an additive expression (addition or subtraction) in the parse tree.
    ///
    /// @param ctx The parse tree context for the additive expression.
    /// @return An AdditiveExpression object representing the addition or subtraction operation.
    /// @throws SemanticException If either operand is not of type int.
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

    /// Visits a comparison expression (e.g., ==, !=, <, >, <=, >=) in the parse tree.
    ///
    /// @param ctx The parse tree context for the comparison expression.
    /// @return A ComparisonExpression object representing the comparison operation.
    /// @throws SemanticException If the operand types do not match the expected types.
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

    /// Visits a logical expression (AND, OR) in the parse tree.
    ///
    /// @param ctx The parse tree context for the logical expression.
    /// @return A LogicalExpression object representing the logical AND or OR operation.
    /// @throws SemanticException If either operand is not of type boolean.
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

    /// Visits a parenthesized expression in the parse tree.
    ///
    /// @param ctx The parse tree context for the parenthesized expression.
    /// @return A ParenthesizedExpression object representing the expression inside parentheses.
    @Override
    public Expression visitParenthesizedExpression(LigmaParser.ParenthesizedExpressionContext ctx) {
        log.debug("Parenthesized expression: {}", ctx.getText());
        Expression expression = visit(ctx.expression());

        return new ParenthesizedExpression(expression, expression.getType());
    }

    /// Visits an identifier expression (variable reference) in the parse tree.
    ///
    /// @param ctx The parse tree context for the identifier expression.
    /// @return An Identifier object representing the variable reference.
    /// @throws SemanticException If the identifier is not declared in the symbol table.
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

    /// Visits a literal expression (integer or boolean literal) in the parse tree.
    ///
    /// @param ctx The parse tree context for the literal expression.
    /// @return A Literal object representing the literal value.
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

    /// Visits a function call expression in the parse tree.
    ///
    /// @param ctx The parse tree context for the function call expression.
    /// @return A FunctionCallExpression object representing the function call.
    @Override
    public Expression visitFunctionCallExpression(LigmaParser.FunctionCallExpressionContext ctx) {
        return functionVisitor.visitFunctionCallExpression(ctx);
    }

}
