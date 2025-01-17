package com.enderzombi102.endc.psi;

import com.enderzombi102.endc.EndcLanguage;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.antlr.intellij.adaptor.SymtabUtils;
import org.antlr.intellij.adaptor.psi.ANTLRPsiNode;
import org.antlr.intellij.adaptor.psi.ScopeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockSubtree extends ANTLRPsiNode implements ScopeNode {
	public BlockSubtree(@NotNull ASTNode node) {
		super(node);
	}

	@Nullable
	@Override
	public PsiElement resolve(PsiNamedElement element) {
		return SymtabUtils.resolve(
				this,
				EndcLanguage.INSTANCE,
				element,
				"/block/vardef/ID"
		);
	}
}
