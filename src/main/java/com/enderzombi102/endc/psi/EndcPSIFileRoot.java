package com.enderzombi102.endc.psi;

import com.enderzombi102.endc.Icons;
import com.enderzombi102.endc.EndcFileType;
import com.enderzombi102.endc.EndcLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.antlr.intellij.adaptor.SymtabUtils;
import org.antlr.intellij.adaptor.psi.ScopeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EndcPSIFileRoot extends PsiFileBase implements ScopeNode {
    public EndcPSIFileRoot(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, EndcLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return EndcFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "EndC Language file";
    }

    @Override
    public Icon getIcon(int flags) {
        return Icons.ENDC_ICON;
    }

	/**
	 * Return null since a file scope has no enclosing scope. It is not itself in a scope.
	 */
	@Override
	public ScopeNode getContext() {
		return null;
	}

	@Nullable
	@Override
	public PsiElement resolve(PsiNamedElement element) {
		if ( element.getParent() instanceof CallSubtree ) {
			return SymtabUtils.resolve(this, EndcLanguage.INSTANCE,  element, "/script/function/ID");
		}
		return SymtabUtils.resolve(this, EndcLanguage.INSTANCE, element, "/script/vardef/ID");
	}
}
