package ligma.visitor;

import ligma.ast.expression.Expression;
import ligma.ast.statement.Statement;
import ligma.ast.statement.VariableDefinition;
import ligma.enums.DataType;
import ligma.generated.LigmaBaseVisitor;
import ligma.generated.LigmaParser;
import ligma.table.Descriptor;
import ligma.table.SymbolTable;

public class StatementVisitor extends LigmaBaseVisitor<Statement> {

    private final ExpressionVisitor expressionVisitor = new ExpressionVisitor();

    @Override
    public Statement visitVariableDefinition(LigmaParser.VariableDefinitionContext ctx) {
        String type = ctx.dataType().getText();
        String identifier = ctx.IDENTIFIER().getText();

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
        return super.visitAssignment(ctx);
    }

    @Override
    public Statement visitIfStatement(LigmaParser.IfStatementContext ctx) {
        return super.visitIfStatement(ctx);
    }

    @Override
    public Statement visitWhileStatement(LigmaParser.WhileStatementContext ctx) {
        return super.visitWhileStatement(ctx);
    }

    @Override
    public Statement visitFunctionDefinition(LigmaParser.FunctionDefinitionContext ctx) {
        return super.visitFunctionDefinition(ctx);
    }

    @Override
    public Statement visitFunctionCall(LigmaParser.FunctionCallContext ctx) {
        return super.visitFunctionCall(ctx);
    }

}
