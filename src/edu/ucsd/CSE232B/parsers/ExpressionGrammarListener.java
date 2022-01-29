// Generated from ExpressionGrammar.g4 by ANTLR 4.7.2

package edu.ucsd.CSE232B.parsers;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExpressionGrammarParser}.
 */
public interface ExpressionGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExpressionGrammarParser#doc}.
	 * @param ctx the parse tree
	 */
	void enterDoc(ExpressionGrammarParser.DocContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionGrammarParser#doc}.
	 * @param ctx the parse tree
	 */
	void exitDoc(ExpressionGrammarParser.DocContext ctx);
	/**
	 * Enter a parse tree produced by the {@code directAbsolutePath}
	 * labeled alternative in {@link ExpressionGrammarParser#ap}.
	 * @param ctx the parse tree
	 */
	void enterDirectAbsolutePath(ExpressionGrammarParser.DirectAbsolutePathContext ctx);
	/**
	 * Exit a parse tree produced by the {@code directAbsolutePath}
	 * labeled alternative in {@link ExpressionGrammarParser#ap}.
	 * @param ctx the parse tree
	 */
	void exitDirectAbsolutePath(ExpressionGrammarParser.DirectAbsolutePathContext ctx);
	/**
	 * Enter a parse tree produced by the {@code indirectAbsolutePath}
	 * labeled alternative in {@link ExpressionGrammarParser#ap}.
	 * @param ctx the parse tree
	 */
	void enterIndirectAbsolutePath(ExpressionGrammarParser.IndirectAbsolutePathContext ctx);
	/**
	 * Exit a parse tree produced by the {@code indirectAbsolutePath}
	 * labeled alternative in {@link ExpressionGrammarParser#ap}.
	 * @param ctx the parse tree
	 */
	void exitIndirectAbsolutePath(ExpressionGrammarParser.IndirectAbsolutePathContext ctx);
	/**
	 * Enter a parse tree produced by the {@code currentRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void enterCurrentRelativePath(ExpressionGrammarParser.CurrentRelativePathContext ctx);
	/**
	 * Exit a parse tree produced by the {@code currentRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void exitCurrentRelativePath(ExpressionGrammarParser.CurrentRelativePathContext ctx);
	/**
	 * Enter a parse tree produced by the {@code indirectRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void enterIndirectRelativePath(ExpressionGrammarParser.IndirectRelativePathContext ctx);
	/**
	 * Exit a parse tree produced by the {@code indirectRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void exitIndirectRelativePath(ExpressionGrammarParser.IndirectRelativePathContext ctx);
	/**
	 * Enter a parse tree produced by the {@code commaRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void enterCommaRelativePath(ExpressionGrammarParser.CommaRelativePathContext ctx);
	/**
	 * Exit a parse tree produced by the {@code commaRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void exitCommaRelativePath(ExpressionGrammarParser.CommaRelativePathContext ctx);
	/**
	 * Enter a parse tree produced by the {@code filterRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void enterFilterRelativePath(ExpressionGrammarParser.FilterRelativePathContext ctx);
	/**
	 * Exit a parse tree produced by the {@code filterRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void exitFilterRelativePath(ExpressionGrammarParser.FilterRelativePathContext ctx);
	/**
	 * Enter a parse tree produced by the {@code tagRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void enterTagRelativePath(ExpressionGrammarParser.TagRelativePathContext ctx);
	/**
	 * Exit a parse tree produced by the {@code tagRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void exitTagRelativePath(ExpressionGrammarParser.TagRelativePathContext ctx);
	/**
	 * Enter a parse tree produced by the {@code attribRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void enterAttribRelativePath(ExpressionGrammarParser.AttribRelativePathContext ctx);
	/**
	 * Exit a parse tree produced by the {@code attribRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void exitAttribRelativePath(ExpressionGrammarParser.AttribRelativePathContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parentheseRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void enterParentheseRelativePath(ExpressionGrammarParser.ParentheseRelativePathContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parentheseRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void exitParentheseRelativePath(ExpressionGrammarParser.ParentheseRelativePathContext ctx);
	/**
	 * Enter a parse tree produced by the {@code childrenRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void enterChildrenRelativePath(ExpressionGrammarParser.ChildrenRelativePathContext ctx);
	/**
	 * Exit a parse tree produced by the {@code childrenRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void exitChildrenRelativePath(ExpressionGrammarParser.ChildrenRelativePathContext ctx);
	/**
	 * Enter a parse tree produced by the {@code upperRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void enterUpperRelativePath(ExpressionGrammarParser.UpperRelativePathContext ctx);
	/**
	 * Exit a parse tree produced by the {@code upperRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void exitUpperRelativePath(ExpressionGrammarParser.UpperRelativePathContext ctx);
	/**
	 * Enter a parse tree produced by the {@code directRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void enterDirectRelativePath(ExpressionGrammarParser.DirectRelativePathContext ctx);
	/**
	 * Exit a parse tree produced by the {@code directRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void exitDirectRelativePath(ExpressionGrammarParser.DirectRelativePathContext ctx);
	/**
	 * Enter a parse tree produced by the {@code textRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void enterTextRelativePath(ExpressionGrammarParser.TextRelativePathContext ctx);
	/**
	 * Exit a parse tree produced by the {@code textRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 */
	void exitTextRelativePath(ExpressionGrammarParser.TextRelativePathContext ctx);
	/**
	 * Enter a parse tree produced by the {@code equalFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 */
	void enterEqualFilter(ExpressionGrammarParser.EqualFilterContext ctx);
	/**
	 * Exit a parse tree produced by the {@code equalFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 */
	void exitEqualFilter(ExpressionGrammarParser.EqualFilterContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stringconstantFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 */
	void enterStringconstantFilter(ExpressionGrammarParser.StringconstantFilterContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stringconstantFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 */
	void exitStringconstantFilter(ExpressionGrammarParser.StringconstantFilterContext ctx);
	/**
	 * Enter a parse tree produced by the {@code notFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 */
	void enterNotFilter(ExpressionGrammarParser.NotFilterContext ctx);
	/**
	 * Exit a parse tree produced by the {@code notFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 */
	void exitNotFilter(ExpressionGrammarParser.NotFilterContext ctx);
	/**
	 * Enter a parse tree produced by the {@code andFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 */
	void enterAndFilter(ExpressionGrammarParser.AndFilterContext ctx);
	/**
	 * Exit a parse tree produced by the {@code andFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 */
	void exitAndFilter(ExpressionGrammarParser.AndFilterContext ctx);
	/**
	 * Enter a parse tree produced by the {@code isFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 */
	void enterIsFilter(ExpressionGrammarParser.IsFilterContext ctx);
	/**
	 * Exit a parse tree produced by the {@code isFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 */
	void exitIsFilter(ExpressionGrammarParser.IsFilterContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rpFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 */
	void enterRpFilter(ExpressionGrammarParser.RpFilterContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rpFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 */
	void exitRpFilter(ExpressionGrammarParser.RpFilterContext ctx);
	/**
	 * Enter a parse tree produced by the {@code orFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 */
	void enterOrFilter(ExpressionGrammarParser.OrFilterContext ctx);
	/**
	 * Exit a parse tree produced by the {@code orFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 */
	void exitOrFilter(ExpressionGrammarParser.OrFilterContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parentheseFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 */
	void enterParentheseFilter(ExpressionGrammarParser.ParentheseFilterContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parentheseFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 */
	void exitParentheseFilter(ExpressionGrammarParser.ParentheseFilterContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionGrammarParser#tagName}.
	 * @param ctx the parse tree
	 */
	void enterTagName(ExpressionGrammarParser.TagNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionGrammarParser#tagName}.
	 * @param ctx the parse tree
	 */
	void exitTagName(ExpressionGrammarParser.TagNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionGrammarParser#attName}.
	 * @param ctx the parse tree
	 */
	void enterAttName(ExpressionGrammarParser.AttNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionGrammarParser#attName}.
	 * @param ctx the parse tree
	 */
	void exitAttName(ExpressionGrammarParser.AttNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionGrammarParser#fileName}.
	 * @param ctx the parse tree
	 */
	void enterFileName(ExpressionGrammarParser.FileNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionGrammarParser#fileName}.
	 * @param ctx the parse tree
	 */
	void exitFileName(ExpressionGrammarParser.FileNameContext ctx);
}