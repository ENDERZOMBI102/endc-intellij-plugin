package com.enderzombi102.endc.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/** A reference object associated with (referring to) a IdentifierPSINode
 *  underneath a call_expr rule subtree root.
 */
public class FunctionRef extends EndcElementRef {
	public FunctionRef(@NotNull IdentifierPSINode element) {
		super(element);
	}

	@Override
	public boolean isDefSubtree(PsiElement def) {
		return def instanceof FunctionSubtree;
	}
}
