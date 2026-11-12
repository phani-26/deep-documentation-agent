// Generated from src/main/antlr4/com/deepdoc/parser/JavaScriptTypeScript.g4 by ANTLR 4.13.1
package com.deepdoc.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link JavaScriptTypeScriptParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface JavaScriptTypeScriptVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#compilationUnit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompilationUnit(JavaScriptTypeScriptParser.CompilationUnitContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#topLevel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTopLevel(JavaScriptTypeScriptParser.TopLevelContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#importDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportDecl(JavaScriptTypeScriptParser.ImportDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#importBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportBody(JavaScriptTypeScriptParser.ImportBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#importClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportClause(JavaScriptTypeScriptParser.ImportClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#namedImports}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamedImports(JavaScriptTypeScriptParser.NamedImportsContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#importSpec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportSpec(JavaScriptTypeScriptParser.ImportSpecContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#exportDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExportDecl(JavaScriptTypeScriptParser.ExportDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#namedExports}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamedExports(JavaScriptTypeScriptParser.NamedExportsContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#exportSpec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExportSpec(JavaScriptTypeScriptParser.ExportSpecContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#classDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDecl(JavaScriptTypeScriptParser.ClassDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#classHeritage}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassHeritage(JavaScriptTypeScriptParser.ClassHeritageContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#interfaceDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceDecl(JavaScriptTypeScriptParser.InterfaceDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#typeAliasDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeAliasDecl(JavaScriptTypeScriptParser.TypeAliasDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#functionDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDecl(JavaScriptTypeScriptParser.FunctionDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#variableDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDecl(JavaScriptTypeScriptParser.VariableDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#otherStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOtherStatement(JavaScriptTypeScriptParser.OtherStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#typeParams}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeParams(JavaScriptTypeScriptParser.TypeParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#typeAnnotation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeAnnotation(JavaScriptTypeScriptParser.TypeAnnotationContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#typeExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeExpr(JavaScriptTypeScriptParser.TypeExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#typeName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeName(JavaScriptTypeScriptParser.TypeNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#expressionish}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionish(JavaScriptTypeScriptParser.ExpressionishContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(JavaScriptTypeScriptParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#arrow}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrow(JavaScriptTypeScriptParser.ArrowContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#braceBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBraceBlock(JavaScriptTypeScriptParser.BraceBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#parenBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenBlock(JavaScriptTypeScriptParser.ParenBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link JavaScriptTypeScriptParser#bracketBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBracketBlock(JavaScriptTypeScriptParser.BracketBlockContext ctx);
}