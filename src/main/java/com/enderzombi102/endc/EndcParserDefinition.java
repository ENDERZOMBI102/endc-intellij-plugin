package com.enderzombi102.endc;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor;
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.intellij.adaptor.lexer.RuleIElementType;
import org.antlr.intellij.adaptor.lexer.TokenIElementType;
import org.antlr.intellij.adaptor.parser.ANTLRParserAdaptor;
import org.antlr.intellij.adaptor.psi.ANTLRPsiNode;
import com.enderzombi102.endc.psi.ArgdefSubtree;
import com.enderzombi102.endc.psi.BlockSubtree;
import com.enderzombi102.endc.psi.CallSubtree;
import com.enderzombi102.endc.psi.FunctionSubtree;
import com.enderzombi102.endc.psi.EndcPSIFileRoot;
import com.enderzombi102.endc.psi.VardefSubtree;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.enderzombi102.endc.parser.EndCParser.*;

public class EndcParserDefinition implements ParserDefinition {
	public static final IFileElementType FILE = new IFileElementType(EndcLanguage.INSTANCE);

	public static TokenIElementType ID;

	static {
		//noinspection deprecation
		PSIElementTypeFactory.defineLanguageIElementTypes(
				EndcLanguage.INSTANCE,
				com.enderzombi102.endc.parser.EndCParser.tokenNames,
				com.enderzombi102.endc.parser.EndCParser.ruleNames
		);
		List<TokenIElementType> tokenIElementTypes = PSIElementTypeFactory.getTokenIElementTypes(EndcLanguage.INSTANCE);
		ID = tokenIElementTypes.get(com.enderzombi102.endc.parser.EndCLexer.ID);
	}

	public static final TokenSet COMMENTS = PSIElementTypeFactory.createTokenSet(
		EndcLanguage.INSTANCE,
		com.enderzombi102.endc.parser.EndCLexer.COMMENT
	);

	public static final TokenSet WHITESPACE = PSIElementTypeFactory.createTokenSet(
		EndcLanguage.INSTANCE,
		com.enderzombi102.endc.parser.EndCLexer.WS
	);

	public static final TokenSet STRING = PSIElementTypeFactory.createTokenSet(
		EndcLanguage.INSTANCE,
		com.enderzombi102.endc.parser.EndCLexer.STRING
	);

	@NotNull
	@Override
	public Lexer createLexer(Project project) {
		com.enderzombi102.endc.parser.EndCLexer lexer = new com.enderzombi102.endc.parser.EndCLexer(null);
		return new ANTLRLexerAdaptor(EndcLanguage.INSTANCE, lexer);
	}

	@NotNull
	public PsiParser createParser(final Project project) {
		final com.enderzombi102.endc.parser.EndCParser parser = new com.enderzombi102.endc.parser.EndCParser(null);
		return new ANTLRParserAdaptor(EndcLanguage.INSTANCE, parser) {
			@Override
			protected ParseTree parse(Parser parser, IElementType root) {
				// start rule depends on root passed in; sometimes we want to create an ID node etc...
				if ( root instanceof IFileElementType ) {
					return ( (com.enderzombi102.endc.parser.EndCParser) parser ).script();
				}
				// let's hope it's an ID as needed by "rename function"
				return ( (com.enderzombi102.endc.parser.EndCParser) parser ).primary();
			}
		};
	}

	/** "Tokens of those types are automatically skipped by PsiBuilder." */
	@NotNull
	public TokenSet getWhitespaceTokens() {
		return WHITESPACE;
	}

	@NotNull
	public TokenSet getCommentTokens() {
		return COMMENTS;
	}

	@NotNull
	public TokenSet getStringLiteralElements() {
		return STRING;
	}

	/** What is the IFileElementType of the root parse tree node? It
	 *  is called from {@link #createFile(FileViewProvider)} at least.
	 */
	@Override
	public @NotNull IFileElementType getFileNodeType() {
		return FILE;
	}

	/** Create the root of your PSI tree (a PsiFile).
	 *
	 *  From IntelliJ IDEA Architectural Overview:
	 *  "A PSI (Program Structure Interface) file is the root of a structure
	 *  representing the contents of a file as a hierarchy of elements
	 *  in a particular programming language."
	 *
	 *  PsiFile is to be distinguished from a FileASTNode, which is a parse
	 *  tree node that eventually becomes a PsiFile. From PsiFile, we can get
	 *  it back via: {@link PsiFile#getNode}.
	 */
	@Override
	public @NotNull PsiFile createFile( @NotNull FileViewProvider viewProvider ) {
		return new EndcPSIFileRoot(viewProvider);
	}

	/** Convert from *NON-LEAF* parse node (AST they call it)
	 *  to PSI node. Leaves are created in the AST factory.
	 *  Rename re-factoring can cause this to be
	 *  called on a TokenIElementType since we want to rename ID nodes.
	 *  In that case, this method is called to create the root node
	 *  but with ID type. Kind of strange, but we can simply create a
	 *  ASTWrapperPsiElement to make everything work correctly.
	 *
	 *  RuleIElementType.  Ah! It's that ID is the root
	 *  IElementType requested to parse, which means that the root
	 *  node returned from parsetree->PSI conversion.  But, it
	 *  must be a CompositeElement! The adaptor calls
	 *  rootMarker.done(root) to finish off the PSI conversion.
	 *  See {@link ANTLRParserAdaptor#parse(IElementType root,
	 *  PsiBuilder)}
	 *
	 *  If you don't care to distinguish PSI nodes by type, it is
	 *  sufficient to create a {@link ANTLRPsiNode} around
	 *  the parse tree node
	 */
	@NotNull
	public PsiElement createElement(ASTNode node) {
		IElementType elType = node.getElementType();
		if ( elType instanceof TokenIElementType ) {
			return new ANTLRPsiNode(node);
		}
		if ( !(elType instanceof RuleIElementType) ) {
			return new ANTLRPsiNode(node);
		}
		RuleIElementType ruleElType = (RuleIElementType) elType;
		switch ( ruleElType.getRuleIndex() ) {
			case RULE_function :
				return new FunctionSubtree(node, elType);
			case RULE_vardef :
				return new VardefSubtree(node, elType);
			case RULE_formal_arg :
				return new ArgdefSubtree(node, elType);
			case RULE_func_block:
				return new BlockSubtree(node);
			case RULE_call_expr :
				return new CallSubtree(node);
			default :
				return new ANTLRPsiNode(node);
		}
	}
}
