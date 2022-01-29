// Generated from ExpressionGrammar.g4 by ANTLR 4.7.2

package edu.ucsd.CSE232B.parsers;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ExpressionGrammarParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ExpressionGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ExpressionGrammarParser#doc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDoc(ExpressionGrammarParser.DocContext ctx);
	/**
	 * Visit a parse tree produced by the {@code directAbsolutePath}
	 * labeled alternative in {@link ExpressionGrammarParser#ap}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirectAbsolutePath(ExpressionGrammarParser.DirectAbsolutePathContext ctx);
	/**
	 * Visit a parse tree produced by the {@code indirectAbsolutePath}
	 * labeled alternative in {@link ExpressionGrammarParser#ap}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndirectAbsolutePath(ExpressionGrammarParser.IndirectAbsolutePathContext ctx);
	/**
	 * Visit a parse tree produced by the {@code currentRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCurrentRelativePath(ExpressionGrammarParser.CurrentRelativePathContext ctx);
	/**
	 * Visit a parse tree produced by the {@code indirectRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndirectRelativePath(ExpressionGrammarParser.IndirectRelativePathContext ctx);
	/**
	 * Visit a parse tree produced by the {@code commaRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommaRelativePath(ExpressionGrammarParser.CommaRelativePathContext ctx);
	/**
	 * Visit a parse tree produced by the {@code filterRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFilterRelativePath(ExpressionGrammarParser.FilterRelativePathContext ctx);
	/**
	 * Visit a parse tree produced by the {@code tagRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTagRelativePath(ExpressionGrammarParser.TagRelativePathContext ctx);
	/**
	 * Visit a parse tree produced by the {@code attribRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttribRelativePath(ExpressionGrammarParser.AttribRelativePathContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parentheseRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParentheseRelativePath(ExpressionGrammarParser.ParentheseRelativePathContext ctx);
	/**
	 * Visit a parse tree produced by the {@code childrenRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChildrenRelativePath(ExpressionGrammarParser.ChildrenRelativePathContext ctx);
	/**
	 * Visit a parse tree produced by the {@code upperRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpperRelativePath(ExpressionGrammarParser.UpperRelativePathContext ctx);
	/**
	 * Visit a parse tree produced by the {@code directRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirectRelativePath(ExpressionGrammarParser.DirectRelativePathContext ctx);
	/**
	 * Visit a parse tree produced by the {@code textRelativePath}
	 * labeled alternative in {@link ExpressionGrammarParser#rp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTextRelativePath(ExpressionGrammarParser.TextRelativePathContext ctx);
	/**
	 * Visit a parse tree produced by the {@code equalFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualFilter(ExpressionGrammarParser.EqualFilterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stringconstantFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringconstantFilter(ExpressionGrammarParser.StringconstantFilterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code notFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotFilter(ExpressionGrammarParser.NotFilterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code andFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndFilter(ExpressionGrammarParser.AndFilterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code isFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIsFilter(ExpressionGrammarParser.IsFilterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code rpFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRpFilter(ExpressionGrammarParser.RpFilterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code orFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrFilter(ExpressionGrammarParser.OrFilterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parentheseFilter}
	 * labeled alternative in {@link ExpressionGrammarParser#f}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParentheseFilter(ExpressionGrammarParser.ParentheseFilterContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionGrammarParser#tagName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTagName(ExpressionGrammarParser.TagNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionGrammarParser#attName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttName(ExpressionGrammarParser.AttNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionGrammarParser#fileName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFileName(ExpressionGrammarParser.FileNameContext ctx);
}