//package parser;

import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
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
	
	public Visitor(CallGraph callGraph) {
		this.callGraph = callGraph;
		clazzStack = new Stack<Clazz>();
		
		file = new File();
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
		currentClazz = callGraph.containsClazz(node.getName().getIdentifier());
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
	public void endVisit(TypeDeclarationStatement node) {
		callGraph.addClazz(clazzStack.pop());
	}
	
	/**
	 * This function overrides what to do when we reach
	 * a method declaration inside a class.
	 */
	@Override
	public boolean visit(MethodDeclaration node) {
		Method m = new Method();
		m.setName(node.getName().getIdentifier());
		m.setClazz(currentClazz);
		m.clazz= cl;			
		m.methodCalls= new ArrayList<Method>();
		m.astNode= node;

		currentMethod= m;

		return super.visit(node);
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
