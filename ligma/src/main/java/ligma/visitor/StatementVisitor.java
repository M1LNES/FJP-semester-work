package ligma.visitor;

import ligma.ast.expression.Expression;
import ligma.ast.function.FunctionParameter;
import ligma.ast.statement.Assignment;
import ligma.ast.statement.ConstantDefinition;
import ligma.ast.statement.DoWhileLoop;
import ligma.ast.statement.ForLoop;
import ligma.ast.statement.FunctionCall;
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

@Slf4j
public class StatementVisitor extends LigmaBaseVisitor<Statement> {

    private final ExpressionVisitor expressionVisitor = new ExpressionVisitor();

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
        Descriptor descriptor = VariableDescriptor.builder()
                                                  .name(identifier)
                                                  .type(dataType)
                                                  .isConstant(false)
                                                  .scopeLevel(SymbolTable.getCurrentScope().getLevel())
                                                  .build();

        SymbolTable.add(identifier, descriptor);

        return new VariableDefinition(identifier, expression);
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
        Descriptor descriptor = VariableDescriptor.builder()
                                                  .name(identifier)
                                                  .type(DataType.getDataType(type))
                                                  .isConstant(true)
                                                  .scopeLevel(SymbolTable.getCurrentScope().getLevel())
                                                  .build();

        SymbolTable.add(identifier, descriptor);

        return new ConstantDefinition(identifier, expression);
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
            return new IfStatement(expression, ifStatements, null);
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
        Descriptor descriptor = VariableDescriptor.builder()
                                                  .name(identifier)
                                                  .type(DataType.INT)
                                                  .isConstant(false)
                                                  .scopeLevel(SymbolTable.getCurrentScope().getLevel())
                                                  .build();

        SymbolTable.add(identifier, descriptor);

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

        List<Statement> statements = new ArrayList<>();

        // Traverse statements in the body
        for (LigmaParser.StatementContext statementCtx : ctx.statement()) {
            statements.add(visit(statementCtx));
        }

        SymbolTable.exitScope();

        return new WhileLoop(expression, statements);
    }

    @Override
    public Statement visitDoWhileLoop(LigmaParser.DoWhileLoopContext ctx) {
        log.debug("Do-while loop: {}", ctx.getText());

        SymbolTable.enterScope(ScopeType.DO_WHILE.name());

        // Traverse statements in the body
        List<Statement> statements = new ArrayList<>();
        for (LigmaParser.StatementContext statementCtx : ctx.statement()) {
            statements.add(visit(statementCtx));
        }

        Expression expression = expressionVisitor.visit(ctx.expression());

        // Expression must be of type boolean
        if (expression.getType() != DataType.BOOLEAN) {
            throw new SemanticException("Condition must be a boolean type");
        }

        SymbolTable.exitScope();

        return new DoWhileLoop(statements, expression);
    }

    @Override
    public Statement visitRepeatUntilLoop(LigmaParser.RepeatUntilLoopContext ctx) {
        log.debug("Repeat-until loop: {}", ctx.getText());

        SymbolTable.enterScope(ScopeType.REPEAT_UNTIL.name());

        // Traverse statements in the body
        List<Statement> statements = new ArrayList<>();
        for (LigmaParser.StatementContext statementCtx : ctx.statement()) {
            statements.add(visit(statementCtx));
        }

        Expression expression = expressionVisitor.visit(ctx.expression());

        // Expression must be of type boolean
        if (expression.getType() != DataType.BOOLEAN) {
            throw new SemanticException("Condition must be a boolean type");
        }

        SymbolTable.exitScope();

        return new RepeatUntilLoop(statements, expression);
    }

    @Override
    public Statement visitFunctionCall(LigmaParser.FunctionCallContext ctx) {
        log.debug("Function call statement: {}", ctx.getText());

        String identifier = ctx.IDENTIFIER().getText();
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
        for (LigmaParser.ExpressionContext expressionCtx : ctx.argumentList().expression()) {
            arguments.add(expressionVisitor.visit(expressionCtx));
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

        return new FunctionCall(identifier, arguments);
    }

}
