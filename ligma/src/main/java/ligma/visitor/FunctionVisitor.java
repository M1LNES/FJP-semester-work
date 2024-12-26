package ligma.visitor;

import ligma.enums.DataType;
import ligma.exception.SemanticException;
import ligma.generated.LigmaBaseVisitor;
import ligma.generated.LigmaParser;
import ligma.ir.expression.Expression;
import ligma.ir.expression.FunctionCallExpression;
import ligma.ir.function.Function;
import ligma.ir.function.FunctionParameter;
import ligma.ir.statement.FunctionCall;
import ligma.ir.statement.Statement;
import ligma.table.Descriptor;
import ligma.table.FunctionDescriptor;
import ligma.table.SymbolTable;
import ligma.table.VariableDescriptor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/// @author Milan Janoch & Jakub Pavlicek
/// @version 1.0
///
/// The FunctionVisitor class is responsible for visiting function definitions, function calls,
/// and function call expressions in the Ligma language. It handles function parameter parsing,
/// the creation of function descriptors, validation of return types, and adds relevant information
/// to the symbol table. This class also processes the body of functions and the arguments for function calls.
@Slf4j
public class FunctionVisitor extends LigmaBaseVisitor<Object> {

    /// A static visitor for processing statements within functions.
    private static final StatementVisitor statementVisitor = new StatementVisitor();
    /// A static visitor for processing expressions within functions.
    private static final ExpressionVisitor expressionVisitor = new ExpressionVisitor();

    /// Visits a function definition in the Ligma language, processing the function's return type,
    /// parameters, body, and return expression. It also ensures that the function's return type matches
    /// the type of the return expression and adds the function to the symbol table.
    ///
    /// @param ctx The context of the function definition.
    /// @return A Function object representing the parsed function.
    @Override
    public Function visitFunctionDefinition(LigmaParser.FunctionDefinitionContext ctx) {
        String type = ctx.dataType().getText();
        String identifier = ctx.IDENTIFIER().getText();
        log.debug("Function definition: {} [type: {}, id: {}]", ctx.getText(), type, identifier);

        DataType returnType = DataType.getDataType(type);

        SymbolTable.enterScope(true);

        List<FunctionParameter> parameters = new ArrayList<>();
        List<Statement> statements = new ArrayList<>();

        // Traverse function parameters
        processFunctionParameters(ctx, parameters);

        LigmaParser.FunctionBodyContext functionBody = ctx.functionBody();

        // Traverse statements in the function body
        for (LigmaParser.StatementContext statementCtx : functionBody.statement()) {
            statements.add(statementVisitor.visit(statementCtx));
        }

        // Return expression
        Expression returnExpr = expressionVisitor.visit(functionBody.expression());

        // Function's return type doesn't match the expression type
        if (returnExpr.getType() != returnType) {
            throw new SemanticException(
                "Function's return type is: " + type +
                ", but the provided type was: " + returnExpr.getType().name().toLowerCase()
            );
        }

        SymbolTable.exitScope();

        // Function descriptor belongs to the parent scope
        addFunctionToSymbolTable(identifier, returnType, parameters, statements, returnExpr);

        return new Function(identifier, returnType, parameters, statements, returnExpr);
    }

    /// Visits a function call statement in the Ligma language, processing the function's identifier
    /// and its arguments. Returns a FunctionCall object representing the parsed function call.
    ///
    /// @param ctx The context of the function call.
    /// @return A FunctionCall object representing the parsed function call.
    @Override
    public FunctionCall visitFunctionCall(LigmaParser.FunctionCallContext ctx) {
        log.debug("Function call statement: {}", ctx.getText());
        String identifier = ctx.IDENTIFIER().getText();
        List<Expression> arguments = new ArrayList<>();

        LigmaParser.ArgumentListContext argumentListCtx = ctx.argumentList();

        // Function call has some arguments
        if (argumentListCtx != null && argumentListCtx.expression() != null) {
            arguments = argumentListCtx.expression()
                                       .stream()
                                       .map(expressionVisitor::visit)
                                       .toList();
        }

        return new FunctionCall(identifier, arguments);
    }

    /// Visits a function call expression in the Ligma language, processing the function's identifier
    /// and its arguments within an expression context. Returns a FunctionCallExpression object.
    ///
    /// @param ctx The context of the function call expression.
    /// @return A FunctionCallExpression object representing the parsed function call expression.
    @Override
    public FunctionCallExpression visitFunctionCallExpression(LigmaParser.FunctionCallExpressionContext ctx) {
        log.debug("Function call expression: {}", ctx.getText());
        LigmaParser.FunctionCallContext functionCallCtx = ctx.functionCall();

        String identifier = functionCallCtx.IDENTIFIER().getText();
        List<Expression> arguments = new ArrayList<>();

        // Function call has some arguments
        if (functionCallCtx.argumentList() != null) {
            arguments = functionCallCtx.argumentList()
                                       .expression()
                                       .stream()
                                       .map(expressionVisitor::visit)
                                       .toList();
        }

        return new FunctionCallExpression(DataType.INT, identifier, arguments);
    }

    /// Processes the function parameters by visiting each parameter context in the function definition,
    /// creating FunctionParameter objects, and adding them to the provided parameters list. Also adds
    /// parameter descriptors to the symbol table.
    ///
    /// @param ctx The context of the function definition.
    /// @param parameters The list to which FunctionParameter objects will be added.
    private void processFunctionParameters(LigmaParser.FunctionDefinitionContext ctx, List<FunctionParameter> parameters) {
        if (ctx.parameterList() == null) {
            return;
        }

        for (LigmaParser.ParameterContext parameterCtx : ctx.parameterList().parameter()) {
            String paramName = parameterCtx.IDENTIFIER().getText();
            String paramType = parameterCtx.dataType().getText();
            DataType paramDataType = DataType.getDataType(paramType);

            FunctionParameter parameter = new FunctionParameter(paramDataType, paramName);
            parameters.add(parameter);

            log.debug("Parameter: {} {}", paramType, paramName);

            // Create parameter descriptor and add it to the symbol table
            addParameterToSymbolTable(paramName, paramDataType);
        }
    }

    /// Adds a parameter descriptor to the symbol table, which contains information about the parameter's
    /// name, type, and scope level.
    ///
    /// @param paramName   The name of the parameter.
    /// @param paramDataType The data type of the parameter.
    private void addParameterToSymbolTable(String paramName, DataType paramDataType) {
        Descriptor paramDescriptor = VariableDescriptor.builder()
                                                       .name(paramName)
                                                       .type(paramDataType)
                                                       .isConstant(false)
                                                       .scopeLevel(SymbolTable.getLevel(paramName))
                                                       .build();

        SymbolTable.add(paramName, paramDescriptor);
    }

    /// Adds a function descriptor to the symbol table, which contains information about the function's
    /// name, return type, parameters, statements, and return expression.
    ///
    /// @param identifier  The name of the function.
    /// @param returnType  The return type of the function.
    /// @param parameters  The list of parameters of the function.
    /// @param statements  The list of statements in the function body.
    /// @param returnExpr  The return expression of the function.
    private void addFunctionToSymbolTable(String identifier, DataType returnType, List<FunctionParameter> parameters, List<Statement> statements, Expression returnExpr) {
        Descriptor descriptor = FunctionDescriptor.builder()
                                                  .name(identifier)
                                                  .type(returnType)
                                                  .scopeLevel(SymbolTable.getLevel(identifier))
                                                  .parameters(parameters)
                                                  .statements(statements)
                                                  .returnExpression(returnExpr)
                                                  .build();

        SymbolTable.add(identifier, descriptor);
    }

}
