package models;

import java.util.ArrayList;
import java.util.List;

public class Clazz {
	
	private File				file;
	private String 				name;
	private boolean 			isInterface;
	private List<Method> 		methods;
	private List<Clazz> 		subClazzes;
	
	private List<Clazz> 		interfaces;
	private List<String> 		unresolvedInterfaces;
	
	private Clazz 				superClazz;
	private String 				unresolvedSuperClazz;
	
	private List<Mapping>		variables;
	
	private List<Exprezzion> 	unresolvedExprezzions;

	public Clazz() {
		methods = new ArrayList<Method>();
		interfaces = new ArrayList<Clazz>();
		unresolvedInterfaces = new ArrayList<String>();
		subClazzes = new ArrayList<Clazz>();
		unresolvedSuperClazz = "";
		
		variables = new ArrayList<Mapping>();
		unresolvedExprezzions = new ArrayList<Exprezzion>();
		
	}

	public Clazz(String name, boolean isInterface, List<Method> methods,
			ArrayList<Clazz> interfaces, Clazz superClazz,
			List<Clazz> subClazzes) {
		this.name = name;
		this.isInterface = isInterface;
		this.methods = methods;
		this.interfaces = interfaces;
		this.superClazz = superClazz;
		this.subClazzes = subClazzes;
		
		unresolvedInterfaces = new ArrayList<String>();
		unresolvedSuperClazz = "";
		
		variables = new ArrayList<Mapping>();
		unresolvedExprezzions = new ArrayList<Exprezzion>();
	}
	
	public void print() {
		System.out.println("  CLASS: " + name);
		System.out.println("    Interface: " + isInterface);
		System.out.println("    Implements: ");
		for(Clazz interf: interfaces)
			System.out.println("      " + interf.getName());
		System.out.println("    Unresolved Interfaces: ");
		for(String i: unresolvedInterfaces)
			System.out.println("      " + i);
		System.out.print("    Super Class: ");
		if(superClazz != null)
			System.out.println(superClazz.getName());
		else
			System.out.println("");
		System.out.println("    Unresolved Super Class: " + unresolvedSuperClazz);
		System.out.println("    Sub Classes: ");
		for(Clazz clazz: subClazzes)
			System.out.println("      " + clazz.getName());
		System.out.println("    Fields: ");
		for(Mapping map: variables) 
			System.out.println("      " + map.getType() + ": " + map.getVarName());
		for(Method m: methods)
			m.print();
	}
	
	public void addMethod(Method m) {
		this.methods.add(m);
	}
	
	/**
	 * This function will return a method if it is passed a string such as
	 * A.method() and if the current class is A and contains a method named
	 * method()
	 * @param m
	 * @return
	 */
	public Method hasUnresolvedMethod(String m) {
		String shortM = m;
		shortM = shortM.substring(shortM.lastIndexOf(".")+1);
		for(Method method: methods) {
			String unresolved = method.getName();
			unresolved = unresolved.substring(unresolved.lastIndexOf(".")+1);
			if(unresolved.equals(shortM))
				return method;
		}
		return null;
	}
	
	public void addUnresolvedInterface(String i) {
		this.unresolvedInterfaces.add(i);
	}
	
	public void removeUnresolvedInterface(String i) {
		this.unresolvedInterfaces.remove(i);
	}
	
	public void addUnresolvedSuperClazz(String s) {
		this.unresolvedSuperClazz = s;
	}
	
	public void removeUnresolvedSuperClazz() {
		this.unresolvedSuperClazz = "";
	}
	
	public void addInterface(Clazz clazz) {
		this.interfaces.add(clazz);
	}
	
	public void addSubClazz(Clazz clazz) {
		this.subClazzes.add(clazz);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isInterface() {
		return isInterface;
	}

	public void setInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}

	public List<Method> getMethods() {
		return methods;
	}

	public void setMethods(List<Method> methods) {
		this.methods = methods;
	}

	public List<Clazz> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(ArrayList<Clazz> interfaces) {
		this.interfaces = interfaces;
	}

	public Clazz getSuperClazz() {
		return superClazz;
	}

	public void setSuperClazz(Clazz superClazz) {
		this.superClazz = superClazz;
	}

	public List<Clazz> getSubClazzes() {
		return subClazzes;
	}

	public void setSubClazzes(List<Clazz> subClazzes) {
		this.subClazzes = subClazzes;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getUnresolvedSuperClazz() {
		return unresolvedSuperClazz;
	}

	public void setUnresolvedSuperClazz(String unresolvedSuperClazz) {
		this.unresolvedSuperClazz = unresolvedSuperClazz;
	}

	public List<String> getUnresolvedInterfaces() {
		return unresolvedInterfaces;
	}

	public void setUnresolvedInterfaces(List<String> unresolvedInterfaces) {
		this.unresolvedInterfaces = unresolvedInterfaces;
	}

	public List<Mapping> getVariables() {
		return variables;
	}

	public void setVariables(List<Mapping> variables) {
		this.variables = variables;
	}

	public List<Exprezzion> getUnresolvedExprezzions() {
		return unresolvedExprezzions;
	}

	public void setUnresolvedExprezzions(List<Exprezzion> unresolvedExprezzions) {
		this.unresolvedExprezzions = unresolvedExprezzions;
	}
	
}

