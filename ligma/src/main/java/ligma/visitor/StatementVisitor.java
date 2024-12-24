package ligma.visitor;

import ligma.ast.expression.Expression;
import ligma.ast.statement.Assignment;
import ligma.ast.statement.ConstantDefinition;
import ligma.ast.statement.DoWhileLoop;
import ligma.ast.statement.ForLoop;
import ligma.ast.statement.IfStatement;
import ligma.ast.statement.RepeatUntilLoop;
import ligma.ast.statement.Statement;
import ligma.ast.statement.VariableDefinition;
import ligma.ast.statement.WhileLoop;
import ligma.enums.DataType;
import ligma.enums.ScopeType;
import ligma.exception.SemanticException;
import ligma.generated.LigmaBaseVisitor;
import ligma.generated.LigmaParser;
import ligma.table.Descriptor;
import ligma.table.FunctionDescriptor;
import ligma.table.SymbolTable;
import ligma.table.VariableDescriptor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@Slf4j
public class StatementVisitor extends LigmaBaseVisitor<Statement> {

    private static final ExpressionVisitor expressionVisitor = new ExpressionVisitor();
    private static final FunctionVisitor functionVisitor = new FunctionVisitor();

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
        if (dataType != expression.getType()) {
            throw new SemanticException("Variable '" + identifier + "' is not of type " + dataType);
        }

        // Add identifier with descriptor to the Symbol Table
        addVariableToSymbolTable(identifier, dataType, false);

        return new VariableDefinition(identifier, dataType, expression);
    }

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
        if (dataType != expression.getType()) {
            throw new SemanticException("Variable '" + identifier + "' is not of type " + dataType);
        }

        // Add identifier with descriptor to the Symbol Table
        addVariableToSymbolTable(identifier, dataType, true);

        return new ConstantDefinition(identifier, dataType, expression);
    }

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
                    if (descriptor.getType() != expression.getType()) {
                        throw new SemanticException("Variable '" + iden + "' is not of type " + expression.getType());
                    }
                }
            }
        }

        return new Assignment(allIdentifiers, expression);
    }

    @Override
    public Statement visitIfStatement(LigmaParser.IfStatementContext ctx) {
        log.debug("If statement: {}", ctx.getText());

        SymbolTable.enterScope(ScopeType.IF.name());

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

        SymbolTable.enterScope(ScopeType.ELSE.name());

        // Traverse statements in the 'else' body
        List<Statement> elseStatements = new ArrayList<>();
        for (LigmaParser.StatementContext statementCtx : ctx.ifElseBody(1).statement()) {
            elseStatements.add(visit(statementCtx));
        }

        SymbolTable.exitScope();

        return new IfStatement(expression, ifStatements, elseStatements);
    }

    @Override
    public Statement visitForLoop(LigmaParser.ForLoopContext ctx) {
        log.debug("For loop: {}", ctx.getText());

        SymbolTable.enterScope(ScopeType.FOR.name());

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

    @Override
    public Statement visitWhileLoop(LigmaParser.WhileLoopContext ctx) {
        log.debug("While loop: {}", ctx.getText());

        SymbolTable.enterScope(ScopeType.WHILE.name());

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

    @Override
    public Statement visitDoWhileLoop(LigmaParser.DoWhileLoopContext ctx) {
        log.debug("Do-while loop: {}", ctx.getText());
        return processLoop(ctx.statement(), ctx.expression(), ScopeType.DO_WHILE, DoWhileLoop::new);
    }

    @Override
    public Statement visitRepeatUntilLoop(LigmaParser.RepeatUntilLoopContext ctx) {
        log.debug("Repeat-until loop: {}", ctx.getText());
        return processLoop(ctx.statement(), ctx.expression(), ScopeType.REPEAT_UNTIL, RepeatUntilLoop::new);
    }

    private Statement processLoop(
        List<LigmaParser.StatementContext> statementCtxList,
        LigmaParser.ExpressionContext expressionCtx,
        ScopeType scopeType,
        BiFunction<List<Statement>, Expression, Statement> loopConstructor
    ) {
        SymbolTable.enterScope(scopeType.name());

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

    @Override
    public Statement visitFunctionCall(LigmaParser.FunctionCallContext ctx) {
        return functionVisitor.visitFunctionCall(ctx);
    }

    private void addVariableToSymbolTable(String identifier, DataType dataType, boolean isConstant) {
        Descriptor descriptor = VariableDescriptor.builder()
                                                  .name(identifier)
                                                  .type(dataType)
                                                  .isConstant(isConstant)
                                                  .scopeLevel(SymbolTable.getCurrentScope().getLevel())
                                                  .build();

        SymbolTable.add(identifier, descriptor);
    }

}
