package parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.StringLiteral;

import models.*;

public class Resolver {
	
	CallGraph callGraph;
	
	public Resolver(CallGraph callGraph) {
		this.callGraph = callGraph;
	}
	
	public void resolveAll() {
		resolveClazzes();
		resolveMethods();
	}
	
	public boolean resolveMethods() {
		for(File file: callGraph.getAllFiles()) {
			resolveFileMethodCalls(file);
		}
		return true;
	}
	
	public boolean resolveFileMethodCalls(File file) {
		for(Clazz clazz: file.getFileClazzes()) {
			resolveAllClazzMethodCalls(file, clazz);
		}
		return true;
	}
	
	public boolean resolveAllClazzMethodCalls(File file, Clazz clazz) {
		for(Method m: clazz.getMethods()) {
			resolveMethodCalls(file, clazz, m);
		}
		return true;
	}
	
	public boolean resolveMethodCalls(File file, Clazz clazz, Method method) {
		RVisitor rvisitor = new RVisitor(this, callGraph, clazz, method);
		method.getNode().accept(rvisitor);
		
		return true;
	}
	
	/**
	 * This function will return a string that corresponds to the
	 * type of the literal in the expression it is passed.
	 * @param expression
	 * @return
	 */
	private String resolveLiteralType(Expression expression) {
		String literal = "";
		
		if(expression instanceof BooleanLiteral)
			literal = "boolean";
		else if(expression instanceof CharacterLiteral)
			literal = "char";
		else if(expression instanceof NullLiteral)
			literal = "null";
		else if(expression instanceof NumberLiteral || expression instanceof CastExpression)
			literal = resolveNumberLiteral(expression);
		else if(expression instanceof StringLiteral)
			literal = "String";
		
		return literal;
	}
	
	private String resolveNumberLiteral(Expression expression) {
		
		if(expression instanceof CastExpression) {
			return ((CastExpression)expression).getType().toString();
		}
		
		NumberLiteral number = (NumberLiteral)expression;
		if(number.getToken().contains("F") || number.getToken().contains("f"))
			return "float";
		if(number.getToken().contains("D") || number.getToken().contains("d") || 
				number.getToken().contains("E") || number.getToken().contains("e"))
			return "double";
		if(number.getToken().contains("L") || number.getToken().contains("l"))
			return "long";
		if(!number.getToken().contains("."))
			return "int";
		else
			return "double";
	}
	
	public boolean resolveClazzes() {
		for(Clazz clazz: callGraph.getAllClazzes()) {
			// TODO This function needs to be redone
			//resolveClazz(clazz);
		}
		return true;
	}
	
	/**
	 * This return a list of all files that are in the
	 * supplied package name.
	 * @param pkg
	 * @return
	 */
	public List<File> getFilesInPackage(String pkg) {
		List<File> files = new ArrayList<File>();
		
		for(File file: callGraph.getAllFiles()) {
			if(file.getFilePackage().equals(pkg))
				files.add(file);
		}
		
		return files;
	}

	public CallGraph getCallGraph() {
		return callGraph;
	}

	public void setCallGraph(CallGraph callGraph) {
		this.callGraph = callGraph;
	}
}
