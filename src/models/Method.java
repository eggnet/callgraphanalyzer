package models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class Method {
	
	private String		 		name;
	private Clazz 				clazz;
	
	private int					startLine;
	private int					endLine;
	
	private String				returnType;
	
	private List<Method> 		methodCalls;
	private List<Method>		calledBy;
	
	private MethodDeclaration	node;
	
	
	public Method() {
		methodCalls = new ArrayList<Method>();
		calledBy = new ArrayList<Method>();
	}
	
	public Method(String name, Clazz clazz, ArrayList<Method> methodCalls) {
		this.name = name;
		this.clazz = clazz;
		this.methodCalls = methodCalls;
		calledBy = new ArrayList<Method>();
	}
	
	public void addCalledBy(Method m) {
		if(!calledBy.contains(m))
			this.calledBy.add(m);
	}
	
	public void addMethodCall(Method m) {
		if(!methodCalls.contains(m))
			this.methodCalls.add(m);
	}
	
	public void print() {
		System.out.println("    METHOD: " + name);
		System.out.println("      Calls: ");
		for(Method m: methodCalls)
			System.out.println("        " + m.getName());
		System.out.println("      Called By: ");
		for(Method m: calledBy)
			System.out.println("        " + m.getName());
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Clazz getClazz() {
		return clazz;
	}
	public void setClazz(Clazz clazz) {
		this.clazz = clazz;
	}
	public List<Method> getMethodCalls() {
		return methodCalls;
	}
	public void setMethodCalls(ArrayList<Method> methodCalls) {
		this.methodCalls = methodCalls;
	}

	public List<Method> getCalledBy() {
		return calledBy;
	}

	public void setCalledBy(List<Method> calledBy) {
		this.calledBy = calledBy;
	}

	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public MethodDeclaration getNode() {
		return node;
	}

	public void setNode(MethodDeclaration node) {
		this.node = node;
	}
}
