// Generated from /Users/kuba/Items/NAVAZUJICI/FJP/semestralka/ligma/Ligma.g4 by ANTLR 4.13.2
package ligma.generated;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LigmaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LigmaVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link LigmaParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(LigmaParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link LigmaParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(LigmaParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link LigmaParser#variableDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDefinition(LigmaParser.VariableDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link LigmaParser#constantDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantDefinition(LigmaParser.ConstantDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link LigmaParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(LigmaParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link LigmaParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(LigmaParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link LigmaParser#whileStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStatement(LigmaParser.WhileStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link LigmaParser#procedureDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedureDefinition(LigmaParser.ProcedureDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link LigmaParser#procedureCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedureCall(LigmaParser.ProcedureCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link LigmaParser#parameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterList(LigmaParser.ParameterListContext ctx);
	/**
	 * Visit a parse tree produced by {@link LigmaParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(LigmaParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link LigmaParser#dataType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDataType(LigmaParser.DataTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link LigmaParser#argumentList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgumentList(LigmaParser.ArgumentListContext ctx);
	/**
	 * Visit a parse tree produced by {@link LigmaParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(LigmaParser.ExpressionContext ctx);
}