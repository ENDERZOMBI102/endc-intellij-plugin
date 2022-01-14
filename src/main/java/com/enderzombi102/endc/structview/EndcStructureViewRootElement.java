package com.enderzombi102.endc.structview;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class EndcStructureViewRootElement extends EndcStructureViewElement {
	public EndcStructureViewRootElement(PsiFile element) {
		super(element);
	}

	@NotNull
	@Override
	public ItemPresentation getPresentation() {
		return new EndcRootPresentation( (PsiFile) element );
	}
}
