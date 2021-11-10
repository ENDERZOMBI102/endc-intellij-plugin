package com.enderzombi102.endc.adaptor.test;

import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.testFramework.ParsingTestCase;
import org.antlr.intellij.adaptor.xpath.XPath;
import com.enderzombi102.endc.EndcLanguage;
import com.enderzombi102.endc.EndcParserDefinition;

import java.util.Collection;

public class TestXPath extends ParsingTestCase {
	public TestXPath() {
		super( "", "endc", new EndcParserDefinition() );
	}

	public void testSingleVarDef() throws Exception {
		String code = "DCLAR CONSTANT x_________= 1";
		String output = code;
		String xpath = "/script/statement";
		checkXPathResults(code, xpath, output);
	}

	public void testMultiVarDef() throws Exception {
		String code =
			"DCLAR CONSTANT x________ = 1\n" +
			"DCLAR CONSTANT y________ = ( 1, 2, 3 )\n";
		String output = code;
		String xpath = "/script/statement";
		checkXPathResults(code, xpath, output);
	}

	public void testFuncNames() throws Exception {
		String code = loadFile("test/com/enderzombi102/endc/adaptor/test/hello_world.endc");
		String output = "hello world!";
		String xpath = "/script/function/ID";
		checkXPathResults(code, xpath, output);
	}

	public void testAllIDs() throws Exception {
		String code = loadFile("test/com/enderzombi102/endc/adaptor/test/anonymusFunc.endc");
		String output = "";
		String xpath = "//ID";
		checkXPathResults(code, xpath, output);
	}

	public void testAnyVarDef() throws Exception {
		String code = loadFile("test/org/antlr/jetbrains/adaptor/test/test.sample");
		String output =
			"var y = x\n"+
			"var z = 9";
		String xpath = "//vardef";
		checkXPathResults(code, xpath, output);
	}

	public void testVarDefIDs() throws Exception {
		String code = loadFile("test/org/antlr/jetbrains/adaptor/test/test.sample");
		String output =
			"y\n" +
			"z";
		String xpath = "//vardef/ID";
		checkXPathResults(code, xpath, output);
	}

	public void testAllVarDefIDsInScopes() throws Exception {
		String code = loadFile("test/org/antlr/jetbrains/adaptor/test/bubblesort.sample");
		String output =
			"x\n"+
			"i\n"+
			"j\n"+
			"swap\n"+
			"x";
		String xpath = "//block/statement/vardef/ID";
		checkXPathResults(code, xpath, output);
	}

	public void testTopLevelVarDefIDsInScopes() throws Exception {
		String code = loadFile("test/org/antlr/jetbrains/adaptor/test/bubblesort.sample");
		String output =
			"x\n"+
			"i";
		String xpath = "//function/block/statement/vardef/ID";
		checkXPathResults(code, xpath, output);
	}

	public void testRuleUnderWildcard() throws Exception {
		String code = loadFile("test/org/antlr/jetbrains/adaptor/test/test.sample");
		String output =
			"var y = x\n"+
			"x\n"+
			"[\n"+
			"1\n"+
			"]\n"+
			"=\n"+
			"\"sdflkjsdf\"\n"+
			"return\n"+
			"false;";
		String xpath = "//function/*/statement/*";
		checkXPathResults(code, xpath, output);
	}

	public void testAllNonWhileTokens() throws Exception {
		String code = loadFile("test/org/antlr/jetbrains/adaptor/test/bubblesort.sample");
		String output =
			"(\n"+
			")\n"+
			"return";
		String xpath = "/script/function/block/statement/!'while'";
		checkXPathResults(code, xpath, output);
	}

	public void testGetNestedIf() throws Exception {
		String code = loadFile("test/org/antlr/jetbrains/adaptor/test/bubblesort.sample");
		String output =
			"if";
		String xpath = "//'if'";
		checkXPathResults(code, xpath, output);
	}

	public void testWildcardUnderFuncThenJustTokens() throws Exception {
		String code = loadFile("test/org/antlr/jetbrains/adaptor/test/test.sample");
		String output =
			"func\n"+
			"f\n"+
			"(\n"+
			")\n"+
			"func\n"+
			"g\n"+
			"(\n"+
			")\n"+
			"func\n"+
			"h\n"+
			"(\n"+
			")\n"+
			":";
		String xpath = "//function/*";
		myFile = createPsiFile("a", code);
		ensureParsed(myFile);
		assertEquals(code, myFile.getText());
		final StringBuilder buf = new StringBuilder();
		Collection<? extends PsiElement> nodes = XPath.findAll(EndcLanguage.INSTANCE, myFile, xpath);
		for (PsiElement n : nodes) {
			if ( n instanceof LeafPsiElement ) {
				buf.append(n.getText());
				buf.append("\n");
			}
		}
		assertEquals(output.trim(), buf.toString().trim());
	}

	// S U P P O R T

	protected void checkXPathResults(String code, String xpath, String allNodesText) {
		checkXPathResults(EndcLanguage.INSTANCE, code, xpath, allNodesText);
	}

	protected void checkXPathResults(Language language, String code, String xpath, String allNodesText) {
		myFile = createPsiFile("a", code);
		ensureParsed( myFile );
		assertEquals( code, myFile.getText() );
		Collection<? extends PsiElement> nodes = XPath.findAll( language, myFile, xpath );
		StringBuilder buf = new StringBuilder();
		for (PsiElement t : nodes) {
			buf.append( t.getText() );
			buf.append( "\n" );
		}
		assertEquals(allNodesText.trim(), buf.toString().trim());
	}

	@Override
	protected String getTestDataPath() {
		return ".";
	}

	@Override
	protected boolean skipSpaces() {
		return false;
	}

	@Override
	protected boolean includeRanges() {
		return true;
	}
}
