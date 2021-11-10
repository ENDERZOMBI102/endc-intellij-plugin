package com.enderzombi102.endc.structview;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiFile;
import com.enderzombi102.endc.Icons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EndcRootPresentation implements ItemPresentation {
	protected final PsiFile element;

	protected EndcRootPresentation(PsiFile element) {
		this.element = element;
	}

	@Nullable
	@Override
	public Icon getIcon(boolean unused) {
		return Icons.ENDC_ICON;
	}

	@Nullable
	@Override
	public String getPresentableText() {
		return element.getVirtualFile().getNameWithoutExtension();
	}

	@Nullable
	@Override
	public String getLocationString() {
		return null;
	}
}
