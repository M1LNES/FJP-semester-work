package ligma.visitor;

import ligma.enums.DataType;
import ligma.exception.SemanticException;
import ligma.generated.LigmaBaseVisitor;
import ligma.generated.LigmaParser;
import ligma.ir.expression.Expression;
import ligma.ir.expression.FunctionCallExpression;
import ligma.ir.function.Callable;
import ligma.ir.statement.Assignment;
import ligma.ir.statement.ConstantDefinition;
import ligma.ir.statement.DoWhileLoop;
import ligma.ir.statement.ForLoop;
import ligma.ir.statement.IfStatement;
import ligma.ir.statement.RepeatUntilLoop;
import ligma.ir.statement.Statement;
import ligma.ir.statement.VariableDefinition;
import ligma.ir.statement.WhileLoop;
import ligma.table.Descriptor;
import ligma.table.FunctionDescriptor;
import ligma.table.SymbolTable;
import ligma.table.VariableDescriptor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// The StatementVisitor class is responsible for traversing and processing various types of statements
/// in the Ligma language during the semantic analysis phase. This class extends the LigmaBaseVisitor
/// and overrides methods to visit and process statements. It performs semantic checks such as ensuring correct type
/// assignments, checking variable redeclarations, and managing scope through the Symbol Table.
@Slf4j
public class StatementVisitor extends LigmaBaseVisitor<Statement> {

    /// A static visitor for processing expressions.
    private static final ExpressionVisitor expressionVisitor = new ExpressionVisitor();
    /// A static visitor for processing functions.
    private static final FunctionVisitor functionVisitor = new FunctionVisitor();

    /// Visits a variable definition statement and processes it.
    /// Validates the data type and checks for variable redeclaration in the current scope.
    ///
    /// @param ctx The context representing a variable definition.
    /// @return A new VariableDefinition statement.
    /// @throws SemanticException If there is a redeclaration or type mismatch.
    @Override
    public Statement visitVariableDefinition(LigmaParser.VariableDefinitionContext ctx) {
        String type = ctx.dataType().getText();
        String identifier = ctx.IDENTIFIER().getText();
        log.debug("Variable definition: {} [type: {}, id: {}]", ctx.getText(), type, identifier);

        // Redeclaration of the same identifier in the current scope
        if (SymbolTable.isIdentifierInCurrentScope(identifier)) {
            throw new SemanticException("Variable '" + identifier + "' is already defined in the scope");
        }

        DataType dataType = DataType.getDataType(type);
        Expression expression = expressionVisitor.visit(ctx.expression());

        // Type mismatch
        if (!(expression instanceof FunctionCallExpression) && dataType != expression.getType()) {
            throw new SemanticException(
                "Cannot assign the value to the variable '" + identifier
                + "': type mismatch [" + dataType + ", " + expression.getType() + "]"
            );
        }

        // Add identifier with descriptor to the Symbol Table
        addVariableToSymbolTable(identifier, dataType, false);

        return new VariableDefinition(identifier, dataType, expression);
    }

    /// Visits a constant definition statement and processes it.
    /// Validates the data type and checks for constant redeclaration in the current scope.
    ///
    /// @param ctx The context representing a constant definition.
    /// @return A new ConstantDefinition statement.
    /// @throws SemanticException If there is a redeclaration or type mismatch.
    @Override
    public Statement visitConstantDefinition(LigmaParser.ConstantDefinitionContext ctx) {
        String type = ctx.variableDefinition().dataType().getText();
        String identifier = ctx.variableDefinition().IDENTIFIER().getText();
        log.debug("Constant definition: {} [type: {}, id: {}]", ctx.getText(), type, identifier);

        // Redeclaration of the same identifier in the current scope
        if (SymbolTable.isIdentifierInCurrentScope(identifier)) {
            throw new SemanticException("Variable '" + identifier + "' is already defined in the scope");
        }

        DataType dataType = DataType.getDataType(type);
        Expression expression = expressionVisitor.visit(ctx.variableDefinition().expression());

        // Type mismatch
        if (!(expression instanceof FunctionCallExpression) && dataType != expression.getType()) {
            throw new SemanticException(
                "Cannot assign the value to the constant variable '" + identifier
                    + "': type mismatch [" + dataType + ", " + expression.getType() + "]"
            );
        }

        // Add identifier with descriptor to the Symbol Table
        addVariableToSymbolTable(identifier, dataType, true);

        return new ConstantDefinition(identifier, dataType, expression);
    }

    /// Visits an assignment statement and processes it.
    /// Ensures that no reassignment to constants or functions occurs, and checks for type mismatches.
    ///
    /// @param ctx The context representing an assignment.
    /// @return An Assignment statement.
    /// @throws SemanticException If reassignment to a constant or function occurs or if a type mismatch is found.
    @Override
    public Statement visitAssignment(LigmaParser.AssignmentContext ctx) {
        log.debug("Assignment: {}", ctx.getText());
        String firstIdentifier = ctx.IDENTIFIER().getText();
        Expression expression = expressionVisitor.visit(ctx.expression());

        // Traverse all chained identifiers
        List<String> chainedIdentifiers = new ArrayList<>();
        for (LigmaParser.ChainedAssignmentContext chainedAssignmentCtx : ctx.chainedAssignment()) {
            chainedIdentifiers.add(chainedAssignmentCtx.IDENTIFIER().getText());
        }

        // All identifiers in the assigment
        List<String> allIdentifiers = new ArrayList<>();
        allIdentifiers.add(firstIdentifier);
        allIdentifiers.addAll(chainedIdentifiers);

        // Loop throush every identifier in the assignment
        for (String iden : allIdentifiers) {
            Descriptor descriptor = SymbolTable.lookup(iden);

            switch (descriptor) {
                // Reassigment to constant is not allowed
                case VariableDescriptor varDesc when varDesc.isConstant() ->
                    throw new SemanticException("Cannot assign new value to constant '" + varDesc.getName() + "'");
                // Cannot assign value to a function
                case FunctionDescriptor funcDesc ->
                    throw new SemanticException("Cannot assign new value to a function '" + funcDesc.getName() + "'");
                // Identifier was not found in the traversed scopes
                case null -> throw new SemanticException("Variable " + iden + " was not declared yet");
                default -> {
                    // Type mismatch
                    if (!(expression instanceof Callable) && descriptor.getType() != expression.getType()) {
                        throw new SemanticException("Variable '" + iden + "' is not of type " + expression.getType());
                    }
                }
            }
        }

        return new Assignment(allIdentifiers, expression);
    }

    /// Visits an if statement and processes it.
    /// Ensures the condition is of boolean type and processes the 'if' and 'else' bodies.
    ///
    /// @param ctx The context representing an if statement.
    /// @return An IfStatement with the condition, 'if' body, and 'else' body.
    /// @throws SemanticException If the condition is not of boolean type.
    @Override
    public Statement visitIfStatement(LigmaParser.IfStatementContext ctx) {
        log.debug("If statement: {}", ctx.getText());

        SymbolTable.enterScope(false);

        Expression expression = expressionVisitor.visit(ctx.expression());

        // Expression must be of type boolean
        if (expression.getType() != DataType.BOOLEAN) {
            throw new SemanticException("Condition must be a boolean type");
        }

        // Traverse statements in the 'if' body
        List<Statement> ifStatements = new ArrayList<>();
        for (LigmaParser.StatementContext statementCtx : ctx.ifElseBody(0).statement()) {
            ifStatements.add(visit(statementCtx));
        }

        SymbolTable.exitScope();

        // 'else' is not present
        if (ctx.ELSE() == null) {
            return new IfStatement(expression, ifStatements, new ArrayList<>());
        }

        SymbolTable.enterScope(false);

        // Traverse statements in the 'else' body
        List<Statement> elseStatements = new ArrayList<>();
        for (LigmaParser.StatementContext statementCtx : ctx.ifElseBody(1).statement()) {
            elseStatements.add(visit(statementCtx));
        }

        SymbolTable.exitScope();

        return new IfStatement(expression, ifStatements, elseStatements);
    }

    /// Visits a for loop statement and processes it.
    /// Ensures the initialization expression and range are of type integer.
    ///
    /// @param ctx The context representing a for loop.
    /// @return A ForLoop statement.
    /// @throws SemanticException If the initialization or range expressions are not of type integer.
    @Override
    public Statement visitForLoop(LigmaParser.ForLoopContext ctx) {
        log.debug("For loop: {}", ctx.getText());

        SymbolTable.enterScope(false);

        String identifier = ctx.IDENTIFIER().getText();

        // Add identifier with descriptor to the Symbol Table
        addVariableToSymbolTable(identifier, DataType.INT, false);

        Expression expression = expressionVisitor.visit(ctx.expression(0));
        Expression toExpression = expressionVisitor.visit(ctx.expression(1));

        // For loop initialization expression must be of type int
        if (expression.getType() != DataType.INT) {
            throw new SemanticException("For loop initialization expression must be of type int");
        }
        // For loop range must be of type int
        if (toExpression.getType() != DataType.INT) {
            throw new SemanticException("For loop range must be of type int");
        }

        // Traverse statements in the for loop body
        List<Statement> statements = new ArrayList<>();
        for (LigmaParser.StatementContext statementCtx : ctx.statement()) {
            statements.add(visit(statementCtx));
        }

        SymbolTable.exitScope();

        return new ForLoop(identifier, expression, toExpression, statements);
    }

    /// Visits a while loop statement and processes it.
    /// Ensures the condition is of boolean type and processes the body statements.
    ///
    /// @param ctx The context representing a while loop.
    /// @return A WhileLoop statement.
    /// @throws SemanticException If the condition is not of boolean type.
    @Override
    public Statement visitWhileLoop(LigmaParser.WhileLoopContext ctx) {
        log.debug("While loop: {}", ctx.getText());

        SymbolTable.enterScope(false);

        Expression expression = expressionVisitor.visit(ctx.expression());

        // Expression must be of type boolean
        if (expression.getType() != DataType.BOOLEAN) {
            throw new SemanticException("Condition must be a boolean type");
        }

        // Traverse statements in the body
        List<Statement> statements = new ArrayList<>();
        for (LigmaParser.StatementContext statementCtx : ctx.statement()) {
            statements.add(visit(statementCtx));
        }

        SymbolTable.exitScope();

        return new WhileLoop(expression, statements);
    }

    /// Visits a do-while loop statement and processes it.
    ///
    /// @param ctx The context representing a do-while loop.
    /// @return A DoWhileLoop statement.
    @Override
    public Statement visitDoWhileLoop(LigmaParser.DoWhileLoopContext ctx) {
        log.debug("Do-while loop: {}", ctx.getText());
        return processLoop(ctx.statement(), ctx.expression(), DoWhileLoop::new);
    }

    /// Visits a repeat-until loop statement and processes it.
    ///
    /// @param ctx The context representing a repeat-until loop.
    /// @return A RepeatUntilLoop statement.
    @Override
    public Statement visitRepeatUntilLoop(LigmaParser.RepeatUntilLoopContext ctx) {
        log.debug("Repeat-until loop: {}", ctx.getText());
        return processLoop(ctx.statement(), ctx.expression(), RepeatUntilLoop::new);
    }

    /// Processes a loop (either do-while or repeat-until) and validates its condition.
    ///
    /// @param statementCtxList The list of statements in the loop body.
    /// @param expressionCtx The context representing the loop condition.
    /// @param loopConstructor A function to construct the appropriate loop statement.
    /// @return A loop statement (DoWhileLoop or RepeatUntilLoop).
    /// @throws SemanticException If the loop condition is not of boolean type.
    private Statement processLoop(
        List<LigmaParser.StatementContext> statementCtxList,
        LigmaParser.ExpressionContext expressionCtx,
        BiFunction<List<Statement>, Expression, Statement> loopConstructor
    ) {
        SymbolTable.enterScope(false);

        // Traverse statements in the loop body
        List<Statement> statements = statementCtxList.stream()
                                                     .map(this::visit)
                                                     .toList();

        // Parse the loop condition
        Expression condition = expressionVisitor.visit(expressionCtx);

        // Validate that the condition is boolean
        if (condition.getType() != DataType.BOOLEAN) {
            throw new SemanticException("Condition must be a boolean type");
        }

        SymbolTable.exitScope();

        // Create and return the loop statement
        return loopConstructor.apply(statements, condition);
    }

    /// Visits a function call statement and processes it.
    ///
    /// @param ctx The context representing a function call.
    /// @return The result of the function call.
    @Override
    public Statement visitFunctionCall(LigmaParser.FunctionCallContext ctx) {
        return functionVisitor.visitFunctionCall(ctx);
    }

    /// Adds a variable to the symbol table.
    ///
    /// @param identifier The name of the variable.
    /// @param dataType The data type of the variable.
    /// @param isConstant Whether the variable is constant or not.
    private void addVariableToSymbolTable(String identifier, DataType dataType, boolean isConstant) {
        Descriptor descriptor = VariableDescriptor.builder()
                                                  .name(identifier)
                                                  .type(dataType)
                                                  .isConstant(isConstant)
                                                  .scopeLevel(SymbolTable.getLevel(identifier))
                                                  .build();

        SymbolTable.add(identifier, descriptor);
    }

}
