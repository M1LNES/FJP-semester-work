package ligma.visitor;

import ligma.ast.expression.Expression;
import ligma.ast.function.Function;
import ligma.ast.function.FunctionParameter;
import ligma.ast.statement.Statement;
import ligma.enums.DataType;
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
public class FunctionDefinitionVisitor extends LigmaBaseVisitor<Function> {

    StatementVisitor statementVisitor = new StatementVisitor();
    ExpressionVisitor expressionVisitor = new ExpressionVisitor();

    @Override
    public Function visitFunctionDefinition(LigmaParser.FunctionDefinitionContext ctx) {
        String type = ctx.dataType().getText();
        String identifier = ctx.IDENTIFIER().getText();
        log.debug("Function definition: {} [type: {}, id: {}]", ctx.getText(), type, identifier);

        DataType returnType = DataType.getDataType(type);

        SymbolTable.enterScope(identifier);

        List<FunctionParameter> parameters = new ArrayList<>();
        List<Statement> statements = new ArrayList<>();

        // Traverse function parameters
        for (LigmaParser.ParameterContext parameterCtx : ctx.parameterList().parameter()) {
            String paramName = parameterCtx.IDENTIFIER().getText();
            String paramType = parameterCtx.dataType().getText();
            DataType paramDataType = DataType.getDataType(paramType);

            FunctionParameter parameter = new FunctionParameter(paramDataType, paramName);
            parameters.add(parameter);

            log.debug("Parameter: {} {}", paramType, paramName);

            // Create parameter descriptor and add it to the symbol table
            Descriptor paramDescriptor = VariableDescriptor.builder()
                                                           .name(paramName)
                                                           .type(paramDataType)
                                                           .isConstant(false)
                                                           .scopeLevel(SymbolTable.getCurrentScope().getLevel())
                                                           .build();

            SymbolTable.add(paramName, paramDescriptor);
        }

        LigmaParser.FunctionBodyContext functionBody = ctx.functionBody();

        // Traverse statements in the function body
        for (LigmaParser.StatementContext statementCtx : functionBody.statement()) {
            statements.add(statementVisitor.visit(statementCtx));
        }

        // Return expression
        Expression returnExpr = expressionVisitor.visit(functionBody.expression());

        // Function's return type doesn't match the expression type
        if (returnExpr.getType() != returnType) {
            throw new RuntimeException(
                "Function's return type is: " + type +
                ", but the provided type was: " + returnExpr.getType().name().toLowerCase()
            );
        }

        SymbolTable.exitScope();

        // Function descriptor belongs to the parent scope
        Descriptor descriptor = FunctionDescriptor.builder()
                                                  .name(identifier)
                                                  .type(returnType)
                                                  .scopeLevel(SymbolTable.getCurrentScope().getLevel())
                                                  .parameters(parameters)
                                                  .statements(statements)
                                                  .returnExpression(returnExpr)
                                                  .build();

        SymbolTable.add(identifier, descriptor);

        return new Function(identifier, returnType, parameters, statements);
    }

}
