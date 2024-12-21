package ligma.visitor;

import ligma.ast.expression.Expression;
import ligma.ast.function.FunctionParameter;
import ligma.ast.statement.Assignment;
import ligma.ast.statement.ConstantDefinition;
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
            throw new RuntimeException("Variable '" + identifier + "' is already defined in the scope");
        }

        DataType dataType = DataType.getDataType(type);
        Expression expression = expressionVisitor.visit(ctx.expression());

        // Type mismatch
        if (dataType != expression.getType()) {
            throw new RuntimeException("Variable '" + identifier + "' is not of type " + dataType);
        }

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
            throw new RuntimeException("Variable '" + identifier + "' is already defined in the scope");
        }

        DataType dataType = DataType.getDataType(type);
        Expression expression = expressionVisitor.visit(ctx.variableDefinition().expression());

        // Type mismatch
        if (dataType != expression.getType()) {
            throw new RuntimeException("Variable '" + identifier + "' is not of type " + dataType);
        }

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
        String identifier = ctx.IDENTIFIER().getText();
        Descriptor descriptor = SymbolTable.lookup(identifier);

        // Identifier was not found in the traversed scopes
        if (descriptor == null) {
            throw new RuntimeException("Variable " + identifier + " was not declared yet");
        }

        // Reassigment to constant is not allowed
        if (descriptor instanceof VariableDescriptor varDesc && varDesc.isConstant()) {
            throw new RuntimeException("Cannot assign new value to constant");
        }

        // Cannot assign value to a function
        if (descriptor instanceof FunctionDescriptor) {
            throw new RuntimeException("Cannot assign new value to a function");
        }

        Expression expression = expressionVisitor.visit(ctx.expression());

        // Type mismatch
        if (descriptor.getType() != expression.getType()) {
            throw new RuntimeException("Variable '" + identifier + "' is not of type " + expression.getType());
        }

        return new Assignment(identifier, expression);
    }

    @Override
    public Statement visitIfStatement(LigmaParser.IfStatementContext ctx) {
        log.debug("If statement: {}", ctx.getText());

        SymbolTable.enterScope(ScopeType.IF.name());

        Expression expression = expressionVisitor.visit(ctx.expression());

        // Expression must be of type boolean
        if (!DataType.isBooleanType(expression.getType())) {
            throw new RuntimeException("Condition must be a boolean type");
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
    public Statement visitWhileStatement(LigmaParser.WhileStatementContext ctx) {
        log.debug("While statement: {}", ctx.getText());

        SymbolTable.enterScope(ScopeType.WHILE.name());

        Expression expression = expressionVisitor.visit(ctx.expression());

        // Expression must be of type boolean
        if (!DataType.isBooleanType(expression.getType())) {
            throw new RuntimeException("Condition must be a boolean type");
        }

        List<Statement> statements = new ArrayList<>();

        // Traverse statements in the body
        for (LigmaParser.StatementContext statementCtx : ctx.statement()) {
            statements.add(visit(statementCtx));
        }

        SymbolTable.exitScope();

        return new WhileStatement(expression, statements);
    }

    @Override
    public Statement visitFunctionCall(LigmaParser.FunctionCallContext ctx) {
        log.debug("Function call statement: {}", ctx.getText());

        String identifier = ctx.IDENTIFIER().getText();

        Descriptor descriptor = SymbolTable.lookup(identifier);

        // Function doesn't exist
        if (descriptor == null) {
            throw new RuntimeException("Function " + identifier + " was not declared yet");
        }
        // Identifier is not a function
        if (!(descriptor instanceof FunctionDescriptor funcDescriptor)) {
            throw new RuntimeException(identifier + " is not a function");
        }

        List<Expression> arguments = new ArrayList<>();

        for (LigmaParser.ExpressionContext expressionCtx : ctx.argumentList().expression()) {
            arguments.add(expressionVisitor.visit(expressionCtx));
        }

        // Argument count doesn't match the parameter count
        if (funcDescriptor.getParameters().size() != arguments.size()) {
            throw new RuntimeException(
                "Function '" + identifier + "' needs " + funcDescriptor.getParameters().size() + " arguments" +
                " but " + arguments.size() + " was provided"
            );
        }

        // Check that every argument type matches the parameter type
        for (Expression argument : arguments) {
            for (FunctionParameter parameter : funcDescriptor.getParameters()) {
                if (argument.getType() != parameter.getType()) {
                    throw new RuntimeException(
                        "Argument type is " + argument.getType() +
                        " but " + parameter.getType() + " was expected"
                    );
                }
            }
        }

        return new FunctionCall(identifier, arguments);
    }

}
