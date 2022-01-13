package com.enderzombi102.endc;

import com.enderzombi102.endc.parser.EndCLexer;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor;
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.intellij.adaptor.lexer.TokenIElementType;
import org.jetbrains.annotations.NotNull;

import static com.enderzombi102.endc.parser.EndCLexer.BACK;
import static com.enderzombi102.endc.parser.EndCLexer.IF;
import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

/** A highlighter is really just a mapping from token type to
 *  some text attributes using {@link #getTokenHighlights(IElementType)}.
 *  The reason that it returns an array, TextAttributesKey[], is
 *  that you might want to mix the attributes of a few known highlighters.
 *  A {@link TextAttributesKey} is just a name for that a theme
 *  or IDE skin can set. For example, {@link com.intellij.openapi.editor.DefaultLanguageHighlighterColors#KEYWORD}
 *  is the key that maps to what identifiers look like in the editor.
 *  To change it, see dialog: Editor > Colors & Fonts > Language Defaults.
 *
 *  From <a href="http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/syntax_highlighting_and_error_highlighting.html">doc</a>:
 *  "The mapping of the TextAttributesKey to specific attributes used
 *  in an editor is defined by the EditorColorsScheme class, and can
 *  be configured by the user if the plugin provides an appropriate
 *  configuration interface.
 *  ...
 *  The syntax highlighter returns the {@link TextAttributesKey}
 * instances for each token type which needs special highlighting.
 * For highlighting lexer errors, the standard TextAttributesKey
 * for bad characters HighlighterColors.BAD_CHARACTER can be used."
 */
public class EndcSyntaxHighlighter extends SyntaxHighlighterBase {
	private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];
	public static final TextAttributesKey ID =
		createTextAttributesKey("ENDC_ID", DefaultLanguageHighlighterColors.IDENTIFIER);
	public static final TextAttributesKey KEYWORD =
		createTextAttributesKey("ENDC_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
	public static final TextAttributesKey STRING =
		createTextAttributesKey("ENDC_STRING", DefaultLanguageHighlighterColors.STRING);
	public static final TextAttributesKey BLOCK_COMMENT =
		createTextAttributesKey("ENDC_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);

	static {
		PSIElementTypeFactory.defineLanguageIElementTypes(
				EndcLanguage.INSTANCE,
				com.enderzombi102.endc.parser.EndCParser.tokenNames,
				com.enderzombi102.endc.parser.EndCParser.ruleNames
		);
	}

	@NotNull
	@Override
	public Lexer getHighlightingLexer() {
		com.enderzombi102.endc.parser.EndCLexer lexer = new com.enderzombi102.endc.parser.EndCLexer(null);
		return new ANTLRLexerAdaptor(EndcLanguage.INSTANCE, lexer);
	}

	@NotNull
	@Override
	public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
		if ( !(tokenType instanceof TokenIElementType) ) return EMPTY_KEYS;
		TokenIElementType myType = (TokenIElementType)tokenType;
		int ttype = myType.getANTLRTokenType();
		TextAttributesKey attrKey;
		switch ( ttype ) {
			case EndCLexer.ID:
				attrKey = ID;
				break;
			case EndCLexer.IF:
			case EndCLexer.ELSE:
			case EndCLexer.DECLARE:
			case EndCLexer.CONSTANT:
			case EndCLexer.VARIABLE:
			case EndCLexer.GIVE:
			case BACK:
			case EndCLexer.SUBRUTINE:
			case EndCLexer.CALL:
			case EndCLexer.EXPORT:
			case EndCLexer.TEMPLATE:
			case EndCLexer.BEHAVIOR:
			case EndCLexer.BUILD:
			case EndCLexer.OWN:
			case EndCLexer.FROM:
			case EndCLexer.INITIALIZER:
			case EndCLexer.DEINITIALIZER:
			case EndCLexer.FALSE:
			case EndCLexer.CONSTANTME:
				attrKey = KEYWORD;
				break;
			case EndCLexer.STRING :
				attrKey = STRING;
				break;
			case EndCLexer.COMMENT :
				attrKey = BLOCK_COMMENT;
				break;
			default :
				return EMPTY_KEYS;
		}
		return new TextAttributesKey[] {attrKey};
	}
}
