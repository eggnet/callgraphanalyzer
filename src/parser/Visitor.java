package parser;

import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;

import models.*;

public class Visitor extends ASTVisitor {
	
	private CallGraph callGraph;
	
	private List<Clazz> allClazzes;
	private List<Method> allMethods;
	
	private File file;
	private Stack<Clazz> clazzStack;
	private Clazz currentClazz;
	private Method currentMethod;
	
	public Visitor(CallGraph callGraph, String fileName) {
		this.callGraph = callGraph;
		clazzStack = new Stack<Clazz>();
		
		file = new File();
		file.setFileName(fileName);
	}
	
	/**
	 * This function overrides what to do when the AST visitor 
	 * encounters a package declaration
	 */
	@Override
	public boolean visit(PackageDeclaration node) {
		if (file.getFilePackage() != null)
			System.err.println("ERROR: StructuredFile.java there is more than one package, using first visited");
		else 
			file.setFilePackage(node.getName().getFullyQualifiedName()); 

		return super.visit(node);
	}
	
	/**
	 * This function overrides what to do when the AST visitor
	 * encounters an import declaration
	 */
	@Override
	public boolean visit(ImportDeclaration node) {
		String imp= node.getName().getFullyQualifiedName();
		file.addFileImport(imp);

		return super.visit(node);
	}
	
	/**
	 * This function overrides what to do when the AST visitor
	 * encounters a type declaration (This is mostly when 
	 * you define a class in the Java file)
	 */
	@Override
	public boolean visit(TypeDeclaration node) {
		System.out.println("FOUND CLASS");
		System.out.println(node.getName().getIdentifier());
		currentClazz = callGraph.containsClazz(node.getName().getIdentifier());
		System.out.println(currentClazz);
		if(currentClazz == null) {
			Clazz clazz = new Clazz();
			clazzStack.push(clazz);
			currentClazz = clazz;
		}
		
		currentClazz.setName(node.getName().getIdentifier());
		currentClazz.setInterface(node.isInterface());

		return super.visit(node);
	}
	
	/**
	 * This function overrides what to do when we reach
	 * the end of visiting a class. (Add the class to 
	 * the call graph as we have finished parsing it)
	 */
	@Override
	public void endVisit(TypeDeclaration node) {
		System.out.println("END OF CLASS");
		// Add to call graph
		callGraph.addClazz(clazzStack.peek());
		// Add to file
		file.addClazz(clazzStack.pop());
	}
	
	/**
	 * This function overrides what to do when we reach
	 * a method declaration inside a class.
	 */
	@Override
	public boolean visit(MethodDeclaration node) {
		System.out.println("FOUND METHOD");
		currentMethod = callGraph.containsMethod(node.getName().getIdentifier());
		if(currentMethod == null) {
			Method m = new Method();
			currentMethod = m;
		}
		currentMethod.setName(node.getName().getIdentifier());
		currentMethod.setClazz(currentClazz);

		return super.visit(node);
	}
	
	/**
	 * This function overrides what to do when we reach
	 * a method invocation statement
	 */
	@Override
	public boolean visit(MethodInvocation node) {
		Method testMethod;
		testMethod = callGraph.containsMethod(node.getName().getIdentifier());
		if(testMethod == null) {
			Method m = new Method();
			m.setName(node.getName().getIdentifier());
			testMethod = m;
		}
		
		currentMethod.addMethodCall(testMethod);
		callGraph.addMethod(testMethod);

		return super.visit(node);
	}
	
	/**
	 * This method overrides what to do when we reach
	 * the end of a method declaration
	 */
	@Override
	public void endVisit(MethodDeclaration node) {
		// Add method to the call graph
		callGraph.addMethod(currentMethod);
		// Add method to the current class
		clazzStack.peek().addMethod(currentMethod);
	}
	
	public void commitFile() {
		callGraph.addFile(file);
	}

	public CallGraph getCallGraph() {
		return callGraph;
	}

	public void setCallGraph(CallGraph callGraph) {
		this.callGraph = callGraph;
	}

	public List<Clazz> getAllClazzes() {
		return allClazzes;
	}

	public void setAllClazzes(List<Clazz> allClazzes) {
		this.allClazzes = allClazzes;
	}

	public List<Method> getAllMethods() {
		return allMethods;
	}

	public void setAllMethods(List<Method> allMethods) {
		this.allMethods = allMethods;
	}
	
	

}
