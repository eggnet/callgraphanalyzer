package models;

import java.util.ArrayList;
import java.util.List;

public class Method {
	
	private String		 		name;
	private Clazz 				clazz;
	
	private int					startLine;
	private int					endLine;
	
	private String				returnType;
	
	private List<Method> 		methodCalls;
	private List<Method>		calledBy;
	private List<Exprezzion>	unresolvedExprezzions;
	
	
	public Method() {
		methodCalls = new ArrayList<Method>();
		calledBy = new ArrayList<Method>();
		unresolvedExprezzions = new ArrayList<Exprezzion>();
	}
	
	public Method(String name, Clazz clazz, ArrayList<Method> methodCalls) {
		this.name = name;
		this.clazz = clazz;
		this.methodCalls = methodCalls;
		calledBy = new ArrayList<Method>();
		unresolvedExprezzions = new ArrayList<Exprezzion>();
	}
	
	public void addCalledBy(Method m) {
		if(!calledBy.contains(m))
			this.calledBy.add(m);
	}
	
	public void addMethodCall(Method m) {
		if(!methodCalls.contains(m))
			this.methodCalls.add(m);
	}
	
	public void addUnresolvedExprezzion(String exprezzion, String methodCall, List<Exprezzion> parameters, String resolvedType) {
		Exprezzion e = new Exprezzion(exprezzion, methodCall, parameters, resolvedType);
		this.unresolvedExprezzions.add(e);
	}
	
	public void print() {
		System.out.println("    METHOD: " + name);
		System.out.println("      Calls: ");
		for(Method m: methodCalls)
			System.out.println("        " + m.getName());
		System.out.println("      Unresolved Calls: ");
		for(Exprezzion e: unresolvedExprezzions)
			e.print(2);
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

	public List<Exprezzion> getUnresolvedExprezzions() {
		return unresolvedExprezzions;
	}

	public void setUnresolvedExprezzions(List<Exprezzion> unresolvedExprezzions) {
		this.unresolvedExprezzions = unresolvedExprezzions;
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
}
