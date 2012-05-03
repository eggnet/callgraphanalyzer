package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Method {
	public String name;
	public Clazz clazz;
	public List<Method> methodCalls;
	
	
	public Method() {
		methodCalls = new ArrayList<Method>();
	}
	
	public Method(String name, Clazz clazz, ArrayList<Method> methodCalls) {
		this.name = name;
		this.clazz = clazz;
		this.methodCalls = methodCalls;
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
	public ArrayList<Method> getMethodCalls() {
		return methodCalls;
	}
	public void setMethodCalls(ArrayList<Method> methodCalls) {
		this.methodCalls = methodCalls;
	}

	
}
