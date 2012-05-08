package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import models.CallGraph;
import models.Clazz;
import models.File;
import models.Mapping;
import models.Method;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import callgraphanalyzer.Mappings;

public class Visitor extends ASTVisitor {
	
	private CallGraph callGraph;
	
	private File file;
	private Stack<Clazz> clazzStack;
	private Clazz currentClazz;
	private Method currentMethod;
	private Mappings mappings = new Mappings();
	
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
		currentClazz = callGraph.containsClazz(file.getFilePackage() + "." + node.getName().getIdentifier());
		if(currentClazz == null) {
			Clazz clazz = new Clazz();
			clazzStack.push(clazz);
			currentClazz = clazz;
		}
		
		currentClazz.setName(file.getFilePackage() + "." + node.getName().getIdentifier());
		currentClazz.setInterface(node.isInterface());
		
		Type t = node.getSuperclassType();
		if(t != null)
			currentClazz.addUnresolvedSuperClazz(t.toString());
		
		List<Type> interfaces = node.superInterfaceTypes();
		for(Type i: interfaces)
			currentClazz.addUnresolvedInterface(i.toString());

		return super.visit(node);
	}
	
	/**
	 * This function overrides what to do when we reach
	 * the end of visiting a class. (Add the class to 
	 * the call graph as we have finished parsing it)
	 */
	@Override
	public void endVisit(TypeDeclaration node) {
		// Add it to the current file
		clazzStack.peek().setFile(file);
		// Add to call graph
		callGraph.addClazz(clazzStack.peek());
		// Add to file
		// Add to file - interfaces if needed
		if(clazzStack.peek().isInterface())
			file.addInterface(clazzStack.peek());
		file.addClazz(clazzStack.pop());
	}
	
	/**
	 * This function overrides what to do when we reach
	 * a method declaration inside a class.
	 */
	@Override
	public boolean visit(MethodDeclaration node) {
		// Get the method's parameter
		List<SingleVariableDeclaration> parametersList =  node.parameters();
		List<String> parameters = new ArrayList<String>();
		for(SingleVariableDeclaration v: parametersList) {
			parameters.add(v.getType().toString());
		}
		
		// Get the unique name
		String uniqueMethod = currentClazz.getName() + "." + node.getName().getIdentifier()+ "(";
		for(String s: parameters)
			uniqueMethod += s + ", ";
		if(!parameters.isEmpty())
			uniqueMethod = uniqueMethod.substring(0, uniqueMethod.length()-2);
		uniqueMethod += ")";
		
		currentMethod = callGraph.containsMethod(uniqueMethod);
		if(currentMethod == null) {
			Method m = new Method();
			currentMethod = m;
		}
		currentMethod.setName(uniqueMethod);
		currentMethod.setClazz(currentClazz);

		return super.visit(node);
	}
	
	/**
	 * This function overrides what to do when we reach
	 * a method invocation statement
	 */
	@Override
	public boolean visit(MethodInvocation node) {
		// Get the method's parameter
		List<Type> parametersList =  node.typeArguments();
		List<String> parameters = new ArrayList<String>();
		for(Type t: parametersList) {
			parameters.add(t.toString());
		}
		
		// Get the stub name
		String stubName = node.getName().getIdentifier() + "(";
		for(String s: parameters)
			stubName += s + ", ";
		if(!parameters.isEmpty())
			stubName = stubName.substring(0, stubName.length()-2);
		stubName += ")";
		Expression exp = node.getExpression();
		currentMethod.addUnresolvedMethod(stubName);

		return super.visit(node);
	}
	
	/**
	 * This function overrides what to do when we reach
	 * a class instance creation statement
	 */
	@Override
	public boolean visit(ClassInstanceCreation node) {
		// Get the constructor's parameters
		List<Type> parametersList =  node.typeArguments();
		List<String> parameters = new ArrayList<String>();
		for(Type t: parametersList) {
			parameters.add(t.toString());
		}
		
		// Get the stub name
		String stubName = node.getType().toString()  + "(";
		for(String s: parameters)
			stubName += s + ", ";
		if(!parameters.isEmpty())
			stubName = stubName.substring(0, stubName.length()-2);
		stubName += ")";
		
		currentMethod.addUnresolvedMethod(stubName);
		
		
		return super.visit(node);
	}
	@Override
	public boolean visit(VariableDeclarationExpression node)
	{
		SimpleName varName = null;
		for (Iterator<VariableDeclarationFragment> i = node.fragments().iterator();i.hasNext();)
		{
			VariableDeclarationFragment frag = (VariableDeclarationFragment)i.next();
			varName = frag.getName();
		}
		Mapping m = new Mapping(node.getType().toString(), varName.getFullyQualifiedName());
		mappings.addMapping(node.fragments().get(0).toString(), m);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(VariableDeclarationStatement node)
	{
		SimpleName varName = null;
		for (Iterator<VariableDeclarationFragment> i = node.fragments().iterator();i.hasNext();)
		{
			VariableDeclarationFragment frag = (VariableDeclarationFragment)i.next();
			varName = frag.getName();
		}
		Mapping m = new Mapping(node.getType().toString(), varName.getFullyQualifiedName());
		mappings.addMapping(varName.getFullyQualifiedName(), m);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(Block node)
	{
		//TODO @braden create a new Mapping List
		mappings.newMap();
		return super.visit(node);
	}
	
	@Override 
	public void endVisit(Block node)
	{
		//TODO @braden
		mappings.removeMap();
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
}
