package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Method {
	
	private String		 	name;
	private Clazz 			clazz;
	private List<Method> 	methodCalls;
	
	
	public Method() {
		methodCalls = new ArrayList<Method>();
	}
	
	public Method(String name, Clazz clazz, ArrayList<Method> methodCalls) {
		this.name = name;
		this.clazz = clazz;
		this.methodCalls = methodCalls;
	}
	
	public void addMethodCall(Method m) {
		if(!methodCalls.contains(m))
			this.methodCalls.add(m);
	}
	
	public void print() {
		
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
}
