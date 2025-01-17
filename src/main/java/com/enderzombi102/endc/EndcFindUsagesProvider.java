package com.enderzombi102.endc;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import org.antlr.intellij.adaptor.lexer.RuleIElementType;
import org.antlr.intellij.adaptor.psi.ANTLRPsiNode;
import com.enderzombi102.endc.psi.FunctionSubtree;
import com.enderzombi102.endc.psi.IdentifierPSINode;
import com.enderzombi102.endc.psi.VardefSubtree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.enderzombi102.endc.parser.EndCParser.RULE_call_expr;
import static com.enderzombi102.endc.parser.EndCParser.RULE_expr;
import static com.enderzombi102.endc.parser.EndCParser.RULE_formal_arg;
import static com.enderzombi102.endc.parser.EndCParser.RULE_function;
import static com.enderzombi102.endc.parser.EndCParser.RULE_primary;
import static com.enderzombi102.endc.parser.EndCParser.RULE_statement;
import static com.enderzombi102.endc.parser.EndCParser.RULE_vardef;

public class EndcFindUsagesProvider implements FindUsagesProvider {
	/** Is "find usages" meaningful for a kind of definition subtree? */
	@Override
	public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
		return psiElement instanceof IdentifierPSINode || // the case where we highlight the ID in def subtree itself
			   psiElement instanceof FunctionSubtree ||   // remaining cases are for resolve() results
			   psiElement instanceof VardefSubtree;
	}

	@Nullable
	@Override
	public WordsScanner getWordsScanner() {
		return null; // null implies use SimpleWordScanner default
	}

	@Nullable
	@Override
	public String getHelpId(@NotNull PsiElement psiElement) {
		return null;
	}

	/** What kind of thing is the ID node? Can group by in "Find Usages" dialog */
	@NotNull
	@Override
	public String getType(PsiElement element) {
		// The parent of an ID node will be a RuleIElementType:
		// function, vardef, formal_arg, statement, expr, call_expr, primary
		ANTLRPsiNode parent = (ANTLRPsiNode) element.getParent();
		RuleIElementType elType = (RuleIElementType) parent.getNode().getElementType();
		switch ( elType.getRuleIndex() ) {
			case RULE_function :
			case RULE_call_expr :
				return "function";
			case RULE_vardef :
			case RULE_statement :
			case RULE_expr :
			case RULE_primary :
				return "variable";
			case RULE_formal_arg :
				return "parameter";
			default:
				return "";
		}
	}

	@NotNull
	@Override
	public String getDescriptiveName(PsiElement element) {
		return element.getText();
	}

	@NotNull
	@Override
	public String getNodeText(PsiElement element, boolean useFullName) {
		return element.getText();
	}
}
