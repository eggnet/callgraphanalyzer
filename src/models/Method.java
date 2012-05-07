package models;

import java.util.ArrayList;
import java.util.List;

public class Method {
	
	private String		 	name;
	private Clazz 			clazz;
	
	private List<Method> 	methodCalls;
	private List<Method>	calledBy;
	private List<String>	unresolvedMethods;
	
	
	public Method() {
		methodCalls = new ArrayList<Method>();
		calledBy = new ArrayList<Method>();
		unresolvedMethods = new ArrayList<String>();
	}
	
	public Method(String name, Clazz clazz, ArrayList<Method> methodCalls) {
		this.name = name;
		this.clazz = clazz;
		this.methodCalls = methodCalls;
		calledBy = new ArrayList<Method>();
		unresolvedMethods = new ArrayList<String>();
	}
	
	public void addCalledBy(Method m) {
		if(!calledBy.contains(m))
			this.calledBy.add(m);
	}
	
	public void addMethodCall(Method m) {
		if(!methodCalls.contains(m))
			this.methodCalls.add(m);
	}
	
	public void addUnresolvedMethod(String m) {
		if(!unresolvedMethods.contains(m))
			this.unresolvedMethods.add(m);
	}
	
	public void removeUnresolvedMethod(String m) {
		if(unresolvedMethods.contains(m))
			this.unresolvedMethods.remove(m);
	}
	
	public void print() {
		System.out.println("    METHOD: " + name);
		System.out.println("      Calls: ");
		for(Method m: methodCalls)
			System.out.println("        " + m.getName());
		System.out.println("      Unresolved Calls: ");
		for(String m: unresolvedMethods)
			System.out.println("        " + m);
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

	public List<String> getUnresolvedMethods() {
		return unresolvedMethods;
	}

	public void setUnresolvedMethods(List<String> unresolvedMethods) {
		this.unresolvedMethods = unresolvedMethods;
	}

	public List<Method> getCalledBy() {
		return calledBy;
	}

	public void setCalledBy(List<Method> calledBy) {
		this.calledBy = calledBy;
	}
	
	
	
}
