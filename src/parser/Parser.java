package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

import org.eclipse.jdt.core.dom.CompilationUnit;

import models.*;

public class Parser {
	
	public Parser() {
		
	}
	
	/**
	 * This function takes a String file path and will parse
	 * that file using ASTParser
	 * @param file
	 */
	public void parseFile(String file) {
		// Get the file's contents
		String fileData;
		try {
			fileData = readFileToString(file);
		} catch (IOException e) {
			System.err.println("Could not read the file " + file);
			return;
		}
		// Create parse for JRE 1.0 - 1.6
		ASTParser parser= ASTParser.newParser(AST.JLS3);
		// Parser expects pointer to Java file
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(fileData.toCharArray());
		// Setting this causes considerable run time damage
		parser.setResolveBindings(true);
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
	}
	
	private String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			System.out.println(numRead);
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		reader.close();
		return  fileData.toString();	
	}
}
