// Generated from /Users/kuba/Items/NAVAZUJICI/FJP/semestralka/ligma/Ligma.g4 by ANTLR 4.13.2
package ligma.generated;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LigmaParser}.
 */
public interface LigmaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LigmaParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(LigmaParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link LigmaParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(LigmaParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link LigmaParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(LigmaParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LigmaParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(LigmaParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link LigmaParser#variableDefinition}.
	 * @param ctx the parse tree
	 */
	void enterVariableDefinition(LigmaParser.VariableDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link LigmaParser#variableDefinition}.
	 * @param ctx the parse tree
	 */
	void exitVariableDefinition(LigmaParser.VariableDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link LigmaParser#constantDefinition}.
	 * @param ctx the parse tree
	 */
	void enterConstantDefinition(LigmaParser.ConstantDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link LigmaParser#constantDefinition}.
	 * @param ctx the parse tree
	 */
	void exitConstantDefinition(LigmaParser.ConstantDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link LigmaParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(LigmaParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link LigmaParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(LigmaParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link LigmaParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(LigmaParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LigmaParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(LigmaParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link LigmaParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(LigmaParser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LigmaParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(LigmaParser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link LigmaParser#procedureDefinition}.
	 * @param ctx the parse tree
	 */
	void enterProcedureDefinition(LigmaParser.ProcedureDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link LigmaParser#procedureDefinition}.
	 * @param ctx the parse tree
	 */
	void exitProcedureDefinition(LigmaParser.ProcedureDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link LigmaParser#procedureCall}.
	 * @param ctx the parse tree
	 */
	void enterProcedureCall(LigmaParser.ProcedureCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link LigmaParser#procedureCall}.
	 * @param ctx the parse tree
	 */
	void exitProcedureCall(LigmaParser.ProcedureCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link LigmaParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(LigmaParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link LigmaParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(LigmaParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link LigmaParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(LigmaParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link LigmaParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(LigmaParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link LigmaParser#dataType}.
	 * @param ctx the parse tree
	 */
	void enterDataType(LigmaParser.DataTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link LigmaParser#dataType}.
	 * @param ctx the parse tree
	 */
	void exitDataType(LigmaParser.DataTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link LigmaParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void enterArgumentList(LigmaParser.ArgumentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link LigmaParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void exitArgumentList(LigmaParser.ArgumentListContext ctx);
	/**
	 * Enter a parse tree produced by {@link LigmaParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(LigmaParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link LigmaParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(LigmaParser.ExpressionContext ctx);
}