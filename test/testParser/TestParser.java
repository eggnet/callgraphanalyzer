package testParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import models.CallGraph;
import models.Clazz;
import models.File;

import org.junit.Test;

import parser.Parser;

public class TestParser {
	@Test
	public void testVisitorVisitPackage() {
		CallGraph cg = new CallGraph();
		Parser parser = new Parser(cg);

		// Single package
		String fileName = "src/test/A.java";
		String rawFile = "package myPackage;\nclass A(){\n}\n";
		parser.parseFileFromString(fileName, rawFile);
		File f = cg.containsFile(fileName);
		assertEquals(f.getFileName(), fileName);
		assertEquals(f.getFilePackage(), "myPackage");

		// Duplicate packages
		rawFile = "package Hahaha;\npackage hohoho;\npackage MuHAHAHA;\nclass A(){}\n";
		parser.parseFileFromString(fileName, rawFile);
		f = cg.containsFile(fileName);
		assertEquals(f.getFilePackage(), "MuHAHAHA");

		// Empty package
		rawFile = "class A(){}\n";
		parser.parseFileFromString(fileName, rawFile);
		f = cg.containsFile(fileName);
		assertEquals(f.getFilePackage(), "");

		// casesensitive packages
		rawFile = "packaGe myPackage;\nclass B(){}\n";
		parser.parseFileFromString(fileName, rawFile);
		f = cg.containsFile(fileName);
		assertEquals(f.getFilePackage(), "");
	}

	@Test
	public void testVisitorVisitImport() {
		CallGraph cg = new CallGraph();
		Parser parser = new Parser(cg);

		// Single import
		String fileName = "src/test/A.java";
		String rawFile = "package myPackage;\nimport models.clazz;\nclass A(){\n}\n";
		parser.parseFileFromString(fileName, rawFile);
		File f = cg.containsFile(fileName);
		List<String> imports = f.getFileImports();

		assertEquals(imports.size(), 1);
		assertEquals(imports.get(0), "models.clazz");

		// multiple imports
		rawFile = "package myPackage;\nimport models.clazz;\n\nimport models.*;import java.util.List;\nimport static org.junit.Assert.*;\nclass B(){\n}\n";
		parser.parseFileFromString(fileName, rawFile);
		f = cg.containsFile(fileName);
		imports = f.getFileImports();

		assertEquals(imports.size(), 4);
		assertTrue(imports.contains("models.clazz"));
		assertTrue(imports.contains("models"));
		assertTrue(imports.contains("java.util.List"));
		assertTrue(imports.contains("org.junit.Assert"));

		// empty import
		rawFile = "package myPackage;\nclass C(){\n}\n";
		parser.parseFileFromString(fileName, rawFile);
		f = cg.containsFile(fileName);
		imports = f.getFileImports();
		assertEquals(imports.size(), 0);
	}

	@Test
	public void testVisitorVisitTypeDeclarationClass() {
		CallGraph cg = new CallGraph();
		Parser parser = new Parser(cg);

		// Single class
		String fileName = "src/test/A.java";
		String rawFile = "package mypackage;\nimport models.clazz;\npublic class classA implements classAbase{\n}\n";
		parser.parseFileFromString(fileName, rawFile);
		File f = cg.containsFile(fileName);
		List<Clazz> classes = f.getFileClazzes();

		assertEquals(classes.size(), 1);
		assertEquals(classes.get(0).getName(), "mypackage.classA");
		assertEquals(classes.get(0).getFile().getFileName(), fileName);

		// Multiple class
		rawFile = "package mypackage;\nimport models.clazz;\nprivate class classB(){\npublic class insideClassC{}\n};\n";
		parser.parseFileFromString(fileName, rawFile);
		f = cg.containsFile(fileName);
		classes = f.getFileClazzes();

		assertEquals(classes.size(), 2);
		boolean classB = false;
		boolean classC = false;
		for (Clazz c : classes) {
			if (c.getName().equals("mypackage.insideClassC"))
				classC = true;
			if (c.getName().equals("mypackage.classB"))
				classB = true;
		}
		assertTrue(classC);
		assertTrue(classB);
	}

}
