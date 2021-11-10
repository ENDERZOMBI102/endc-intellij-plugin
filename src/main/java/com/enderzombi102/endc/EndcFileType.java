package com.enderzombi102.endc;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EndcFileType extends LanguageFileType {
	public static final String FILE_EXTENSION = "endc";
	public static final EndcFileType INSTANCE = new EndcFileType();

	protected EndcFileType() {
		super(EndcLanguage.INSTANCE);
	}

	@NotNull
	@Override
	public String getName() {
		return "EndC file";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "EndC file";
	}

	@NotNull
	@Override
	public String getDefaultExtension() {
		return FILE_EXTENSION;
	}

	@Nullable
	@Override
	public Icon getIcon() {
		return Icons.ENDC_ICON;
	}
}
