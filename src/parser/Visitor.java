package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import models.CallGraph;
import models.Clazz;
import models.Exprezzion;
import models.File;
import models.Method;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class Visitor extends ASTVisitor {
	
	private CallGraph callGraph;
	
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
		// Check to see if there is an expression out front of the invocation
		String exp;
		String methodCall;
		List<Exprezzion> parameters;
		String resolvedType = "";
		if(node.getExpression() != null)
		{
			System.out.println("-----------------");
			System.out.println("Expression: " + node.getExpression().toString());
			System.out.println("Is a Name.");
			exp = node.getExpression().toString();
			methodCall = node.getName().getIdentifier();
			System.out.println("Method: " + node.getName().getIdentifier());
			parameters = parseMethodParameters(node);
			
			currentMethod.addUnresolvedExprezzion(exp, methodCall, parameters, resolvedType);
			
			if(node.getExpression() instanceof Name) {
				exp = node.getExpression().toString();
				// resolvedType = lookUp(exp);
				currentMethod.addUnresolvedExprezzion(exp, "", new ArrayList<Exprezzion>(), resolvedType);
			}
		}
		else
		{
			// This means we have a local function call
			System.out.println("-----------------");
			System.out.println("Method: " + node.getName().getIdentifier());
			exp = "";
			methodCall = node.getName().getIdentifier();
			parameters = parseMethodParameters(node);
			
			currentMethod.addUnresolvedExprezzion(exp, methodCall, parameters, resolvedType);
		}
		

		return super.visit(node);
	}
	
	
	public List<Exprezzion> parseMethodParameters(MethodInvocation node) {
		List<Exprezzion> exprezzions = new ArrayList<Exprezzion>();
		
		// Temp variables
		String exp;
		String methodCall;
		List<Exprezzion> parameters;
		String resolvedType = "";
		
		List<Expression> expressions = node.arguments();
		int paramNum = 0;
		for(Expression expression: expressions) {
			// 3 Cases
			if(expression instanceof Name)
			{
				exp = expression.toString();
				// exp = lookUp(((Name)node.getExpression()).toString());
				methodCall = "";
				parameters = new ArrayList<Exprezzion>();
			}
			else if(expression instanceof MethodInvocation) {
				// Handle if the method invocation has an expression
				if(((MethodInvocation)expression).getExpression() != null) {
					exp = ((MethodInvocation)expression).getExpression().toString();
				}
				else
					exp = "";
				methodCall = ((MethodInvocation)expression).getName().getIdentifier();
				parameters = parseMethodParameters(((MethodInvocation)expression));
			}
			else {
				// The parameter must be some kind of literal
				exp = expression.toString();
				methodCall = "";
				parameters = new ArrayList<Exprezzion>();
				resolvedType = resolveLiteralType(expression);
			}
			// Add the new parameter
			exprezzions.add(new Exprezzion(exp, methodCall, parameters, resolvedType));
			System.out.println("Argument: " + expression.toString());
			paramNum++;
		}
		
		return exprezzions;
	}
	
	private String resolveLiteralType(Expression expression) {
		String literal = "";
		
		if(expression instanceof BooleanLiteral)
			literal = "boolean";
		else if(expression instanceof CharacterLiteral)
			literal = "char";
		else if(expression instanceof NullLiteral)
			literal = "null";
		else if(expression instanceof NumberLiteral)
		{
			// TODO Need to figure out if it is a float/double/int/whatever
			// HARD.
			literal = "None Supported Type";
		}
		else if(expression instanceof StringLiteral)
			literal = "String";
		
		
		
		return literal;
	}
	
	/**
	 * This function overrides what to do when we reach
	 * a class instance creation statement
	 */
	@Override
	public boolean visit(ClassInstanceCreation node) {
		// SHOULD BE SAME LOGIC AS WHAT TO DO WHEN WE FIND METHOD INVOCATION
		
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
}
