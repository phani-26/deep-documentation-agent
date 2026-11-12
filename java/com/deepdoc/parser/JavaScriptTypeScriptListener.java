// Generated from src/main/antlr4/com/deepdoc/parser/JavaScriptTypeScript.g4 by ANTLR 4.13.1
package com.deepdoc.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link JavaScriptTypeScriptParser}.
 */
public interface JavaScriptTypeScriptListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompilationUnit(JavaScriptTypeScriptParser.CompilationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompilationUnit(JavaScriptTypeScriptParser.CompilationUnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#topLevel}.
	 * @param ctx the parse tree
	 */
	void enterTopLevel(JavaScriptTypeScriptParser.TopLevelContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#topLevel}.
	 * @param ctx the parse tree
	 */
	void exitTopLevel(JavaScriptTypeScriptParser.TopLevelContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#importDecl}.
	 * @param ctx the parse tree
	 */
	void enterImportDecl(JavaScriptTypeScriptParser.ImportDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#importDecl}.
	 * @param ctx the parse tree
	 */
	void exitImportDecl(JavaScriptTypeScriptParser.ImportDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#importBody}.
	 * @param ctx the parse tree
	 */
	void enterImportBody(JavaScriptTypeScriptParser.ImportBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#importBody}.
	 * @param ctx the parse tree
	 */
	void exitImportBody(JavaScriptTypeScriptParser.ImportBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#importClause}.
	 * @param ctx the parse tree
	 */
	void enterImportClause(JavaScriptTypeScriptParser.ImportClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#importClause}.
	 * @param ctx the parse tree
	 */
	void exitImportClause(JavaScriptTypeScriptParser.ImportClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#namedImports}.
	 * @param ctx the parse tree
	 */
	void enterNamedImports(JavaScriptTypeScriptParser.NamedImportsContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#namedImports}.
	 * @param ctx the parse tree
	 */
	void exitNamedImports(JavaScriptTypeScriptParser.NamedImportsContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#importSpec}.
	 * @param ctx the parse tree
	 */
	void enterImportSpec(JavaScriptTypeScriptParser.ImportSpecContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#importSpec}.
	 * @param ctx the parse tree
	 */
	void exitImportSpec(JavaScriptTypeScriptParser.ImportSpecContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#exportDecl}.
	 * @param ctx the parse tree
	 */
	void enterExportDecl(JavaScriptTypeScriptParser.ExportDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#exportDecl}.
	 * @param ctx the parse tree
	 */
	void exitExportDecl(JavaScriptTypeScriptParser.ExportDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#namedExports}.
	 * @param ctx the parse tree
	 */
	void enterNamedExports(JavaScriptTypeScriptParser.NamedExportsContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#namedExports}.
	 * @param ctx the parse tree
	 */
	void exitNamedExports(JavaScriptTypeScriptParser.NamedExportsContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#exportSpec}.
	 * @param ctx the parse tree
	 */
	void enterExportSpec(JavaScriptTypeScriptParser.ExportSpecContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#exportSpec}.
	 * @param ctx the parse tree
	 */
	void exitExportSpec(JavaScriptTypeScriptParser.ExportSpecContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#classDecl}.
	 * @param ctx the parse tree
	 */
	void enterClassDecl(JavaScriptTypeScriptParser.ClassDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#classDecl}.
	 * @param ctx the parse tree
	 */
	void exitClassDecl(JavaScriptTypeScriptParser.ClassDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#classHeritage}.
	 * @param ctx the parse tree
	 */
	void enterClassHeritage(JavaScriptTypeScriptParser.ClassHeritageContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#classHeritage}.
	 * @param ctx the parse tree
	 */
	void exitClassHeritage(JavaScriptTypeScriptParser.ClassHeritageContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#interfaceDecl}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceDecl(JavaScriptTypeScriptParser.InterfaceDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#interfaceDecl}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceDecl(JavaScriptTypeScriptParser.InterfaceDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#typeAliasDecl}.
	 * @param ctx the parse tree
	 */
	void enterTypeAliasDecl(JavaScriptTypeScriptParser.TypeAliasDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#typeAliasDecl}.
	 * @param ctx the parse tree
	 */
	void exitTypeAliasDecl(JavaScriptTypeScriptParser.TypeAliasDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#functionDecl}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDecl(JavaScriptTypeScriptParser.FunctionDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#functionDecl}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDecl(JavaScriptTypeScriptParser.FunctionDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#variableDecl}.
	 * @param ctx the parse tree
	 */
	void enterVariableDecl(JavaScriptTypeScriptParser.VariableDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#variableDecl}.
	 * @param ctx the parse tree
	 */
	void exitVariableDecl(JavaScriptTypeScriptParser.VariableDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#otherStatement}.
	 * @param ctx the parse tree
	 */
	void enterOtherStatement(JavaScriptTypeScriptParser.OtherStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#otherStatement}.
	 * @param ctx the parse tree
	 */
	void exitOtherStatement(JavaScriptTypeScriptParser.OtherStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#typeParams}.
	 * @param ctx the parse tree
	 */
	void enterTypeParams(JavaScriptTypeScriptParser.TypeParamsContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#typeParams}.
	 * @param ctx the parse tree
	 */
	void exitTypeParams(JavaScriptTypeScriptParser.TypeParamsContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#typeAnnotation}.
	 * @param ctx the parse tree
	 */
	void enterTypeAnnotation(JavaScriptTypeScriptParser.TypeAnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#typeAnnotation}.
	 * @param ctx the parse tree
	 */
	void exitTypeAnnotation(JavaScriptTypeScriptParser.TypeAnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#typeExpr}.
	 * @param ctx the parse tree
	 */
	void enterTypeExpr(JavaScriptTypeScriptParser.TypeExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#typeExpr}.
	 * @param ctx the parse tree
	 */
	void exitTypeExpr(JavaScriptTypeScriptParser.TypeExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#typeName}.
	 * @param ctx the parse tree
	 */
	void enterTypeName(JavaScriptTypeScriptParser.TypeNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#typeName}.
	 * @param ctx the parse tree
	 */
	void exitTypeName(JavaScriptTypeScriptParser.TypeNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#expressionish}.
	 * @param ctx the parse tree
	 */
	void enterExpressionish(JavaScriptTypeScriptParser.ExpressionishContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#expressionish}.
	 * @param ctx the parse tree
	 */
	void exitExpressionish(JavaScriptTypeScriptParser.ExpressionishContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(JavaScriptTypeScriptParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(JavaScriptTypeScriptParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#arrow}.
	 * @param ctx the parse tree
	 */
	void enterArrow(JavaScriptTypeScriptParser.ArrowContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#arrow}.
	 * @param ctx the parse tree
	 */
	void exitArrow(JavaScriptTypeScriptParser.ArrowContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#braceBlock}.
	 * @param ctx the parse tree
	 */
	void enterBraceBlock(JavaScriptTypeScriptParser.BraceBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#braceBlock}.
	 * @param ctx the parse tree
	 */
	void exitBraceBlock(JavaScriptTypeScriptParser.BraceBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#parenBlock}.
	 * @param ctx the parse tree
	 */
	void enterParenBlock(JavaScriptTypeScriptParser.ParenBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#parenBlock}.
	 * @param ctx the parse tree
	 */
	void exitParenBlock(JavaScriptTypeScriptParser.ParenBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link JavaScriptTypeScriptParser#bracketBlock}.
	 * @param ctx the parse tree
	 */
	void enterBracketBlock(JavaScriptTypeScriptParser.BracketBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link JavaScriptTypeScriptParser#bracketBlock}.
	 * @param ctx the parse tree
	 */
	void exitBracketBlock(JavaScriptTypeScriptParser.BracketBlockContext ctx);
}