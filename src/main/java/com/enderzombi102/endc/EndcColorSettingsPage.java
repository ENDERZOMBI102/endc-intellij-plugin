package com.enderzombi102.endc;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class EndcColorSettingsPage implements ColorSettingsPage {
	private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
		new AttributesDescriptor("Identifier", EndcSyntaxHighlighter.ID),
		new AttributesDescriptor("Keyword", EndcSyntaxHighlighter.KEYWORD),
		new AttributesDescriptor("String", EndcSyntaxHighlighter.STRING),
		new AttributesDescriptor("Block comment", EndcSyntaxHighlighter.BLOCK_COMMENT),
	};

	@Nullable
	@Override
	public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
		return null;
	}

	@Nullable
	@Override
	public Icon getIcon() {
		return Icons.ENDC_ICON;
	}

	@NotNull
	@Override
	public SyntaxHighlighter getHighlighter() {
		return new EndcSyntaxHighlighter();
	}

	@NotNull
	@Override
	public String getDemoText() {
		return
			"|* block comment\n" +
			"*|\n" +
			"DCLAR SUBRITIN f{ StRiNg somTxt____ } [\n" +
			"   CALL printto{ STDOUT. somTxt____ }\n" +
			"]\n";
	}

	@NotNull
	@Override
	public AttributesDescriptor[] getAttributeDescriptors() {
		return DESCRIPTORS;
	}

	@NotNull
	@Override
	public ColorDescriptor[] getColorDescriptors() {
		return ColorDescriptor.EMPTY_ARRAY;
	}

	@NotNull
	@Override
	public String getDisplayName() {
		return "EndC";
	}
}
