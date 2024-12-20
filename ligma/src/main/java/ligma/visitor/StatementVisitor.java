package ligma.visitor;

import ligma.ast.expression.Expression;
import ligma.ast.statement.Assignment;
import ligma.ast.statement.FunctionCall;
import ligma.ast.statement.IfStatement;
import ligma.ast.statement.Statement;
import ligma.ast.statement.VariableDefinition;
import ligma.ast.statement.WhileStatement;
import ligma.enums.DataType;
import ligma.enums.ScopeType;
import ligma.generated.LigmaBaseVisitor;
import ligma.generated.LigmaParser;
import ligma.table.Descriptor;
import ligma.table.SymbolTable;
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

        // TODO: switch(type) and check if the "expression" dataType is compatible

        Descriptor descriptor = Descriptor.builder()
                                          .name(identifier)
                                          .type(DataType.getDataType(type))
                                          .isConstant(false)
                                          .scopeLevel(SymbolTable.getCurrentScope().getLevel())
                                          .build();

        SymbolTable.add(identifier, descriptor);

        Expression expression = expressionVisitor.visit(ctx.expression());

        return new VariableDefinition(identifier, expression);
    }

    @Override
    public Statement visitConstantDefinition(LigmaParser.ConstantDefinitionContext ctx) {
        String type = ctx.variableDefinition().dataType().getText();
        String identifier = ctx.variableDefinition().IDENTIFIER().getText();
        log.debug("Constant definition: {} [type: {}, id: {}]", ctx.getText(), type, identifier);

        // TODO: switch(type) and check if the "expression" dataType is compatible

        Descriptor descriptor = Descriptor.builder()
                                          .name(identifier)
                                          .type(DataType.getDataType(type))
                                          .isConstant(true)
                                          .scopeLevel(SymbolTable.getCurrentScope().getLevel())
                                          .build();

        SymbolTable.add(identifier, descriptor);

        Expression expression = expressionVisitor.visit(ctx.variableDefinition().expression());

        return new VariableDefinition(identifier, expression);
    }

    @Override
    public Statement visitAssignment(LigmaParser.AssignmentContext ctx) {
        log.debug("Assignment: {}", ctx.getText());
        String identifier = ctx.IDENTIFIER().getText();

        if (SymbolTable.lookup(identifier) == null) {
            throw new RuntimeException("Variable " + identifier + " was not declared yet");
        }

        Expression expression = expressionVisitor.visit(ctx.expression());

        return new Assignment(identifier, expression);
    }

    @Override
    public Statement visitIfStatement(LigmaParser.IfStatementContext ctx) {
        log.debug("If statement: {}", ctx.getText());

        SymbolTable.enterScope(ScopeType.IF.name());

        Expression expression = expressionVisitor.visit(ctx.expression());
        List<Statement> statements = new ArrayList<>();

        for (LigmaParser.StatementContext statementCtx : ctx.statement()) {
            statements.add(visit(statementCtx));
        }

        SymbolTable.exitScope();

        return new IfStatement(expression, statements);
    }

    @Override
    public Statement visitWhileStatement(LigmaParser.WhileStatementContext ctx) {
        log.debug("While statement: {}", ctx.getText());

        SymbolTable.enterScope(ScopeType.WHILE.name());

        // TODO: expression must be of type boolean (if false -> skip arguments)

        Expression expression = expressionVisitor.visit(ctx.expression());
        List<Statement> statements = new ArrayList<>();

        for (LigmaParser.StatementContext statementCtx : ctx.statement()) {
            statements.add(visit(statementCtx));
        }

        SymbolTable.exitScope();

        return new WhileStatement(expression, statements);
    }

    @Override
    public Statement visitFunctionCall(LigmaParser.FunctionCallContext ctx) {
        log.debug("Function call: {}", ctx.getText());

        String identifier = ctx.IDENTIFIER().getText();

        Descriptor descriptor = SymbolTable.lookup(identifier);

        // Function doesn't exist
        if (descriptor == null) {
            throw new RuntimeException("Function " + identifier + " was not declared yet");
        }
        // Identifier is not a function
        if (!descriptor.isFunction()) {
            throw new RuntimeException(identifier + " is not a function");
        }

        List<Expression> arguments = new ArrayList<>();

        for (LigmaParser.ExpressionContext expressionCtx : ctx.argumentList().expression()) {
            arguments.add(expressionVisitor.visit(expressionCtx));
        }

        // TODO: check that number of arguments match the number of parameters in the function definition
        // TODO: check argument type mismatch
        // TODO: check return type mismatch
        // TODO: check incorrect order of arguments

        return new FunctionCall(identifier, arguments);
    }

}
