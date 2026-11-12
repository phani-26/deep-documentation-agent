// Generated from src/main/antlr4/com/deepdoc/parser/JavaScriptTypeScript.g4 by ANTLR 4.13.1
package com.deepdoc.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class JavaScriptTypeScriptParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		IMPORT=1, EXPORT=2, FROM=3, AS=4, CLASS=5, INTERFACE=6, TYPE=7, FUNCTION=8, 
		CONST=9, LET=10, VAR=11, EXTENDS=12, IMPLEMENTS=13, DEFAULT=14, ASYNC=15, 
		ABSTRACT=16, NEW=17, AWAIT=18, TYPEOF=19, BooleanLiteral=20, NullLiteral=21, 
		ARROW=22, EQEQEQ=23, NEQEQ=24, EQEQ=25, NEQ=26, LTE=27, GTE=28, AMPAMP=29, 
		PIPEPIPE=30, EQ=31, PLUS=32, MINUS=33, STAR=34, SLASH=35, PERCENT=36, 
		BANG=37, QUESTION=38, COLON=39, DOT=40, COMMA=41, PIPE=42, LT=43, GT=44, 
		SEMI=45, LBRACE=46, RBRACE=47, LPAREN=48, RPAREN=49, LBRACK=50, RBRACK=51, 
		Identifier=52, StringLiteral=53, TemplateLiteral=54, NumberLiteral=55, 
		LineComment=56, BlockComment=57, WS=58;
	public static final int
		RULE_compilationUnit = 0, RULE_topLevel = 1, RULE_importDecl = 2, RULE_importBody = 3, 
		RULE_importClause = 4, RULE_namedImports = 5, RULE_importSpec = 6, RULE_exportDecl = 7, 
		RULE_namedExports = 8, RULE_exportSpec = 9, RULE_classDecl = 10, RULE_classHeritage = 11, 
		RULE_interfaceDecl = 12, RULE_typeAliasDecl = 13, RULE_functionDecl = 14, 
		RULE_variableDecl = 15, RULE_otherStatement = 16, RULE_typeParams = 17, 
		RULE_typeAnnotation = 18, RULE_typeExpr = 19, RULE_typeName = 20, RULE_expressionish = 21, 
		RULE_atom = 22, RULE_arrow = 23, RULE_braceBlock = 24, RULE_parenBlock = 25, 
		RULE_bracketBlock = 26;
	private static String[] makeRuleNames() {
		return new String[] {
			"compilationUnit", "topLevel", "importDecl", "importBody", "importClause", 
			"namedImports", "importSpec", "exportDecl", "namedExports", "exportSpec", 
			"classDecl", "classHeritage", "interfaceDecl", "typeAliasDecl", "functionDecl", 
			"variableDecl", "otherStatement", "typeParams", "typeAnnotation", "typeExpr", 
			"typeName", "expressionish", "atom", "arrow", "braceBlock", "parenBlock", 
			"bracketBlock"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'import'", "'export'", "'from'", "'as'", "'class'", "'interface'", 
			"'type'", "'function'", "'const'", "'let'", "'var'", "'extends'", "'implements'", 
			"'default'", "'async'", "'abstract'", "'new'", "'await'", "'typeof'", 
			null, null, "'=>'", "'==='", "'!=='", "'=='", "'!='", "'<='", "'>='", 
			"'&&'", "'||'", "'='", "'+'", "'-'", "'*'", "'/'", "'%'", "'!'", "'?'", 
			"':'", "'.'", "','", "'|'", "'<'", "'>'", "';'", "'{'", "'}'", "'('", 
			"')'", "'['", "']'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "IMPORT", "EXPORT", "FROM", "AS", "CLASS", "INTERFACE", "TYPE", 
			"FUNCTION", "CONST", "LET", "VAR", "EXTENDS", "IMPLEMENTS", "DEFAULT", 
			"ASYNC", "ABSTRACT", "NEW", "AWAIT", "TYPEOF", "BooleanLiteral", "NullLiteral", 
			"ARROW", "EQEQEQ", "NEQEQ", "EQEQ", "NEQ", "LTE", "GTE", "AMPAMP", "PIPEPIPE", 
			"EQ", "PLUS", "MINUS", "STAR", "SLASH", "PERCENT", "BANG", "QUESTION", 
			"COLON", "DOT", "COMMA", "PIPE", "LT", "GT", "SEMI", "LBRACE", "RBRACE", 
			"LPAREN", "RPAREN", "LBRACK", "RBRACK", "Identifier", "StringLiteral", 
			"TemplateLiteral", "NumberLiteral", "LineComment", "BlockComment", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "JavaScriptTypeScript.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public JavaScriptTypeScriptParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CompilationUnitContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(JavaScriptTypeScriptParser.EOF, 0); }
		public List<TopLevelContext> topLevel() {
			return getRuleContexts(TopLevelContext.class);
		}
		public TopLevelContext topLevel(int i) {
			return getRuleContext(TopLevelContext.class,i);
		}
		public CompilationUnitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compilationUnit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterCompilationUnit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitCompilationUnit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitCompilationUnit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CompilationUnitContext compilationUnit() throws RecognitionException {
		CompilationUnitContext _localctx = new CompilationUnitContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_compilationUnit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(57);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 69097708735926246L) != 0)) {
				{
				{
				setState(54);
				topLevel();
				}
				}
				setState(59);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(60);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TopLevelContext extends ParserRuleContext {
		public ImportDeclContext importDecl() {
			return getRuleContext(ImportDeclContext.class,0);
		}
		public ExportDeclContext exportDecl() {
			return getRuleContext(ExportDeclContext.class,0);
		}
		public ClassDeclContext classDecl() {
			return getRuleContext(ClassDeclContext.class,0);
		}
		public InterfaceDeclContext interfaceDecl() {
			return getRuleContext(InterfaceDeclContext.class,0);
		}
		public TypeAliasDeclContext typeAliasDecl() {
			return getRuleContext(TypeAliasDeclContext.class,0);
		}
		public FunctionDeclContext functionDecl() {
			return getRuleContext(FunctionDeclContext.class,0);
		}
		public VariableDeclContext variableDecl() {
			return getRuleContext(VariableDeclContext.class,0);
		}
		public OtherStatementContext otherStatement() {
			return getRuleContext(OtherStatementContext.class,0);
		}
		public TopLevelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_topLevel; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterTopLevel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitTopLevel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitTopLevel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TopLevelContext topLevel() throws RecognitionException {
		TopLevelContext _localctx = new TopLevelContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_topLevel);
		try {
			setState(70);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(62);
				importDecl();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(63);
				exportDecl();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(64);
				classDecl();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(65);
				interfaceDecl();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(66);
				typeAliasDecl();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(67);
				functionDecl();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(68);
				variableDecl();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(69);
				otherStatement();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ImportDeclContext extends ParserRuleContext {
		public TerminalNode IMPORT() { return getToken(JavaScriptTypeScriptParser.IMPORT, 0); }
		public ImportBodyContext importBody() {
			return getRuleContext(ImportBodyContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(JavaScriptTypeScriptParser.SEMI, 0); }
		public ImportDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterImportDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitImportDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitImportDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ImportDeclContext importDecl() throws RecognitionException {
		ImportDeclContext _localctx = new ImportDeclContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_importDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			match(IMPORT);
			setState(73);
			importBody();
			setState(75);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(74);
				match(SEMI);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ImportBodyContext extends ParserRuleContext {
		public TerminalNode StringLiteral() { return getToken(JavaScriptTypeScriptParser.StringLiteral, 0); }
		public ImportClauseContext importClause() {
			return getRuleContext(ImportClauseContext.class,0);
		}
		public TerminalNode FROM() { return getToken(JavaScriptTypeScriptParser.FROM, 0); }
		public ImportBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterImportBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitImportBody(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitImportBody(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ImportBodyContext importBody() throws RecognitionException {
		ImportBodyContext _localctx = new ImportBodyContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_importBody);
		try {
			setState(82);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case StringLiteral:
				enterOuterAlt(_localctx, 1);
				{
				setState(77);
				match(StringLiteral);
				}
				break;
			case STAR:
			case LBRACE:
			case Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(78);
				importClause();
				setState(79);
				match(FROM);
				setState(80);
				match(StringLiteral);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ImportClauseContext extends ParserRuleContext {
		public TerminalNode STAR() { return getToken(JavaScriptTypeScriptParser.STAR, 0); }
		public TerminalNode AS() { return getToken(JavaScriptTypeScriptParser.AS, 0); }
		public TerminalNode Identifier() { return getToken(JavaScriptTypeScriptParser.Identifier, 0); }
		public TerminalNode COMMA() { return getToken(JavaScriptTypeScriptParser.COMMA, 0); }
		public NamedImportsContext namedImports() {
			return getRuleContext(NamedImportsContext.class,0);
		}
		public ImportClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterImportClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitImportClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitImportClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ImportClauseContext importClause() throws RecognitionException {
		ImportClauseContext _localctx = new ImportClauseContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_importClause);
		int _la;
		try {
			setState(93);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(84);
				match(STAR);
				setState(85);
				match(AS);
				setState(86);
				match(Identifier);
				}
				break;
			case Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(87);
				match(Identifier);
				setState(90);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(88);
					match(COMMA);
					setState(89);
					namedImports();
					}
				}

				}
				break;
			case LBRACE:
				enterOuterAlt(_localctx, 3);
				{
				setState(92);
				namedImports();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NamedImportsContext extends ParserRuleContext {
		public TerminalNode LBRACE() { return getToken(JavaScriptTypeScriptParser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(JavaScriptTypeScriptParser.RBRACE, 0); }
		public List<ImportSpecContext> importSpec() {
			return getRuleContexts(ImportSpecContext.class);
		}
		public ImportSpecContext importSpec(int i) {
			return getRuleContext(ImportSpecContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(JavaScriptTypeScriptParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(JavaScriptTypeScriptParser.COMMA, i);
		}
		public NamedImportsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_namedImports; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterNamedImports(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitNamedImports(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitNamedImports(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NamedImportsContext namedImports() throws RecognitionException {
		NamedImportsContext _localctx = new NamedImportsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_namedImports);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			match(LBRACE);
			setState(107);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Identifier) {
				{
				setState(96);
				importSpec();
				setState(101);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(97);
						match(COMMA);
						setState(98);
						importSpec();
						}
						} 
					}
					setState(103);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
				}
				setState(105);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(104);
					match(COMMA);
					}
				}

				}
			}

			setState(109);
			match(RBRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ImportSpecContext extends ParserRuleContext {
		public List<TerminalNode> Identifier() { return getTokens(JavaScriptTypeScriptParser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(JavaScriptTypeScriptParser.Identifier, i);
		}
		public TerminalNode AS() { return getToken(JavaScriptTypeScriptParser.AS, 0); }
		public ImportSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterImportSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitImportSpec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitImportSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ImportSpecContext importSpec() throws RecognitionException {
		ImportSpecContext _localctx = new ImportSpecContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_importSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			match(Identifier);
			setState(114);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(112);
				match(AS);
				setState(113);
				match(Identifier);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExportDeclContext extends ParserRuleContext {
		public TerminalNode EXPORT() { return getToken(JavaScriptTypeScriptParser.EXPORT, 0); }
		public ClassDeclContext classDecl() {
			return getRuleContext(ClassDeclContext.class,0);
		}
		public FunctionDeclContext functionDecl() {
			return getRuleContext(FunctionDeclContext.class,0);
		}
		public InterfaceDeclContext interfaceDecl() {
			return getRuleContext(InterfaceDeclContext.class,0);
		}
		public TypeAliasDeclContext typeAliasDecl() {
			return getRuleContext(TypeAliasDeclContext.class,0);
		}
		public VariableDeclContext variableDecl() {
			return getRuleContext(VariableDeclContext.class,0);
		}
		public TerminalNode DEFAULT() { return getToken(JavaScriptTypeScriptParser.DEFAULT, 0); }
		public NamedExportsContext namedExports() {
			return getRuleContext(NamedExportsContext.class,0);
		}
		public TerminalNode FROM() { return getToken(JavaScriptTypeScriptParser.FROM, 0); }
		public TerminalNode StringLiteral() { return getToken(JavaScriptTypeScriptParser.StringLiteral, 0); }
		public TerminalNode SEMI() { return getToken(JavaScriptTypeScriptParser.SEMI, 0); }
		public ExpressionishContext expressionish() {
			return getRuleContext(ExpressionishContext.class,0);
		}
		public TerminalNode STAR() { return getToken(JavaScriptTypeScriptParser.STAR, 0); }
		public TerminalNode AS() { return getToken(JavaScriptTypeScriptParser.AS, 0); }
		public TerminalNode Identifier() { return getToken(JavaScriptTypeScriptParser.Identifier, 0); }
		public ExportDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exportDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterExportDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitExportDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitExportDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExportDeclContext exportDecl() throws RecognitionException {
		ExportDeclContext _localctx = new ExportDeclContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_exportDecl);
		int _la;
		try {
			setState(153);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(116);
				match(EXPORT);
				setState(118);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DEFAULT) {
					{
					setState(117);
					match(DEFAULT);
					}
				}

				setState(125);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case CLASS:
				case ABSTRACT:
					{
					setState(120);
					classDecl();
					}
					break;
				case FUNCTION:
				case ASYNC:
					{
					setState(121);
					functionDecl();
					}
					break;
				case INTERFACE:
					{
					setState(122);
					interfaceDecl();
					}
					break;
				case TYPE:
					{
					setState(123);
					typeAliasDecl();
					}
					break;
				case CONST:
				case LET:
				case VAR:
					{
					setState(124);
					variableDecl();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(127);
				match(EXPORT);
				setState(128);
				namedExports();
				setState(131);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FROM) {
					{
					setState(129);
					match(FROM);
					setState(130);
					match(StringLiteral);
					}
				}

				setState(134);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
				case 1:
					{
					setState(133);
					match(SEMI);
					}
					break;
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(136);
				match(EXPORT);
				setState(137);
				match(DEFAULT);
				setState(138);
				expressionish();
				setState(140);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
				case 1:
					{
					setState(139);
					match(SEMI);
					}
					break;
				}
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(142);
				match(EXPORT);
				setState(143);
				match(STAR);
				setState(146);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AS) {
					{
					setState(144);
					match(AS);
					setState(145);
					match(Identifier);
					}
				}

				setState(148);
				match(FROM);
				setState(149);
				match(StringLiteral);
				setState(151);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
				case 1:
					{
					setState(150);
					match(SEMI);
					}
					break;
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NamedExportsContext extends ParserRuleContext {
		public TerminalNode LBRACE() { return getToken(JavaScriptTypeScriptParser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(JavaScriptTypeScriptParser.RBRACE, 0); }
		public List<ExportSpecContext> exportSpec() {
			return getRuleContexts(ExportSpecContext.class);
		}
		public ExportSpecContext exportSpec(int i) {
			return getRuleContext(ExportSpecContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(JavaScriptTypeScriptParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(JavaScriptTypeScriptParser.COMMA, i);
		}
		public NamedExportsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_namedExports; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterNamedExports(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitNamedExports(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitNamedExports(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NamedExportsContext namedExports() throws RecognitionException {
		NamedExportsContext _localctx = new NamedExportsContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_namedExports);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(155);
			match(LBRACE);
			setState(167);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Identifier) {
				{
				setState(156);
				exportSpec();
				setState(161);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(157);
						match(COMMA);
						setState(158);
						exportSpec();
						}
						} 
					}
					setState(163);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
				}
				setState(165);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(164);
					match(COMMA);
					}
				}

				}
			}

			setState(169);
			match(RBRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExportSpecContext extends ParserRuleContext {
		public List<TerminalNode> Identifier() { return getTokens(JavaScriptTypeScriptParser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(JavaScriptTypeScriptParser.Identifier, i);
		}
		public TerminalNode AS() { return getToken(JavaScriptTypeScriptParser.AS, 0); }
		public ExportSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exportSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterExportSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitExportSpec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitExportSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExportSpecContext exportSpec() throws RecognitionException {
		ExportSpecContext _localctx = new ExportSpecContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_exportSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
			match(Identifier);
			setState(174);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(172);
				match(AS);
				setState(173);
				match(Identifier);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ClassDeclContext extends ParserRuleContext {
		public TerminalNode CLASS() { return getToken(JavaScriptTypeScriptParser.CLASS, 0); }
		public TerminalNode Identifier() { return getToken(JavaScriptTypeScriptParser.Identifier, 0); }
		public BraceBlockContext braceBlock() {
			return getRuleContext(BraceBlockContext.class,0);
		}
		public TerminalNode ABSTRACT() { return getToken(JavaScriptTypeScriptParser.ABSTRACT, 0); }
		public TypeParamsContext typeParams() {
			return getRuleContext(TypeParamsContext.class,0);
		}
		public ClassHeritageContext classHeritage() {
			return getRuleContext(ClassHeritageContext.class,0);
		}
		public ClassDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterClassDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitClassDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitClassDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClassDeclContext classDecl() throws RecognitionException {
		ClassDeclContext _localctx = new ClassDeclContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_classDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(177);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ABSTRACT) {
				{
				setState(176);
				match(ABSTRACT);
				}
			}

			setState(179);
			match(CLASS);
			setState(180);
			match(Identifier);
			setState(182);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(181);
				typeParams();
				}
			}

			setState(185);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXTENDS || _la==IMPLEMENTS) {
				{
				setState(184);
				classHeritage();
				}
			}

			setState(187);
			braceBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ClassHeritageContext extends ParserRuleContext {
		public TerminalNode EXTENDS() { return getToken(JavaScriptTypeScriptParser.EXTENDS, 0); }
		public List<TypeNameContext> typeName() {
			return getRuleContexts(TypeNameContext.class);
		}
		public TypeNameContext typeName(int i) {
			return getRuleContext(TypeNameContext.class,i);
		}
		public TerminalNode IMPLEMENTS() { return getToken(JavaScriptTypeScriptParser.IMPLEMENTS, 0); }
		public List<TerminalNode> COMMA() { return getTokens(JavaScriptTypeScriptParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(JavaScriptTypeScriptParser.COMMA, i);
		}
		public ClassHeritageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classHeritage; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterClassHeritage(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitClassHeritage(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitClassHeritage(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClassHeritageContext classHeritage() throws RecognitionException {
		ClassHeritageContext _localctx = new ClassHeritageContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_classHeritage);
		int _la;
		try {
			setState(211);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EXTENDS:
				enterOuterAlt(_localctx, 1);
				{
				setState(189);
				match(EXTENDS);
				setState(190);
				typeName();
				setState(200);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IMPLEMENTS) {
					{
					setState(191);
					match(IMPLEMENTS);
					setState(192);
					typeName();
					setState(197);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(193);
						match(COMMA);
						setState(194);
						typeName();
						}
						}
						setState(199);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				}
				break;
			case IMPLEMENTS:
				enterOuterAlt(_localctx, 2);
				{
				setState(202);
				match(IMPLEMENTS);
				setState(203);
				typeName();
				setState(208);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(204);
					match(COMMA);
					setState(205);
					typeName();
					}
					}
					setState(210);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InterfaceDeclContext extends ParserRuleContext {
		public TerminalNode INTERFACE() { return getToken(JavaScriptTypeScriptParser.INTERFACE, 0); }
		public TerminalNode Identifier() { return getToken(JavaScriptTypeScriptParser.Identifier, 0); }
		public BraceBlockContext braceBlock() {
			return getRuleContext(BraceBlockContext.class,0);
		}
		public TypeParamsContext typeParams() {
			return getRuleContext(TypeParamsContext.class,0);
		}
		public TerminalNode EXTENDS() { return getToken(JavaScriptTypeScriptParser.EXTENDS, 0); }
		public List<TypeNameContext> typeName() {
			return getRuleContexts(TypeNameContext.class);
		}
		public TypeNameContext typeName(int i) {
			return getRuleContext(TypeNameContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(JavaScriptTypeScriptParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(JavaScriptTypeScriptParser.COMMA, i);
		}
		public InterfaceDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterInterfaceDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitInterfaceDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitInterfaceDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InterfaceDeclContext interfaceDecl() throws RecognitionException {
		InterfaceDeclContext _localctx = new InterfaceDeclContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_interfaceDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(213);
			match(INTERFACE);
			setState(214);
			match(Identifier);
			setState(216);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(215);
				typeParams();
				}
			}

			setState(227);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXTENDS) {
				{
				setState(218);
				match(EXTENDS);
				setState(219);
				typeName();
				setState(224);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(220);
					match(COMMA);
					setState(221);
					typeName();
					}
					}
					setState(226);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(229);
			braceBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeAliasDeclContext extends ParserRuleContext {
		public TerminalNode TYPE() { return getToken(JavaScriptTypeScriptParser.TYPE, 0); }
		public TerminalNode Identifier() { return getToken(JavaScriptTypeScriptParser.Identifier, 0); }
		public TerminalNode EQ() { return getToken(JavaScriptTypeScriptParser.EQ, 0); }
		public TypeExprContext typeExpr() {
			return getRuleContext(TypeExprContext.class,0);
		}
		public TypeParamsContext typeParams() {
			return getRuleContext(TypeParamsContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(JavaScriptTypeScriptParser.SEMI, 0); }
		public TypeAliasDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeAliasDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterTypeAliasDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitTypeAliasDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitTypeAliasDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeAliasDeclContext typeAliasDecl() throws RecognitionException {
		TypeAliasDeclContext _localctx = new TypeAliasDeclContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_typeAliasDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(231);
			match(TYPE);
			setState(232);
			match(Identifier);
			setState(234);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(233);
				typeParams();
				}
			}

			setState(236);
			match(EQ);
			setState(237);
			typeExpr();
			setState(239);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				{
				setState(238);
				match(SEMI);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionDeclContext extends ParserRuleContext {
		public TerminalNode FUNCTION() { return getToken(JavaScriptTypeScriptParser.FUNCTION, 0); }
		public TerminalNode Identifier() { return getToken(JavaScriptTypeScriptParser.Identifier, 0); }
		public ParenBlockContext parenBlock() {
			return getRuleContext(ParenBlockContext.class,0);
		}
		public BraceBlockContext braceBlock() {
			return getRuleContext(BraceBlockContext.class,0);
		}
		public TerminalNode ASYNC() { return getToken(JavaScriptTypeScriptParser.ASYNC, 0); }
		public TerminalNode STAR() { return getToken(JavaScriptTypeScriptParser.STAR, 0); }
		public TypeParamsContext typeParams() {
			return getRuleContext(TypeParamsContext.class,0);
		}
		public TypeAnnotationContext typeAnnotation() {
			return getRuleContext(TypeAnnotationContext.class,0);
		}
		public FunctionDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterFunctionDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitFunctionDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitFunctionDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionDeclContext functionDecl() throws RecognitionException {
		FunctionDeclContext _localctx = new FunctionDeclContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_functionDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(242);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASYNC) {
				{
				setState(241);
				match(ASYNC);
				}
			}

			setState(244);
			match(FUNCTION);
			setState(246);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STAR) {
				{
				setState(245);
				match(STAR);
				}
			}

			setState(248);
			match(Identifier);
			setState(250);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(249);
				typeParams();
				}
			}

			setState(252);
			parenBlock();
			setState(254);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(253);
				typeAnnotation();
				}
			}

			setState(256);
			braceBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VariableDeclContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(JavaScriptTypeScriptParser.Identifier, 0); }
		public TerminalNode CONST() { return getToken(JavaScriptTypeScriptParser.CONST, 0); }
		public TerminalNode LET() { return getToken(JavaScriptTypeScriptParser.LET, 0); }
		public TerminalNode VAR() { return getToken(JavaScriptTypeScriptParser.VAR, 0); }
		public TypeAnnotationContext typeAnnotation() {
			return getRuleContext(TypeAnnotationContext.class,0);
		}
		public TerminalNode EQ() { return getToken(JavaScriptTypeScriptParser.EQ, 0); }
		public ExpressionishContext expressionish() {
			return getRuleContext(ExpressionishContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(JavaScriptTypeScriptParser.SEMI, 0); }
		public VariableDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterVariableDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitVariableDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitVariableDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableDeclContext variableDecl() throws RecognitionException {
		VariableDeclContext _localctx = new VariableDeclContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_variableDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(258);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 3584L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(259);
			match(Identifier);
			setState(261);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				{
				setState(260);
				typeAnnotation();
				}
				break;
			}
			setState(265);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				{
				setState(263);
				match(EQ);
				setState(264);
				expressionish();
				}
				break;
			}
			setState(268);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				{
				setState(267);
				match(SEMI);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OtherStatementContext extends ParserRuleContext {
		public ExpressionishContext expressionish() {
			return getRuleContext(ExpressionishContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(JavaScriptTypeScriptParser.SEMI, 0); }
		public BraceBlockContext braceBlock() {
			return getRuleContext(BraceBlockContext.class,0);
		}
		public OtherStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_otherStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterOtherStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitOtherStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitOtherStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OtherStatementContext otherStatement() throws RecognitionException {
		OtherStatementContext _localctx = new OtherStatementContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_otherStatement);
		try {
			setState(276);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(270);
				expressionish();
				setState(272);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
				case 1:
					{
					setState(271);
					match(SEMI);
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(274);
				braceBlock();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(275);
				match(SEMI);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeParamsContext extends ParserRuleContext {
		public TerminalNode LT() { return getToken(JavaScriptTypeScriptParser.LT, 0); }
		public List<TerminalNode> Identifier() { return getTokens(JavaScriptTypeScriptParser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(JavaScriptTypeScriptParser.Identifier, i);
		}
		public TerminalNode GT() { return getToken(JavaScriptTypeScriptParser.GT, 0); }
		public List<TerminalNode> COMMA() { return getTokens(JavaScriptTypeScriptParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(JavaScriptTypeScriptParser.COMMA, i);
		}
		public TypeParamsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeParams; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterTypeParams(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitTypeParams(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitTypeParams(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeParamsContext typeParams() throws RecognitionException {
		TypeParamsContext _localctx = new TypeParamsContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_typeParams);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(278);
			match(LT);
			setState(279);
			match(Identifier);
			setState(284);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(280);
				match(COMMA);
				setState(281);
				match(Identifier);
				}
				}
				setState(286);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(287);
			match(GT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeAnnotationContext extends ParserRuleContext {
		public TerminalNode COLON() { return getToken(JavaScriptTypeScriptParser.COLON, 0); }
		public TypeExprContext typeExpr() {
			return getRuleContext(TypeExprContext.class,0);
		}
		public TypeAnnotationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeAnnotation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterTypeAnnotation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitTypeAnnotation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitTypeAnnotation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeAnnotationContext typeAnnotation() throws RecognitionException {
		TypeAnnotationContext _localctx = new TypeAnnotationContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_typeAnnotation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(289);
			match(COLON);
			setState(290);
			typeExpr();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeExprContext extends ParserRuleContext {
		public List<TypeNameContext> typeName() {
			return getRuleContexts(TypeNameContext.class);
		}
		public TypeNameContext typeName(int i) {
			return getRuleContext(TypeNameContext.class,i);
		}
		public List<TerminalNode> PIPE() { return getTokens(JavaScriptTypeScriptParser.PIPE); }
		public TerminalNode PIPE(int i) {
			return getToken(JavaScriptTypeScriptParser.PIPE, i);
		}
		public TypeExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterTypeExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitTypeExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitTypeExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeExprContext typeExpr() throws RecognitionException {
		TypeExprContext _localctx = new TypeExprContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_typeExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(292);
			typeName();
			setState(297);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PIPE) {
				{
				{
				setState(293);
				match(PIPE);
				setState(294);
				typeName();
				}
				}
				setState(299);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeNameContext extends ParserRuleContext {
		public List<TerminalNode> Identifier() { return getTokens(JavaScriptTypeScriptParser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(JavaScriptTypeScriptParser.Identifier, i);
		}
		public List<TerminalNode> DOT() { return getTokens(JavaScriptTypeScriptParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(JavaScriptTypeScriptParser.DOT, i);
		}
		public TerminalNode LT() { return getToken(JavaScriptTypeScriptParser.LT, 0); }
		public List<TypeExprContext> typeExpr() {
			return getRuleContexts(TypeExprContext.class);
		}
		public TypeExprContext typeExpr(int i) {
			return getRuleContext(TypeExprContext.class,i);
		}
		public TerminalNode GT() { return getToken(JavaScriptTypeScriptParser.GT, 0); }
		public List<TerminalNode> COMMA() { return getTokens(JavaScriptTypeScriptParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(JavaScriptTypeScriptParser.COMMA, i);
		}
		public TerminalNode StringLiteral() { return getToken(JavaScriptTypeScriptParser.StringLiteral, 0); }
		public TypeNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterTypeName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitTypeName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitTypeName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeNameContext typeName() throws RecognitionException {
		TypeNameContext _localctx = new TypeNameContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_typeName);
		int _la;
		try {
			int _alt;
			setState(322);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(300);
				match(Identifier);
				setState(305);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,45,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(301);
						match(DOT);
						setState(302);
						match(Identifier);
						}
						} 
					}
					setState(307);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,45,_ctx);
				}
				setState(319);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
				case 1:
					{
					setState(308);
					match(LT);
					setState(309);
					typeExpr();
					setState(314);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(310);
						match(COMMA);
						setState(311);
						typeExpr();
						}
						}
						setState(316);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(317);
					match(GT);
					}
					break;
				}
				}
				break;
			case StringLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(321);
				match(StringLiteral);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionishContext extends ParserRuleContext {
		public List<AtomContext> atom() {
			return getRuleContexts(AtomContext.class);
		}
		public AtomContext atom(int i) {
			return getRuleContext(AtomContext.class,i);
		}
		public ExpressionishContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionish; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterExpressionish(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitExpressionish(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitExpressionish(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionishContext expressionish() throws RecognitionException {
		ExpressionishContext _localctx = new ExpressionishContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_expressionish);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(324);
			atom();
			setState(328);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(325);
					atom();
					}
					} 
				}
				setState(330);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AtomContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(JavaScriptTypeScriptParser.Identifier, 0); }
		public TerminalNode StringLiteral() { return getToken(JavaScriptTypeScriptParser.StringLiteral, 0); }
		public TerminalNode TemplateLiteral() { return getToken(JavaScriptTypeScriptParser.TemplateLiteral, 0); }
		public TerminalNode NumberLiteral() { return getToken(JavaScriptTypeScriptParser.NumberLiteral, 0); }
		public TerminalNode BooleanLiteral() { return getToken(JavaScriptTypeScriptParser.BooleanLiteral, 0); }
		public TerminalNode NullLiteral() { return getToken(JavaScriptTypeScriptParser.NullLiteral, 0); }
		public TerminalNode ASYNC() { return getToken(JavaScriptTypeScriptParser.ASYNC, 0); }
		public TerminalNode FUNCTION() { return getToken(JavaScriptTypeScriptParser.FUNCTION, 0); }
		public TerminalNode CLASS() { return getToken(JavaScriptTypeScriptParser.CLASS, 0); }
		public TerminalNode NEW() { return getToken(JavaScriptTypeScriptParser.NEW, 0); }
		public TerminalNode AWAIT() { return getToken(JavaScriptTypeScriptParser.AWAIT, 0); }
		public TerminalNode TYPEOF() { return getToken(JavaScriptTypeScriptParser.TYPEOF, 0); }
		public ArrowContext arrow() {
			return getRuleContext(ArrowContext.class,0);
		}
		public BraceBlockContext braceBlock() {
			return getRuleContext(BraceBlockContext.class,0);
		}
		public ParenBlockContext parenBlock() {
			return getRuleContext(ParenBlockContext.class,0);
		}
		public BracketBlockContext bracketBlock() {
			return getRuleContext(BracketBlockContext.class,0);
		}
		public TerminalNode LT() { return getToken(JavaScriptTypeScriptParser.LT, 0); }
		public TerminalNode GT() { return getToken(JavaScriptTypeScriptParser.GT, 0); }
		public TerminalNode SLASH() { return getToken(JavaScriptTypeScriptParser.SLASH, 0); }
		public TerminalNode EQ() { return getToken(JavaScriptTypeScriptParser.EQ, 0); }
		public TerminalNode ARROW() { return getToken(JavaScriptTypeScriptParser.ARROW, 0); }
		public TerminalNode DOT() { return getToken(JavaScriptTypeScriptParser.DOT, 0); }
		public TerminalNode COMMA() { return getToken(JavaScriptTypeScriptParser.COMMA, 0); }
		public TerminalNode COLON() { return getToken(JavaScriptTypeScriptParser.COLON, 0); }
		public TerminalNode QUESTION() { return getToken(JavaScriptTypeScriptParser.QUESTION, 0); }
		public TerminalNode PLUS() { return getToken(JavaScriptTypeScriptParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(JavaScriptTypeScriptParser.MINUS, 0); }
		public TerminalNode STAR() { return getToken(JavaScriptTypeScriptParser.STAR, 0); }
		public TerminalNode PERCENT() { return getToken(JavaScriptTypeScriptParser.PERCENT, 0); }
		public TerminalNode AMPAMP() { return getToken(JavaScriptTypeScriptParser.AMPAMP, 0); }
		public TerminalNode PIPEPIPE() { return getToken(JavaScriptTypeScriptParser.PIPEPIPE, 0); }
		public TerminalNode BANG() { return getToken(JavaScriptTypeScriptParser.BANG, 0); }
		public TerminalNode LTE() { return getToken(JavaScriptTypeScriptParser.LTE, 0); }
		public TerminalNode GTE() { return getToken(JavaScriptTypeScriptParser.GTE, 0); }
		public TerminalNode EQEQ() { return getToken(JavaScriptTypeScriptParser.EQEQ, 0); }
		public TerminalNode NEQ() { return getToken(JavaScriptTypeScriptParser.NEQ, 0); }
		public TerminalNode EQEQEQ() { return getToken(JavaScriptTypeScriptParser.EQEQEQ, 0); }
		public TerminalNode NEQEQ() { return getToken(JavaScriptTypeScriptParser.NEQEQ, 0); }
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitAtom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_atom);
		try {
			setState(380);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(331);
				match(Identifier);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(332);
				match(StringLiteral);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(333);
				match(TemplateLiteral);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(334);
				match(NumberLiteral);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(335);
				match(BooleanLiteral);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(336);
				match(NullLiteral);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(337);
				match(ASYNC);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(338);
				match(FUNCTION);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(339);
				match(CLASS);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(340);
				match(NEW);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(341);
				match(AWAIT);
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(342);
				match(TYPEOF);
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(343);
				arrow();
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(344);
				braceBlock();
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(345);
				parenBlock();
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(346);
				bracketBlock();
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				setState(347);
				match(LT);
				setState(348);
				match(Identifier);
				setState(349);
				match(GT);
				}
				break;
			case 18:
				enterOuterAlt(_localctx, 18);
				{
				setState(350);
				match(LT);
				setState(351);
				match(Identifier);
				setState(352);
				match(SLASH);
				setState(353);
				match(GT);
				}
				break;
			case 19:
				enterOuterAlt(_localctx, 19);
				{
				setState(354);
				match(LT);
				setState(355);
				match(SLASH);
				setState(356);
				match(Identifier);
				setState(357);
				match(GT);
				}
				break;
			case 20:
				enterOuterAlt(_localctx, 20);
				{
				setState(358);
				match(EQ);
				}
				break;
			case 21:
				enterOuterAlt(_localctx, 21);
				{
				setState(359);
				match(ARROW);
				}
				break;
			case 22:
				enterOuterAlt(_localctx, 22);
				{
				setState(360);
				match(DOT);
				}
				break;
			case 23:
				enterOuterAlt(_localctx, 23);
				{
				setState(361);
				match(COMMA);
				}
				break;
			case 24:
				enterOuterAlt(_localctx, 24);
				{
				setState(362);
				match(COLON);
				}
				break;
			case 25:
				enterOuterAlt(_localctx, 25);
				{
				setState(363);
				match(QUESTION);
				}
				break;
			case 26:
				enterOuterAlt(_localctx, 26);
				{
				setState(364);
				match(PLUS);
				}
				break;
			case 27:
				enterOuterAlt(_localctx, 27);
				{
				setState(365);
				match(MINUS);
				}
				break;
			case 28:
				enterOuterAlt(_localctx, 28);
				{
				setState(366);
				match(STAR);
				}
				break;
			case 29:
				enterOuterAlt(_localctx, 29);
				{
				setState(367);
				match(SLASH);
				}
				break;
			case 30:
				enterOuterAlt(_localctx, 30);
				{
				setState(368);
				match(PERCENT);
				}
				break;
			case 31:
				enterOuterAlt(_localctx, 31);
				{
				setState(369);
				match(AMPAMP);
				}
				break;
			case 32:
				enterOuterAlt(_localctx, 32);
				{
				setState(370);
				match(PIPEPIPE);
				}
				break;
			case 33:
				enterOuterAlt(_localctx, 33);
				{
				setState(371);
				match(BANG);
				}
				break;
			case 34:
				enterOuterAlt(_localctx, 34);
				{
				setState(372);
				match(LT);
				}
				break;
			case 35:
				enterOuterAlt(_localctx, 35);
				{
				setState(373);
				match(GT);
				}
				break;
			case 36:
				enterOuterAlt(_localctx, 36);
				{
				setState(374);
				match(LTE);
				}
				break;
			case 37:
				enterOuterAlt(_localctx, 37);
				{
				setState(375);
				match(GTE);
				}
				break;
			case 38:
				enterOuterAlt(_localctx, 38);
				{
				setState(376);
				match(EQEQ);
				}
				break;
			case 39:
				enterOuterAlt(_localctx, 39);
				{
				setState(377);
				match(NEQ);
				}
				break;
			case 40:
				enterOuterAlt(_localctx, 40);
				{
				setState(378);
				match(EQEQEQ);
				}
				break;
			case 41:
				enterOuterAlt(_localctx, 41);
				{
				setState(379);
				match(NEQEQ);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArrowContext extends ParserRuleContext {
		public TerminalNode ARROW() { return getToken(JavaScriptTypeScriptParser.ARROW, 0); }
		public ArrowContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrow; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterArrow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitArrow(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitArrow(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrowContext arrow() throws RecognitionException {
		ArrowContext _localctx = new ArrowContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_arrow);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(382);
			match(ARROW);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BraceBlockContext extends ParserRuleContext {
		public TerminalNode LBRACE() { return getToken(JavaScriptTypeScriptParser.LBRACE, 0); }
		public List<TerminalNode> RBRACE() { return getTokens(JavaScriptTypeScriptParser.RBRACE); }
		public TerminalNode RBRACE(int i) {
			return getToken(JavaScriptTypeScriptParser.RBRACE, i);
		}
		public List<BraceBlockContext> braceBlock() {
			return getRuleContexts(BraceBlockContext.class);
		}
		public BraceBlockContext braceBlock(int i) {
			return getRuleContext(BraceBlockContext.class,i);
		}
		public List<ParenBlockContext> parenBlock() {
			return getRuleContexts(ParenBlockContext.class);
		}
		public ParenBlockContext parenBlock(int i) {
			return getRuleContext(ParenBlockContext.class,i);
		}
		public List<BracketBlockContext> bracketBlock() {
			return getRuleContexts(BracketBlockContext.class);
		}
		public BracketBlockContext bracketBlock(int i) {
			return getRuleContext(BracketBlockContext.class,i);
		}
		public BraceBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_braceBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterBraceBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitBraceBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitBraceBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BraceBlockContext braceBlock() throws RecognitionException {
		BraceBlockContext _localctx = new BraceBlockContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_braceBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(384);
			match(LBRACE);
			setState(391);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 576320014815068158L) != 0)) {
				{
				setState(389);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
				case 1:
					{
					setState(385);
					braceBlock();
					}
					break;
				case 2:
					{
					setState(386);
					parenBlock();
					}
					break;
				case 3:
					{
					setState(387);
					bracketBlock();
					}
					break;
				case 4:
					{
					setState(388);
					_la = _input.LA(1);
					if ( _la <= 0 || (_la==RBRACE) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				}
				setState(393);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(394);
			match(RBRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ParenBlockContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(JavaScriptTypeScriptParser.LPAREN, 0); }
		public List<TerminalNode> RPAREN() { return getTokens(JavaScriptTypeScriptParser.RPAREN); }
		public TerminalNode RPAREN(int i) {
			return getToken(JavaScriptTypeScriptParser.RPAREN, i);
		}
		public List<BraceBlockContext> braceBlock() {
			return getRuleContexts(BraceBlockContext.class);
		}
		public BraceBlockContext braceBlock(int i) {
			return getRuleContext(BraceBlockContext.class,i);
		}
		public List<ParenBlockContext> parenBlock() {
			return getRuleContexts(ParenBlockContext.class);
		}
		public ParenBlockContext parenBlock(int i) {
			return getRuleContext(ParenBlockContext.class,i);
		}
		public List<BracketBlockContext> bracketBlock() {
			return getRuleContexts(BracketBlockContext.class);
		}
		public BracketBlockContext bracketBlock(int i) {
			return getRuleContext(BracketBlockContext.class,i);
		}
		public ParenBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parenBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterParenBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitParenBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitParenBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParenBlockContext parenBlock() throws RecognitionException {
		ParenBlockContext _localctx = new ParenBlockContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_parenBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(396);
			match(LPAREN);
			setState(403);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 575897802350002174L) != 0)) {
				{
				setState(401);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
				case 1:
					{
					setState(397);
					braceBlock();
					}
					break;
				case 2:
					{
					setState(398);
					parenBlock();
					}
					break;
				case 3:
					{
					setState(399);
					bracketBlock();
					}
					break;
				case 4:
					{
					setState(400);
					_la = _input.LA(1);
					if ( _la <= 0 || (_la==RPAREN) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				}
				setState(405);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(406);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BracketBlockContext extends ParserRuleContext {
		public TerminalNode LBRACK() { return getToken(JavaScriptTypeScriptParser.LBRACK, 0); }
		public List<TerminalNode> RBRACK() { return getTokens(JavaScriptTypeScriptParser.RBRACK); }
		public TerminalNode RBRACK(int i) {
			return getToken(JavaScriptTypeScriptParser.RBRACK, i);
		}
		public List<BraceBlockContext> braceBlock() {
			return getRuleContexts(BraceBlockContext.class);
		}
		public BraceBlockContext braceBlock(int i) {
			return getRuleContext(BraceBlockContext.class,i);
		}
		public List<ParenBlockContext> parenBlock() {
			return getRuleContexts(ParenBlockContext.class);
		}
		public ParenBlockContext parenBlock(int i) {
			return getRuleContext(ParenBlockContext.class,i);
		}
		public List<BracketBlockContext> bracketBlock() {
			return getRuleContexts(BracketBlockContext.class);
		}
		public BracketBlockContext bracketBlock(int i) {
			return getRuleContext(BracketBlockContext.class,i);
		}
		public BracketBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bracketBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).enterBracketBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof JavaScriptTypeScriptListener ) ((JavaScriptTypeScriptListener)listener).exitBracketBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof JavaScriptTypeScriptVisitor ) return ((JavaScriptTypeScriptVisitor<? extends T>)visitor).visitBracketBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BracketBlockContext bracketBlock() throws RecognitionException {
		BracketBlockContext _localctx = new BracketBlockContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_bracketBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(408);
			match(LBRACK);
			setState(415);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 574208952489738238L) != 0)) {
				{
				setState(413);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
				case 1:
					{
					setState(409);
					braceBlock();
					}
					break;
				case 2:
					{
					setState(410);
					parenBlock();
					}
					break;
				case 3:
					{
					setState(411);
					bracketBlock();
					}
					break;
				case 4:
					{
					setState(412);
					_la = _input.LA(1);
					if ( _la <= 0 || (_la==RBRACK) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				}
				setState(417);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(418);
			match(RBRACK);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001:\u01a5\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0001\u0000\u0005\u0000"+
		"8\b\u0000\n\u0000\f\u0000;\t\u0000\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0003\u0001G\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0003\u0002L\b\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0003\u0003S\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004[\b\u0004\u0001\u0004"+
		"\u0003\u0004^\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0005\u0005d\b\u0005\n\u0005\f\u0005g\t\u0005\u0001\u0005\u0003\u0005"+
		"j\b\u0005\u0003\u0005l\b\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0003\u0006s\b\u0006\u0001\u0007\u0001\u0007\u0003"+
		"\u0007w\b\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0003\u0007~\b\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0003\u0007\u0084\b\u0007\u0001\u0007\u0003\u0007\u0087\b\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007\u008d\b\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007\u0093\b\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007\u0098\b\u0007\u0003\u0007"+
		"\u009a\b\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0005\b\u00a0\b\b\n\b\f"+
		"\b\u00a3\t\b\u0001\b\u0003\b\u00a6\b\b\u0003\b\u00a8\b\b\u0001\b\u0001"+
		"\b\u0001\t\u0001\t\u0001\t\u0003\t\u00af\b\t\u0001\n\u0003\n\u00b2\b\n"+
		"\u0001\n\u0001\n\u0001\n\u0003\n\u00b7\b\n\u0001\n\u0003\n\u00ba\b\n\u0001"+
		"\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0005\u000b\u00c4\b\u000b\n\u000b\f\u000b\u00c7\t\u000b\u0003"+
		"\u000b\u00c9\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0005"+
		"\u000b\u00cf\b\u000b\n\u000b\f\u000b\u00d2\t\u000b\u0003\u000b\u00d4\b"+
		"\u000b\u0001\f\u0001\f\u0001\f\u0003\f\u00d9\b\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0005\f\u00df\b\f\n\f\f\f\u00e2\t\f\u0003\f\u00e4\b\f\u0001"+
		"\f\u0001\f\u0001\r\u0001\r\u0001\r\u0003\r\u00eb\b\r\u0001\r\u0001\r\u0001"+
		"\r\u0003\r\u00f0\b\r\u0001\u000e\u0003\u000e\u00f3\b\u000e\u0001\u000e"+
		"\u0001\u000e\u0003\u000e\u00f7\b\u000e\u0001\u000e\u0001\u000e\u0003\u000e"+
		"\u00fb\b\u000e\u0001\u000e\u0001\u000e\u0003\u000e\u00ff\b\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u0106"+
		"\b\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u010a\b\u000f\u0001\u000f"+
		"\u0003\u000f\u010d\b\u000f\u0001\u0010\u0001\u0010\u0003\u0010\u0111\b"+
		"\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u0115\b\u0010\u0001\u0011\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0005\u0011\u011b\b\u0011\n\u0011\f\u0011"+
		"\u011e\t\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0005\u0013\u0128\b\u0013\n\u0013"+
		"\f\u0013\u012b\t\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0005\u0014"+
		"\u0130\b\u0014\n\u0014\f\u0014\u0133\t\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0005\u0014\u0139\b\u0014\n\u0014\f\u0014\u013c\t\u0014"+
		"\u0001\u0014\u0001\u0014\u0003\u0014\u0140\b\u0014\u0001\u0014\u0003\u0014"+
		"\u0143\b\u0014\u0001\u0015\u0001\u0015\u0005\u0015\u0147\b\u0015\n\u0015"+
		"\f\u0015\u014a\t\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u017d\b\u0016\u0001\u0017"+
		"\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0005\u0018\u0186\b\u0018\n\u0018\f\u0018\u0189\t\u0018\u0001\u0018\u0001"+
		"\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0005"+
		"\u0019\u0192\b\u0019\n\u0019\f\u0019\u0195\t\u0019\u0001\u0019\u0001\u0019"+
		"\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0005\u001a"+
		"\u019e\b\u001a\n\u001a\f\u001a\u01a1\t\u001a\u0001\u001a\u0001\u001a\u0001"+
		"\u001a\u0000\u0000\u001b\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012"+
		"\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.024\u0000\u0004\u0001\u0000"+
		"\t\u000b\u0001\u0000//\u0001\u000011\u0001\u000033\u01fc\u00009\u0001"+
		"\u0000\u0000\u0000\u0002F\u0001\u0000\u0000\u0000\u0004H\u0001\u0000\u0000"+
		"\u0000\u0006R\u0001\u0000\u0000\u0000\b]\u0001\u0000\u0000\u0000\n_\u0001"+
		"\u0000\u0000\u0000\fo\u0001\u0000\u0000\u0000\u000e\u0099\u0001\u0000"+
		"\u0000\u0000\u0010\u009b\u0001\u0000\u0000\u0000\u0012\u00ab\u0001\u0000"+
		"\u0000\u0000\u0014\u00b1\u0001\u0000\u0000\u0000\u0016\u00d3\u0001\u0000"+
		"\u0000\u0000\u0018\u00d5\u0001\u0000\u0000\u0000\u001a\u00e7\u0001\u0000"+
		"\u0000\u0000\u001c\u00f2\u0001\u0000\u0000\u0000\u001e\u0102\u0001\u0000"+
		"\u0000\u0000 \u0114\u0001\u0000\u0000\u0000\"\u0116\u0001\u0000\u0000"+
		"\u0000$\u0121\u0001\u0000\u0000\u0000&\u0124\u0001\u0000\u0000\u0000("+
		"\u0142\u0001\u0000\u0000\u0000*\u0144\u0001\u0000\u0000\u0000,\u017c\u0001"+
		"\u0000\u0000\u0000.\u017e\u0001\u0000\u0000\u00000\u0180\u0001\u0000\u0000"+
		"\u00002\u018c\u0001\u0000\u0000\u00004\u0198\u0001\u0000\u0000\u00006"+
		"8\u0003\u0002\u0001\u000076\u0001\u0000\u0000\u00008;\u0001\u0000\u0000"+
		"\u000097\u0001\u0000\u0000\u00009:\u0001\u0000\u0000\u0000:<\u0001\u0000"+
		"\u0000\u0000;9\u0001\u0000\u0000\u0000<=\u0005\u0000\u0000\u0001=\u0001"+
		"\u0001\u0000\u0000\u0000>G\u0003\u0004\u0002\u0000?G\u0003\u000e\u0007"+
		"\u0000@G\u0003\u0014\n\u0000AG\u0003\u0018\f\u0000BG\u0003\u001a\r\u0000"+
		"CG\u0003\u001c\u000e\u0000DG\u0003\u001e\u000f\u0000EG\u0003 \u0010\u0000"+
		"F>\u0001\u0000\u0000\u0000F?\u0001\u0000\u0000\u0000F@\u0001\u0000\u0000"+
		"\u0000FA\u0001\u0000\u0000\u0000FB\u0001\u0000\u0000\u0000FC\u0001\u0000"+
		"\u0000\u0000FD\u0001\u0000\u0000\u0000FE\u0001\u0000\u0000\u0000G\u0003"+
		"\u0001\u0000\u0000\u0000HI\u0005\u0001\u0000\u0000IK\u0003\u0006\u0003"+
		"\u0000JL\u0005-\u0000\u0000KJ\u0001\u0000\u0000\u0000KL\u0001\u0000\u0000"+
		"\u0000L\u0005\u0001\u0000\u0000\u0000MS\u00055\u0000\u0000NO\u0003\b\u0004"+
		"\u0000OP\u0005\u0003\u0000\u0000PQ\u00055\u0000\u0000QS\u0001\u0000\u0000"+
		"\u0000RM\u0001\u0000\u0000\u0000RN\u0001\u0000\u0000\u0000S\u0007\u0001"+
		"\u0000\u0000\u0000TU\u0005\"\u0000\u0000UV\u0005\u0004\u0000\u0000V^\u0005"+
		"4\u0000\u0000WZ\u00054\u0000\u0000XY\u0005)\u0000\u0000Y[\u0003\n\u0005"+
		"\u0000ZX\u0001\u0000\u0000\u0000Z[\u0001\u0000\u0000\u0000[^\u0001\u0000"+
		"\u0000\u0000\\^\u0003\n\u0005\u0000]T\u0001\u0000\u0000\u0000]W\u0001"+
		"\u0000\u0000\u0000]\\\u0001\u0000\u0000\u0000^\t\u0001\u0000\u0000\u0000"+
		"_k\u0005.\u0000\u0000`e\u0003\f\u0006\u0000ab\u0005)\u0000\u0000bd\u0003"+
		"\f\u0006\u0000ca\u0001\u0000\u0000\u0000dg\u0001\u0000\u0000\u0000ec\u0001"+
		"\u0000\u0000\u0000ef\u0001\u0000\u0000\u0000fi\u0001\u0000\u0000\u0000"+
		"ge\u0001\u0000\u0000\u0000hj\u0005)\u0000\u0000ih\u0001\u0000\u0000\u0000"+
		"ij\u0001\u0000\u0000\u0000jl\u0001\u0000\u0000\u0000k`\u0001\u0000\u0000"+
		"\u0000kl\u0001\u0000\u0000\u0000lm\u0001\u0000\u0000\u0000mn\u0005/\u0000"+
		"\u0000n\u000b\u0001\u0000\u0000\u0000or\u00054\u0000\u0000pq\u0005\u0004"+
		"\u0000\u0000qs\u00054\u0000\u0000rp\u0001\u0000\u0000\u0000rs\u0001\u0000"+
		"\u0000\u0000s\r\u0001\u0000\u0000\u0000tv\u0005\u0002\u0000\u0000uw\u0005"+
		"\u000e\u0000\u0000vu\u0001\u0000\u0000\u0000vw\u0001\u0000\u0000\u0000"+
		"w}\u0001\u0000\u0000\u0000x~\u0003\u0014\n\u0000y~\u0003\u001c\u000e\u0000"+
		"z~\u0003\u0018\f\u0000{~\u0003\u001a\r\u0000|~\u0003\u001e\u000f\u0000"+
		"}x\u0001\u0000\u0000\u0000}y\u0001\u0000\u0000\u0000}z\u0001\u0000\u0000"+
		"\u0000}{\u0001\u0000\u0000\u0000}|\u0001\u0000\u0000\u0000~\u009a\u0001"+
		"\u0000\u0000\u0000\u007f\u0080\u0005\u0002\u0000\u0000\u0080\u0083\u0003"+
		"\u0010\b\u0000\u0081\u0082\u0005\u0003\u0000\u0000\u0082\u0084\u00055"+
		"\u0000\u0000\u0083\u0081\u0001\u0000\u0000\u0000\u0083\u0084\u0001\u0000"+
		"\u0000\u0000\u0084\u0086\u0001\u0000\u0000\u0000\u0085\u0087\u0005-\u0000"+
		"\u0000\u0086\u0085\u0001\u0000\u0000\u0000\u0086\u0087\u0001\u0000\u0000"+
		"\u0000\u0087\u009a\u0001\u0000\u0000\u0000\u0088\u0089\u0005\u0002\u0000"+
		"\u0000\u0089\u008a\u0005\u000e\u0000\u0000\u008a\u008c\u0003*\u0015\u0000"+
		"\u008b\u008d\u0005-\u0000\u0000\u008c\u008b\u0001\u0000\u0000\u0000\u008c"+
		"\u008d\u0001\u0000\u0000\u0000\u008d\u009a\u0001\u0000\u0000\u0000\u008e"+
		"\u008f\u0005\u0002\u0000\u0000\u008f\u0092\u0005\"\u0000\u0000\u0090\u0091"+
		"\u0005\u0004\u0000\u0000\u0091\u0093\u00054\u0000\u0000\u0092\u0090\u0001"+
		"\u0000\u0000\u0000\u0092\u0093\u0001\u0000\u0000\u0000\u0093\u0094\u0001"+
		"\u0000\u0000\u0000\u0094\u0095\u0005\u0003\u0000\u0000\u0095\u0097\u0005"+
		"5\u0000\u0000\u0096\u0098\u0005-\u0000\u0000\u0097\u0096\u0001\u0000\u0000"+
		"\u0000\u0097\u0098\u0001\u0000\u0000\u0000\u0098\u009a\u0001\u0000\u0000"+
		"\u0000\u0099t\u0001\u0000\u0000\u0000\u0099\u007f\u0001\u0000\u0000\u0000"+
		"\u0099\u0088\u0001\u0000\u0000\u0000\u0099\u008e\u0001\u0000\u0000\u0000"+
		"\u009a\u000f\u0001\u0000\u0000\u0000\u009b\u00a7\u0005.\u0000\u0000\u009c"+
		"\u00a1\u0003\u0012\t\u0000\u009d\u009e\u0005)\u0000\u0000\u009e\u00a0"+
		"\u0003\u0012\t\u0000\u009f\u009d\u0001\u0000\u0000\u0000\u00a0\u00a3\u0001"+
		"\u0000\u0000\u0000\u00a1\u009f\u0001\u0000\u0000\u0000\u00a1\u00a2\u0001"+
		"\u0000\u0000\u0000\u00a2\u00a5\u0001\u0000\u0000\u0000\u00a3\u00a1\u0001"+
		"\u0000\u0000\u0000\u00a4\u00a6\u0005)\u0000\u0000\u00a5\u00a4\u0001\u0000"+
		"\u0000\u0000\u00a5\u00a6\u0001\u0000\u0000\u0000\u00a6\u00a8\u0001\u0000"+
		"\u0000\u0000\u00a7\u009c\u0001\u0000\u0000\u0000\u00a7\u00a8\u0001\u0000"+
		"\u0000\u0000\u00a8\u00a9\u0001\u0000\u0000\u0000\u00a9\u00aa\u0005/\u0000"+
		"\u0000\u00aa\u0011\u0001\u0000\u0000\u0000\u00ab\u00ae\u00054\u0000\u0000"+
		"\u00ac\u00ad\u0005\u0004\u0000\u0000\u00ad\u00af\u00054\u0000\u0000\u00ae"+
		"\u00ac\u0001\u0000\u0000\u0000\u00ae\u00af\u0001\u0000\u0000\u0000\u00af"+
		"\u0013\u0001\u0000\u0000\u0000\u00b0\u00b2\u0005\u0010\u0000\u0000\u00b1"+
		"\u00b0\u0001\u0000\u0000\u0000\u00b1\u00b2\u0001\u0000\u0000\u0000\u00b2"+
		"\u00b3\u0001\u0000\u0000\u0000\u00b3\u00b4\u0005\u0005\u0000\u0000\u00b4"+
		"\u00b6\u00054\u0000\u0000\u00b5\u00b7\u0003\"\u0011\u0000\u00b6\u00b5"+
		"\u0001\u0000\u0000\u0000\u00b6\u00b7\u0001\u0000\u0000\u0000\u00b7\u00b9"+
		"\u0001\u0000\u0000\u0000\u00b8\u00ba\u0003\u0016\u000b\u0000\u00b9\u00b8"+
		"\u0001\u0000\u0000\u0000\u00b9\u00ba\u0001\u0000\u0000\u0000\u00ba\u00bb"+
		"\u0001\u0000\u0000\u0000\u00bb\u00bc\u00030\u0018\u0000\u00bc\u0015\u0001"+
		"\u0000\u0000\u0000\u00bd\u00be\u0005\f\u0000\u0000\u00be\u00c8\u0003("+
		"\u0014\u0000\u00bf\u00c0\u0005\r\u0000\u0000\u00c0\u00c5\u0003(\u0014"+
		"\u0000\u00c1\u00c2\u0005)\u0000\u0000\u00c2\u00c4\u0003(\u0014\u0000\u00c3"+
		"\u00c1\u0001\u0000\u0000\u0000\u00c4\u00c7\u0001\u0000\u0000\u0000\u00c5"+
		"\u00c3\u0001\u0000\u0000\u0000\u00c5\u00c6\u0001\u0000\u0000\u0000\u00c6"+
		"\u00c9\u0001\u0000\u0000\u0000\u00c7\u00c5\u0001\u0000\u0000\u0000\u00c8"+
		"\u00bf\u0001\u0000\u0000\u0000\u00c8\u00c9\u0001\u0000\u0000\u0000\u00c9"+
		"\u00d4\u0001\u0000\u0000\u0000\u00ca\u00cb\u0005\r\u0000\u0000\u00cb\u00d0"+
		"\u0003(\u0014\u0000\u00cc\u00cd\u0005)\u0000\u0000\u00cd\u00cf\u0003("+
		"\u0014\u0000\u00ce\u00cc\u0001\u0000\u0000\u0000\u00cf\u00d2\u0001\u0000"+
		"\u0000\u0000\u00d0\u00ce\u0001\u0000\u0000\u0000\u00d0\u00d1\u0001\u0000"+
		"\u0000\u0000\u00d1\u00d4\u0001\u0000\u0000\u0000\u00d2\u00d0\u0001\u0000"+
		"\u0000\u0000\u00d3\u00bd\u0001\u0000\u0000\u0000\u00d3\u00ca\u0001\u0000"+
		"\u0000\u0000\u00d4\u0017\u0001\u0000\u0000\u0000\u00d5\u00d6\u0005\u0006"+
		"\u0000\u0000\u00d6\u00d8\u00054\u0000\u0000\u00d7\u00d9\u0003\"\u0011"+
		"\u0000\u00d8\u00d7\u0001\u0000\u0000\u0000\u00d8\u00d9\u0001\u0000\u0000"+
		"\u0000\u00d9\u00e3\u0001\u0000\u0000\u0000\u00da\u00db\u0005\f\u0000\u0000"+
		"\u00db\u00e0\u0003(\u0014\u0000\u00dc\u00dd\u0005)\u0000\u0000\u00dd\u00df"+
		"\u0003(\u0014\u0000\u00de\u00dc\u0001\u0000\u0000\u0000\u00df\u00e2\u0001"+
		"\u0000\u0000\u0000\u00e0\u00de\u0001\u0000\u0000\u0000\u00e0\u00e1\u0001"+
		"\u0000\u0000\u0000\u00e1\u00e4\u0001\u0000\u0000\u0000\u00e2\u00e0\u0001"+
		"\u0000\u0000\u0000\u00e3\u00da\u0001\u0000\u0000\u0000\u00e3\u00e4\u0001"+
		"\u0000\u0000\u0000\u00e4\u00e5\u0001\u0000\u0000\u0000\u00e5\u00e6\u0003"+
		"0\u0018\u0000\u00e6\u0019\u0001\u0000\u0000\u0000\u00e7\u00e8\u0005\u0007"+
		"\u0000\u0000\u00e8\u00ea\u00054\u0000\u0000\u00e9\u00eb\u0003\"\u0011"+
		"\u0000\u00ea\u00e9\u0001\u0000\u0000\u0000\u00ea\u00eb\u0001\u0000\u0000"+
		"\u0000\u00eb\u00ec\u0001\u0000\u0000\u0000\u00ec\u00ed\u0005\u001f\u0000"+
		"\u0000\u00ed\u00ef\u0003&\u0013\u0000\u00ee\u00f0\u0005-\u0000\u0000\u00ef"+
		"\u00ee\u0001\u0000\u0000\u0000\u00ef\u00f0\u0001\u0000\u0000\u0000\u00f0"+
		"\u001b\u0001\u0000\u0000\u0000\u00f1\u00f3\u0005\u000f\u0000\u0000\u00f2"+
		"\u00f1\u0001\u0000\u0000\u0000\u00f2\u00f3\u0001\u0000\u0000\u0000\u00f3"+
		"\u00f4\u0001\u0000\u0000\u0000\u00f4\u00f6\u0005\b\u0000\u0000\u00f5\u00f7"+
		"\u0005\"\u0000\u0000\u00f6\u00f5\u0001\u0000\u0000\u0000\u00f6\u00f7\u0001"+
		"\u0000\u0000\u0000\u00f7\u00f8\u0001\u0000\u0000\u0000\u00f8\u00fa\u0005"+
		"4\u0000\u0000\u00f9\u00fb\u0003\"\u0011\u0000\u00fa\u00f9\u0001\u0000"+
		"\u0000\u0000\u00fa\u00fb\u0001\u0000\u0000\u0000\u00fb\u00fc\u0001\u0000"+
		"\u0000\u0000\u00fc\u00fe\u00032\u0019\u0000\u00fd\u00ff\u0003$\u0012\u0000"+
		"\u00fe\u00fd\u0001\u0000\u0000\u0000\u00fe\u00ff\u0001\u0000\u0000\u0000"+
		"\u00ff\u0100\u0001\u0000\u0000\u0000\u0100\u0101\u00030\u0018\u0000\u0101"+
		"\u001d\u0001\u0000\u0000\u0000\u0102\u0103\u0007\u0000\u0000\u0000\u0103"+
		"\u0105\u00054\u0000\u0000\u0104\u0106\u0003$\u0012\u0000\u0105\u0104\u0001"+
		"\u0000\u0000\u0000\u0105\u0106\u0001\u0000\u0000\u0000\u0106\u0109\u0001"+
		"\u0000\u0000\u0000\u0107\u0108\u0005\u001f\u0000\u0000\u0108\u010a\u0003"+
		"*\u0015\u0000\u0109\u0107\u0001\u0000\u0000\u0000\u0109\u010a\u0001\u0000"+
		"\u0000\u0000\u010a\u010c\u0001\u0000\u0000\u0000\u010b\u010d\u0005-\u0000"+
		"\u0000\u010c\u010b\u0001\u0000\u0000\u0000\u010c\u010d\u0001\u0000\u0000"+
		"\u0000\u010d\u001f\u0001\u0000\u0000\u0000\u010e\u0110\u0003*\u0015\u0000"+
		"\u010f\u0111\u0005-\u0000\u0000\u0110\u010f\u0001\u0000\u0000\u0000\u0110"+
		"\u0111\u0001\u0000\u0000\u0000\u0111\u0115\u0001\u0000\u0000\u0000\u0112"+
		"\u0115\u00030\u0018\u0000\u0113\u0115\u0005-\u0000\u0000\u0114\u010e\u0001"+
		"\u0000\u0000\u0000\u0114\u0112\u0001\u0000\u0000\u0000\u0114\u0113\u0001"+
		"\u0000\u0000\u0000\u0115!\u0001\u0000\u0000\u0000\u0116\u0117\u0005+\u0000"+
		"\u0000\u0117\u011c\u00054\u0000\u0000\u0118\u0119\u0005)\u0000\u0000\u0119"+
		"\u011b\u00054\u0000\u0000\u011a\u0118\u0001\u0000\u0000\u0000\u011b\u011e"+
		"\u0001\u0000\u0000\u0000\u011c\u011a\u0001\u0000\u0000\u0000\u011c\u011d"+
		"\u0001\u0000\u0000\u0000\u011d\u011f\u0001\u0000\u0000\u0000\u011e\u011c"+
		"\u0001\u0000\u0000\u0000\u011f\u0120\u0005,\u0000\u0000\u0120#\u0001\u0000"+
		"\u0000\u0000\u0121\u0122\u0005\'\u0000\u0000\u0122\u0123\u0003&\u0013"+
		"\u0000\u0123%\u0001\u0000\u0000\u0000\u0124\u0129\u0003(\u0014\u0000\u0125"+
		"\u0126\u0005*\u0000\u0000\u0126\u0128\u0003(\u0014\u0000\u0127\u0125\u0001"+
		"\u0000\u0000\u0000\u0128\u012b\u0001\u0000\u0000\u0000\u0129\u0127\u0001"+
		"\u0000\u0000\u0000\u0129\u012a\u0001\u0000\u0000\u0000\u012a\'\u0001\u0000"+
		"\u0000\u0000\u012b\u0129\u0001\u0000\u0000\u0000\u012c\u0131\u00054\u0000"+
		"\u0000\u012d\u012e\u0005(\u0000\u0000\u012e\u0130\u00054\u0000\u0000\u012f"+
		"\u012d\u0001\u0000\u0000\u0000\u0130\u0133\u0001\u0000\u0000\u0000\u0131"+
		"\u012f\u0001\u0000\u0000\u0000\u0131\u0132\u0001\u0000\u0000\u0000\u0132"+
		"\u013f\u0001\u0000\u0000\u0000\u0133\u0131\u0001\u0000\u0000\u0000\u0134"+
		"\u0135\u0005+\u0000\u0000\u0135\u013a\u0003&\u0013\u0000\u0136\u0137\u0005"+
		")\u0000\u0000\u0137\u0139\u0003&\u0013\u0000\u0138\u0136\u0001\u0000\u0000"+
		"\u0000\u0139\u013c\u0001\u0000\u0000\u0000\u013a\u0138\u0001\u0000\u0000"+
		"\u0000\u013a\u013b\u0001\u0000\u0000\u0000\u013b\u013d\u0001\u0000\u0000"+
		"\u0000\u013c\u013a\u0001\u0000\u0000\u0000\u013d\u013e\u0005,\u0000\u0000"+
		"\u013e\u0140\u0001\u0000\u0000\u0000\u013f\u0134\u0001\u0000\u0000\u0000"+
		"\u013f\u0140\u0001\u0000\u0000\u0000\u0140\u0143\u0001\u0000\u0000\u0000"+
		"\u0141\u0143\u00055\u0000\u0000\u0142\u012c\u0001\u0000\u0000\u0000\u0142"+
		"\u0141\u0001\u0000\u0000\u0000\u0143)\u0001\u0000\u0000\u0000\u0144\u0148"+
		"\u0003,\u0016\u0000\u0145\u0147\u0003,\u0016\u0000\u0146\u0145\u0001\u0000"+
		"\u0000\u0000\u0147\u014a\u0001\u0000\u0000\u0000\u0148\u0146\u0001\u0000"+
		"\u0000\u0000\u0148\u0149\u0001\u0000\u0000\u0000\u0149+\u0001\u0000\u0000"+
		"\u0000\u014a\u0148\u0001\u0000\u0000\u0000\u014b\u017d\u00054\u0000\u0000"+
		"\u014c\u017d\u00055\u0000\u0000\u014d\u017d\u00056\u0000\u0000\u014e\u017d"+
		"\u00057\u0000\u0000\u014f\u017d\u0005\u0014\u0000\u0000\u0150\u017d\u0005"+
		"\u0015\u0000\u0000\u0151\u017d\u0005\u000f\u0000\u0000\u0152\u017d\u0005"+
		"\b\u0000\u0000\u0153\u017d\u0005\u0005\u0000\u0000\u0154\u017d\u0005\u0011"+
		"\u0000\u0000\u0155\u017d\u0005\u0012\u0000\u0000\u0156\u017d\u0005\u0013"+
		"\u0000\u0000\u0157\u017d\u0003.\u0017\u0000\u0158\u017d\u00030\u0018\u0000"+
		"\u0159\u017d\u00032\u0019\u0000\u015a\u017d\u00034\u001a\u0000\u015b\u015c"+
		"\u0005+\u0000\u0000\u015c\u015d\u00054\u0000\u0000\u015d\u017d\u0005,"+
		"\u0000\u0000\u015e\u015f\u0005+\u0000\u0000\u015f\u0160\u00054\u0000\u0000"+
		"\u0160\u0161\u0005#\u0000\u0000\u0161\u017d\u0005,\u0000\u0000\u0162\u0163"+
		"\u0005+\u0000\u0000\u0163\u0164\u0005#\u0000\u0000\u0164\u0165\u00054"+
		"\u0000\u0000\u0165\u017d\u0005,\u0000\u0000\u0166\u017d\u0005\u001f\u0000"+
		"\u0000\u0167\u017d\u0005\u0016\u0000\u0000\u0168\u017d\u0005(\u0000\u0000"+
		"\u0169\u017d\u0005)\u0000\u0000\u016a\u017d\u0005\'\u0000\u0000\u016b"+
		"\u017d\u0005&\u0000\u0000\u016c\u017d\u0005 \u0000\u0000\u016d\u017d\u0005"+
		"!\u0000\u0000\u016e\u017d\u0005\"\u0000\u0000\u016f\u017d\u0005#\u0000"+
		"\u0000\u0170\u017d\u0005$\u0000\u0000\u0171\u017d\u0005\u001d\u0000\u0000"+
		"\u0172\u017d\u0005\u001e\u0000\u0000\u0173\u017d\u0005%\u0000\u0000\u0174"+
		"\u017d\u0005+\u0000\u0000\u0175\u017d\u0005,\u0000\u0000\u0176\u017d\u0005"+
		"\u001b\u0000\u0000\u0177\u017d\u0005\u001c\u0000\u0000\u0178\u017d\u0005"+
		"\u0019\u0000\u0000\u0179\u017d\u0005\u001a\u0000\u0000\u017a\u017d\u0005"+
		"\u0017\u0000\u0000\u017b\u017d\u0005\u0018\u0000\u0000\u017c\u014b\u0001"+
		"\u0000\u0000\u0000\u017c\u014c\u0001\u0000\u0000\u0000\u017c\u014d\u0001"+
		"\u0000\u0000\u0000\u017c\u014e\u0001\u0000\u0000\u0000\u017c\u014f\u0001"+
		"\u0000\u0000\u0000\u017c\u0150\u0001\u0000\u0000\u0000\u017c\u0151\u0001"+
		"\u0000\u0000\u0000\u017c\u0152\u0001\u0000\u0000\u0000\u017c\u0153\u0001"+
		"\u0000\u0000\u0000\u017c\u0154\u0001\u0000\u0000\u0000\u017c\u0155\u0001"+
		"\u0000\u0000\u0000\u017c\u0156\u0001\u0000\u0000\u0000\u017c\u0157\u0001"+
		"\u0000\u0000\u0000\u017c\u0158\u0001\u0000\u0000\u0000\u017c\u0159\u0001"+
		"\u0000\u0000\u0000\u017c\u015a\u0001\u0000\u0000\u0000\u017c\u015b\u0001"+
		"\u0000\u0000\u0000\u017c\u015e\u0001\u0000\u0000\u0000\u017c\u0162\u0001"+
		"\u0000\u0000\u0000\u017c\u0166\u0001\u0000\u0000\u0000\u017c\u0167\u0001"+
		"\u0000\u0000\u0000\u017c\u0168\u0001\u0000\u0000\u0000\u017c\u0169\u0001"+
		"\u0000\u0000\u0000\u017c\u016a\u0001\u0000\u0000\u0000\u017c\u016b\u0001"+
		"\u0000\u0000\u0000\u017c\u016c\u0001\u0000\u0000\u0000\u017c\u016d\u0001"+
		"\u0000\u0000\u0000\u017c\u016e\u0001\u0000\u0000\u0000\u017c\u016f\u0001"+
		"\u0000\u0000\u0000\u017c\u0170\u0001\u0000\u0000\u0000\u017c\u0171\u0001"+
		"\u0000\u0000\u0000\u017c\u0172\u0001\u0000\u0000\u0000\u017c\u0173\u0001"+
		"\u0000\u0000\u0000\u017c\u0174\u0001\u0000\u0000\u0000\u017c\u0175\u0001"+
		"\u0000\u0000\u0000\u017c\u0176\u0001\u0000\u0000\u0000\u017c\u0177\u0001"+
		"\u0000\u0000\u0000\u017c\u0178\u0001\u0000\u0000\u0000\u017c\u0179\u0001"+
		"\u0000\u0000\u0000\u017c\u017a\u0001\u0000\u0000\u0000\u017c\u017b\u0001"+
		"\u0000\u0000\u0000\u017d-\u0001\u0000\u0000\u0000\u017e\u017f\u0005\u0016"+
		"\u0000\u0000\u017f/\u0001\u0000\u0000\u0000\u0180\u0187\u0005.\u0000\u0000"+
		"\u0181\u0186\u00030\u0018\u0000\u0182\u0186\u00032\u0019\u0000\u0183\u0186"+
		"\u00034\u001a\u0000\u0184\u0186\b\u0001\u0000\u0000\u0185\u0181\u0001"+
		"\u0000\u0000\u0000\u0185\u0182\u0001\u0000\u0000\u0000\u0185\u0183\u0001"+
		"\u0000\u0000\u0000\u0185\u0184\u0001\u0000\u0000\u0000\u0186\u0189\u0001"+
		"\u0000\u0000\u0000\u0187\u0185\u0001\u0000\u0000\u0000\u0187\u0188\u0001"+
		"\u0000\u0000\u0000\u0188\u018a\u0001\u0000\u0000\u0000\u0189\u0187\u0001"+
		"\u0000\u0000\u0000\u018a\u018b\u0005/\u0000\u0000\u018b1\u0001\u0000\u0000"+
		"\u0000\u018c\u0193\u00050\u0000\u0000\u018d\u0192\u00030\u0018\u0000\u018e"+
		"\u0192\u00032\u0019\u0000\u018f\u0192\u00034\u001a\u0000\u0190\u0192\b"+
		"\u0002\u0000\u0000\u0191\u018d\u0001\u0000\u0000\u0000\u0191\u018e\u0001"+
		"\u0000\u0000\u0000\u0191\u018f\u0001\u0000\u0000\u0000\u0191\u0190\u0001"+
		"\u0000\u0000\u0000\u0192\u0195\u0001\u0000\u0000\u0000\u0193\u0191\u0001"+
		"\u0000\u0000\u0000\u0193\u0194\u0001\u0000\u0000\u0000\u0194\u0196\u0001"+
		"\u0000\u0000\u0000\u0195\u0193\u0001\u0000\u0000\u0000\u0196\u0197\u0005"+
		"1\u0000\u0000\u01973\u0001\u0000\u0000\u0000\u0198\u019f\u00052\u0000"+
		"\u0000\u0199\u019e\u00030\u0018\u0000\u019a\u019e\u00032\u0019\u0000\u019b"+
		"\u019e\u00034\u001a\u0000\u019c\u019e\b\u0003\u0000\u0000\u019d\u0199"+
		"\u0001\u0000\u0000\u0000\u019d\u019a\u0001\u0000\u0000\u0000\u019d\u019b"+
		"\u0001\u0000\u0000\u0000\u019d\u019c\u0001\u0000\u0000\u0000\u019e\u01a1"+
		"\u0001\u0000\u0000\u0000\u019f\u019d\u0001\u0000\u0000\u0000\u019f\u01a0"+
		"\u0001\u0000\u0000\u0000\u01a0\u01a2\u0001\u0000\u0000\u0000\u01a1\u019f"+
		"\u0001\u0000\u0000\u0000\u01a2\u01a3\u00053\u0000\u0000\u01a35\u0001\u0000"+
		"\u0000\u000099FKRZ]eikrv}\u0083\u0086\u008c\u0092\u0097\u0099\u00a1\u00a5"+
		"\u00a7\u00ae\u00b1\u00b6\u00b9\u00c5\u00c8\u00d0\u00d3\u00d8\u00e0\u00e3"+
		"\u00ea\u00ef\u00f2\u00f6\u00fa\u00fe\u0105\u0109\u010c\u0110\u0114\u011c"+
		"\u0129\u0131\u013a\u013f\u0142\u0148\u017c\u0185\u0187\u0191\u0193\u019d"+
		"\u019f";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}